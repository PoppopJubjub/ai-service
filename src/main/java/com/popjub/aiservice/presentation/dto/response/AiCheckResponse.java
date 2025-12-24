package com.popjub.aiservice.presentation.dto.response;

import java.util.List;

public record AiCheckResponse(
	boolean isAbusive,
	double confidence,
	String category,
	String reason,
	List<String> safetyLabels
) {}
