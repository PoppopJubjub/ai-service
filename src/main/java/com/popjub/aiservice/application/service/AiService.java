package com.popjub.aiservice.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.databind.JsonNode;
import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.domain.entity.Ai;
import com.popjub.aiservice.domain.repository.AiRepository;
import com.popjub.aiservice.infrastructure.client.GeminiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.infrastructure.dto.response.GeminiResponse;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

	private final AiRepository aiRepository;
	private final GeminiClient geminiClient;
	private final ObjectMapper objectMapper;

	public AiResult check(AiCommand command) {
		try {
			//호출
			GeminiResponse geminiRes = geminiClient.requestModerationWithPrompt(command.text());
			//확인용 원본
			String raw = geminiClient.requestRaw(command.text());
			log.info("RAW RESPONSE = {}", raw);
			// Gemini 응답 → GeminiResDto 파싱
			AiResult result = parseGeminiResponse(geminiRes);
			// 원본 저장
			Ai ai = new Ai(
				command.reviewId(),
				command.text(),
				geminiRes.toString()
			);
			aiRepository.save(ai);

			return result;
		}catch (Exception e) {
			log.error("AI 검사 중 오류 발생", e);
			return AiResult.safe();
			}
		}

	// Gemini 응답 파싱
	private AiResult parseGeminiResponse(GeminiResponse response) {
		try {
			// 1. 응답에서 텍스트 추출
			if (response.candidates() == null || response.candidates().isEmpty()) {
				log.warn("Gemini 응답에 candidates가 없음");
				return AiResult.safe();
			}

			var candidate = response.candidates().get(0);
			var content = candidate.content();

			if (content == null || content.parts() == null || content.parts().isEmpty()) {
				log.warn("Gemini 응답에 content가 없음");
				return AiResult.safe();
			}

			String textResponse = content.parts().get(0).text();
			log.info("Gemini 텍스트 응답: {}", textResponse);

			// 2. JSON 파싱 (```json ``` 제거)
			String cleanJson = textResponse
				.replaceAll("```json", "")
				.replaceAll("```", "")
				.trim();

			JsonNode json = objectMapper.readTree(cleanJson);

			// 3. JSON에서 필드 추출
			boolean isAbusive = json.get("isAbusive").asBoolean();
			double confidence = json.get("confidence").asDouble();
			String category = json.get("category").asText();
			String reason = json.get("reason").asText();

			// 4. SafetyRatings도 함께 수집
			List<String> safetyLabels = candidate.safetyRatings() != null
				? candidate.safetyRatings().stream()
				.map(GeminiResponse.SafetyRating::category)
				.toList()
				: List.of();

			return new AiResult(isAbusive, confidence, category, reason, safetyLabels);

		} catch (Exception e) {
			log.error("Gemini 응답 파싱 실패", e);
			return AiResult.safe();
		}
	}
}