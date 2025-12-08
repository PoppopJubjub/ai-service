package com.popjub.aiservice.application.dto.result;

import java.util.List;

public record AiResult(
	boolean isAbusive,
	double  confidence,
	String category,
	String reason,
	List<String> safetyLabels
) {
	public static AiResult safe() {
		return new AiResult(false, 0.0, "정상", "검사 실패", List.of());
	}
}
