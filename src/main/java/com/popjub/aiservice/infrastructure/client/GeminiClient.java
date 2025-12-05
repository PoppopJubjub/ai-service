package com.popjub.aiservice.infrastructure.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import org.springframework.web.reactive.function.client.WebClient;

import com.popjub.aiservice.infrastructure.dto.request.GeminiRequest;
import com.popjub.aiservice.infrastructure.dto.response.GeminiResponse;

@Component
@RequiredArgsConstructor
public class GeminiClient {

	private final WebClient geminiWebClient;

	@Value("${gemini.url}")
	private String geminiUrl;

	@Value("${gemini.api-key}")
	private String apiKey;

	public GeminiResponse requestModeration(String text) {

		GeminiRequest body = GeminiRequest.from(text);

		return geminiWebClient.post()
			.uri(uriBuilder -> uriBuilder
				.path(geminiUrl)
				.queryParam("key", apiKey)
				.build())
			.bodyValue(body)
			.retrieve()
			.bodyToMono(GeminiResponse.class)
			.block();
	}

	public GeminiResponse requestModerationWithPrompt(String text) {
		GeminiRequest body = GeminiRequest.forModerationCheck(text);

		return geminiWebClient.post()
			.uri(uriBuilder -> uriBuilder
				.path(geminiUrl)
				.queryParam("key", apiKey)
				.build())
			.bodyValue(body)
			.retrieve()
			.bodyToMono(GeminiResponse.class)
			.block();
	}

	public String requestRaw(String text) {
		GeminiRequest body = GeminiRequest.from(text);

		return geminiWebClient.post()
			.uri(uriBuilder -> uriBuilder
				.path(geminiUrl)
				.queryParam("key", apiKey)
				.build())
			.bodyValue(body)
			.retrieve()
			.bodyToMono(String.class)  // JSON 원본
			.block();
	}
}