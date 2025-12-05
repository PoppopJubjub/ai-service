package com.popjub.aiservice.application.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.presentation.dto.request.AiCheckRequest;
import com.popjub.aiservice.presentation.dto.response.AiCheckResponse;

@Component
public class AiMapper {
	public AiCommand toCommand(AiCheckRequest request) {
		if (request == null) {
			throw new IllegalArgumentException("AiCheckRequest is null");
		}

		return new AiCommand(
			UUID.fromString(request.reviewId()),
			request.text()
		);
	}

	public AiCheckResponse toResponse(AiResult result) {
		if (result == null) {
			throw new IllegalArgumentException("AiResult is null");
		}

		return new AiCheckResponse(
			result.isAbusive(),
			result.score(),
			result.labels()
		);
	}
}
