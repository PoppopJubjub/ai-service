package com.popjub.aiservice.infrastructure.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.application.event.ReviewCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.application.service.AiService;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.exception.AiErrorCode;
import com.popjub.aiservice.infrastructure.client.ReviewClient;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventConsumer {

	private final ObjectMapper objectMapper;
	private final AiService aiService;
	private final ReviewClient reviewClient;

	@KafkaListener(topics = "review.created", groupId = "ai-service-group")
	public void consume(String message) {
		try {
			ReviewCreateEvent event = objectMapper.readValue(message, ReviewCreateEvent.class);

			if (event.getReviewId() == null || event.getContent() == null) {
				throw new AiCustomException(AiErrorCode.INVALID_REQUEST);
			}

			AiCommand command = new AiCommand(event.getReviewId(), event.getContent());
			AiResult result = aiService.check(command);

			boolean isBlind = result.isAbusive() && result.confidence() >= 0.80;

			if (isBlind) {
				reviewClient.updateBlind(event.getReviewId(), true);
			}
			// TODO: DB 저장 예정

		} catch (AiCustomException e) {
			// swallow: 재시도 금지, 메시지 정상 처리로 마무리
		} catch (Exception e) {
			throw new AiCustomException(AiErrorCode.KAFKA_EVENT_PARSE_FAIL);
		}
	}
}
