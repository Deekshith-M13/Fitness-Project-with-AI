package com.fm.gateway.Service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final WebClient userServiceWebClient;

    public Mono<Boolean> validateUserId(String userId) {
        log.info("Validating userId: {}", userId);
        return userServiceWebClient.get()
                .uri("/api/user/{userId}/validate", userId)
                .retrieve()
                .bodyToMono(Boolean.class)
                .onErrorResume(WebClientResponseException.class, e -> {
                    if (e.getStatusCode() == HttpStatus.NOT_FOUND) {
                        log.info("User {} not found", userId);
                        return Mono.just(false);
                    } else {
                        log.error("Error validating userId {}: Status {}, Response {}",
                                userId, e.getStatusCode(), e.getResponseBodyAsString());
                        return Mono.error(new ResponseStatusException(e.getStatusCode(),
                                "Failed to validate user: " + e.getResponseBodyAsString()));
                    }
                })
                .doOnSuccess(exists -> log.info("User {} exists: {}", userId, exists));
    }

    public Mono<UserResponse> registeUser(RegisterRequest request) {
        log.info("Registering user with email: {}, keyCloakId: {}", request.getEmail(), request.getKeyCloakId());
        return userServiceWebClient.post()
                .uri("/api/user/register")
                .bodyValue(request)
                .retrieve()
                .bodyToMono(UserResponse.class)
                .doOnSuccess(response -> log.info("User registration successful for email: {}", request.getEmail()))
                .onErrorResume(WebClientResponseException.class, e -> {
                    log.error("User registration failed for email {}: Status {}, Response {}",
                            request.getEmail(), e.getStatusCode(), e.getResponseBodyAsString());
                    return Mono.error(new ResponseStatusException(e.getStatusCode(),
                            "User registration failed: " + e.getResponseBodyAsString()));
                });
    }
}