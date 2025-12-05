package com.popjub.aiservice.presentation.dto.response;

import java.util.List;

public record AiCheckResponse(
	boolean isAbusive,
	String score,
	List<String> labels
) {}
