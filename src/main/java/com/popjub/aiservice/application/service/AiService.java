package com.popjub.aiservice.application.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import java.util.Map;
import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.domain.entity.Ai;
import com.popjub.aiservice.domain.repository.AiRepository;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.exception.AiErrorCode;
import com.popjub.aiservice.application.port.ModelClient;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class AiService {

	private final AiRepository aiRepository;
	private final Map<String, ModelClient> modelClients;
	private final ObjectMapper objectMapper;

	@Value("${ai.model}")
	private String activeModel;

	public AiResult check(AiCommand command) {

		validateCommand(command);

		ModelClient modelClient = modelClients.get(activeModel);
		if (modelClient == null) {
			throw new AiCustomException(AiErrorCode.UNSUPPORTED_MODEL);
		}

		try {
			// Gemini 응답 → GeminiResDto 파싱
			AiResult result = modelClient.check(command);
			String rawResult = objectMapper.writeValueAsString(result);

			Ai ai = new Ai(
				command.reviewId(),
				command.text(),
				rawResult
			);
			aiRepository.save(ai);

			return result;

		} catch (AiCustomException e) {
			throw e; // 그대로 전파
		} catch (Exception e) {
			throw new AiCustomException(AiErrorCode.GEMINI_PARSE_FAIL);
		}
	}

	private void validateCommand(AiCommand command) {
		if (command.reviewId() == null) {
			throw new AiCustomException(AiErrorCode.INVALID_REVIEW_ID);
		}
		if (command.text() == null || command.text().isBlank()) {
			throw new AiCustomException(AiErrorCode.EMPTY_TEXT);
		}
	}
}