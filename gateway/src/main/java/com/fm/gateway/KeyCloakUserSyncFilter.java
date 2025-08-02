package com.fm.gateway;

import com.fm.gateway.Service.RegisterRequest;
import com.fm.gateway.Service.UserService;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

@Component
@Slf4j
@RequiredArgsConstructor
public class KeyCloakUserSyncFilter implements WebFilter {

    private final UserService userService;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String userId = exchange.getRequest().getHeaders().getFirst("X-User-ID");
        String token = exchange.getRequest().getHeaders().getFirst("Authorization");

        log.info("Processing request with userId: {}, token: {}", userId, token != null ? "present" : "null");

        if (token == null || token.isEmpty()) {
            log.error("Authorization token is missing");
            return Mono.error(new ResponseStatusException(HttpStatus.UNAUTHORIZED, "Missing Authorization token"));
        }

        RegisterRequest registerRequest = getUserDetails(token);
        if (registerRequest == null) {
            log.error("Failed to parse user details from token");
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Invalid or malformed JWT token"));
        }

        if (userId == null || userId.isEmpty()) {
            userId = registerRequest.getKeyCloakId();
            log.info("No X-User-ID provided, using keyCloakId from token: {}", userId);
        }

        if (userId == null || userId.isEmpty()) {
            log.error("User ID is missing from both header and token");
            return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "Missing user ID"));
        }

        String finalUserId = userId;
        return userService.validateUserId(userId)
                .flatMap(exists -> {
                    if (!exists) {
                        log.info("User {} does not exist, registering new user with email: {}", finalUserId, registerRequest.getEmail());
                        return userService.registeUser(registerRequest)
                                .doOnSuccess(response -> log.info("User {} registered successfully", finalUserId))
                                .doOnError(e -> log.error("User registration failed for userId {}: {}", finalUserId, e.getMessage()))
                                .thenReturn(true);
                    } else {
                        log.info("User {} already exists, skipping registration", finalUserId);
                        return Mono.just(true);
                    }
                })
                .switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Unexpected error during user validation")))
                .flatMap(success -> {
                    ServerHttpRequest mutated = exchange.getRequest().mutate()
                            .header("X-USER-ID", finalUserId)
                            .build();
                    return chain.filter(exchange.mutate().request(mutated).build());
                })
                .onErrorResume(e -> {
                    log.error("Error in user sync filter for userId {}: {}", finalUserId, e.getMessage());
                    if (e instanceof ResponseStatusException) {
                        return Mono.error(e);
                    }
                    return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR, "Failed to process user sync: " + e.getMessage()));
                });
    }

    private RegisterRequest getUserDetails(String token) {
        try {
            String tokenWithoutBearer = token.replace("Bearer ", "").trim();
            SignedJWT signedJWT = SignedJWT.parse(tokenWithoutBearer);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
            String email = claimsSet.getStringClaim("email");
            String keyCloakId = claimsSet.getStringClaim("sub");
            String firstName = claimsSet.getStringClaim("given_name");
            String lastName = claimsSet.getStringClaim("family_name");

            if (email == null || keyCloakId == null) {
                log.error("Missing required claims in JWT: email={}, sub={}", email, keyCloakId);
                return null;
            }

            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setEmail(email);
            registerRequest.setKeyCloakId(keyCloakId);
            registerRequest.setFirstname(firstName);
            registerRequest.setLastname(lastName);
            // Password not set as it's not required for Keycloak-managed users
            log.info("Parsed user details: email={}, keyCloakId={}, firstName={}, lastName={}",
                    email, keyCloakId, firstName, lastName);
            return registerRequest;
        } catch (Exception e) {
            log.error("Failed to parse JWT token: {}", e.getMessage());
            return null;
        }
    }
}