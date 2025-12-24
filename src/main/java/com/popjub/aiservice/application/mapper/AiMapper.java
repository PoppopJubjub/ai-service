package com.popjub.aiservice.application.mapper;

import java.util.UUID;

import org.springframework.stereotype.Component;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.exception.AiErrorCode;
import com.popjub.aiservice.presentation.dto.request.AiCheckRequest;
import com.popjub.aiservice.presentation.dto.response.AiCheckResponse;

@Component
public class AiMapper {
	public AiCommand toCommand(AiCheckRequest request) {
		if (request == null) {
			throw new AiCustomException(AiErrorCode.INVALID_REQUEST_BODY);
		}

		UUID reviewId;
		try {
			reviewId = UUID.fromString(request.reviewId());
		} catch (Exception e) {
			throw new AiCustomException(AiErrorCode.INVALID_REVIEW_ID_FORMAT);
		}

		return new AiCommand(
			reviewId,
			request.text()
		);
	}

	public AiCheckResponse toResponse(AiResult result) {
		if (result == null) {
			throw new AiCustomException(AiErrorCode.INVALID_RESPONSE);
		}

		return new AiCheckResponse(
			result.isAbusive(),
			result.confidence(),
			result.category(),
			result.reason(),
			result.safetyLabels()
		);
	}
}
