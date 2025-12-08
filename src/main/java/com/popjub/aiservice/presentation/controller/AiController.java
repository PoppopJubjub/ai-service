package com.popjub.aiservice.presentation.controller;

import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.application.mapper.AiMapper;
import com.popjub.aiservice.application.service.AiService;
import com.popjub.aiservice.presentation.dto.request.AiCheckRequest;
import com.popjub.aiservice.presentation.dto.response.AiCheckResponse;

import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/internal/ai")
public class AiController {

	private final AiService aiService;
	private final AiMapper aiMapper;

	@PostMapping("/review/check")
	public AiCheckResponse check(@Validated @RequestBody AiCheckRequest request) {

		AiCommand command = aiMapper.toCommand(request);

		AiResult result = aiService.check(command);

		return aiMapper.toResponse(result);
	}
}
