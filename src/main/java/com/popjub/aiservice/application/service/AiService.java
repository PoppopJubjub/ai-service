package com.popjub.aiservice.application.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.domain.entity.Ai;
import com.popjub.aiservice.domain.repository.AiRepository;
import com.popjub.aiservice.infrastructure.client.GeminiClient;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.infrastructure.dto.response.GeminiResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AiService {

	private final AiRepository aiRepository;
	private final GeminiClient geminiClient;

	public AiResult check(AiCommand command) {
		//호출
		GeminiResponse geminiRes = geminiClient.requestModeration(command.text());
		//확인용 원본
		String raw = geminiClient.requestRaw(command.text());
		System.out.println("🔥 RAW RESPONSE = " + raw);
		// Gemini 응답 → GeminiResDto 파싱
		AiResult result = convertToResult(geminiRes);

		Ai ai = new Ai(
			command.reviewId(),
			command.text(),
			geminiRes.toString() // 원본 저장
		);
		aiRepository.save(ai);

		return result;
	}

	private AiResult convertToResult(GeminiResponse res) {

		// 1) candidates.*.safetyRatings
		if (res.candidates() == null || res.candidates().isEmpty()) {
			return new AiResult(false, "LOW", List.of());
		}

		// 첫 번째 candidate
		var candidate = res.candidates().get(0);

		List<GeminiResponse.SafetyRating> ratings = candidate.safetyRatings();

		// safetyRatings 도 없으면 LOW 처리
		if (ratings == null || ratings.isEmpty()) {
			return new AiResult(false, "LOW", List.of());
		}

		// HIGH 포함 여부로 abusive 판단
		boolean abusive = ratings.stream()
			.anyMatch(r -> "HIGH".equalsIgnoreCase(r.probability()));

		// 카테고리 목록
		List<String> labels = ratings.stream()
			.map(GeminiResponse.SafetyRating::category)
			.toList();

		// HIGH > MEDIUM > LOW 순서로 가장 높은 위험도 선택
		String score = ratings.stream()
			.map(GeminiResponse.SafetyRating::probability)
			.sorted((a, b) -> List.of("HIGH", "MEDIUM", "LOW")
				.indexOf(a) - List.of("HIGH", "MEDIUM", "LOW").indexOf(b))
			.findFirst()
			.orElse("LOW");

		return new AiResult(
			abusive,
			score,
			labels
		);
	}
}