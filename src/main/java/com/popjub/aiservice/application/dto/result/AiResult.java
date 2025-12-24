package com.popjub.aiservice.application.dto.result;

import java.util.List;

public record AiResult(
	boolean isAbusive,
	double  confidence, //신뢰도
	String category, //카테고리
	String reason,	// 이유
	List<String> safetyLabels
) {
	public static AiResult safe() {
		return new AiResult(false, 0.0, "정상", "검사 실패", List.of());
	}
}
