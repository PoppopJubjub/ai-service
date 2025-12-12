package com.popjub.aiservice.infrastructure.client;

import static com.popjub.aiservice.exception.AiErrorCode.*;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.web.reactive.function.client.WebClient;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.application.port.ModelClient;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.infrastructure.dto.request.GeminiRequest;
import com.popjub.aiservice.infrastructure.dto.response.GeminiResponse;

@Component
@RequiredArgsConstructor
@Slf4j
public class GeminiModelClient implements ModelClient {

	private final WebClient geminiWebClient;
	private final ObjectMapper objectMapper;

	@Value("${gemini.url}")
	private String geminiUrl;

	@Value("${gemini.api-key}")
	private String apiKey;

	@Override
	public AiResult check(AiCommand command) {

		GeminiRequest body = GeminiRequest.forModerationCheck(command.text());

		GeminiResponse response = geminiWebClient.post()
			.uri(uriBuilder -> uriBuilder
				.path(geminiUrl)
				.queryParam("key", apiKey)
				.build())
			.bodyValue(body)
			.retrieve()
			.bodyToMono(GeminiResponse.class)
			.block();
		log.info("[GeminiModelClient] Raw Response = {}", response);
		if (response == null) {
			throw new AiCustomException(GEMINI_API_FAIL);
		}

		return toAiResult(response);

	}

	private AiResult toAiResult(GeminiResponse response) {
		try {
			if (response.candidates() == null || response.candidates().isEmpty()) {
				throw new AiCustomException(GEMINI_PARSE_FAIL);
			}

			var candidate = response.candidates().get(0);

			if (candidate.content() == null || candidate.content().parts() == null || candidate.content().parts().isEmpty()) {
				throw new AiCustomException(GEMINI_PARSE_FAIL);
			}

			var part = candidate.content().parts().get(0).text();

			String cleanJson = part
				.replaceAll("```json", "")
				.replaceAll("```", "")
				.trim();
			var json = objectMapper.readTree(cleanJson);

			List<String> safetyLabels =
				(candidate.safetyRatings() == null)
					? List.of()
					: candidate.safetyRatings().stream()
					.map(GeminiResponse.SafetyRating::category)
					.toList();

			return new AiResult(
				json.get("isAbusive").asBoolean(),
				json.get("confidence").asDouble(),
				json.get("category").asText(),
				json.get("reason").asText(),
				safetyLabels
			);
		} catch (Exception e) {
			throw new AiCustomException(GEMINI_PARSE_FAIL);
		}
	}

}