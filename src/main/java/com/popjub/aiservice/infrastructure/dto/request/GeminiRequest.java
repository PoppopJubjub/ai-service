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

	public record Content(List<Part> parts) {}
	public record Part(String text) {}
}
