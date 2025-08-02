package com.fm.activityservice.Service;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserValidationService {

    private final WebClient userServiceWebClient;

    public boolean validateUserId(String userid) {
        try {
            Boolean exists = userServiceWebClient.get()
                    .uri("/api/user/{userId}/validate", userid)
                    .retrieve()
                    .bodyToMono(Boolean.class)
                    .block();

            if (Boolean.TRUE.equals(exists)) {
                return true;
            }

            // If not exists, create the user using fallback logic
            userServiceWebClient.get()
                    .uri("/api/user/create-if-not-exists/{userId}", userid)
                    .retrieve()
                    .bodyToMono(Void.class)
                    .block();

            return true;

        } catch (WebClientResponseException e) {
            throw new ResponseStatusException(
                    e.getStatusCode(),
                    "Error during user validation or creation: " + e.getResponseBodyAsString(),
                    e
            );
        } catch (Exception e) {
            throw new ResponseStatusException(
                    HttpStatus.INTERNAL_SERVER_ERROR,
                    "Unexpected error during user validation",
                    e
            );
        }
    }
}
