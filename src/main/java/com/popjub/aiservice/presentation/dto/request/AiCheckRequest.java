package com.popjub.aiservice.presentation.dto.request;

import jakarta.validation.constraints.Pattern;

public record AiCheckRequest(
	@Pattern(regexp = "^[0-9a-fA-F-]{36}$")
	String reviewId,
	String text
) {}