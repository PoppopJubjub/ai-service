package com.popjub.aiservice.infrastructure.dto.request;

import java.util.List;

public record GeminiRequest(
	List<Content> contents
) {

	public static GeminiRequest from(String text) {
		return new GeminiRequest(
			List.of(new Content(
				List.of(new Part(text))
			))
		);
	}
	public static GeminiRequest forModerationCheck(String text) {
		String prompt = String.format("""
			다음 댓글을 분석하여 악성 여부를 판단하세요.
			악성 댓글 기준: 욕설, 비속어, 혐오 표현, 인신공격, 성희롱, 차별적 발언
			
			댓글: "%s"
			
			반드시 아래 JSON 형식으로만 답변하고, 다른 설명은 추가하지 마세요:
			{"isAbusive": true, "confidence": 0.95, "category": "욕설", "reason": "부적절한 표현 포함"}
			또는
			{"isAbusive": false, "confidence": 0.98, "category": "정상", "reason": "문제없는 댓글"}
			""", text);

		return new GeminiRequest(
			List.of(new Content(
				List.of(new Part(prompt))
			))
		);
	}

	public record Content(List<Part> parts) {}

	public record Part(String text) {}
}
