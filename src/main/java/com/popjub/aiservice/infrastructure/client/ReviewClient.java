package com.popjub.aiservice.infrastructure.client;

import java.util.Map;
import java.util.UUID;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class ReviewClient {

	private final WebClient reviewWebClient;

	public void updateBlind(UUID reviewId, boolean blind) {
		reviewWebClient.patch()
			.uri("/internal/reviews/{id}/blind", reviewId)
			.bodyValue(Map.of("blind", blind))
			.retrieve()
			.bodyToMono(Void.class)
			.block();
	}
}
