package com.shopsphere.gateway.client;


import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import com.shopsphere.gateway.dto.TokenResponse;

import reactor.core.publisher.Mono;

@Component
public class AuthClient {

    private final WebClient webClient;

    public AuthClient(WebClient.Builder builder) {
        this.webClient = builder.baseUrl("http://AUTH-SERVICE").build();
    }

    public Mono<TokenResponse> refreshToken(String refreshToken) {

        return webClient.post()
                .uri("/api/auth/refresh")
                .header("Cookie", "refreshToken=" + refreshToken)
                .header("X-Internal-Request", "true")  
                .retrieve()
                .bodyToMono(TokenResponse.class);
    }
}