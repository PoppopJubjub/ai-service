package com.popjub.aiservice.application.dto.result;

import java.util.List;

public record AiResult(
	boolean isAbusive,
	String score,
	List<String> labels
) {}
