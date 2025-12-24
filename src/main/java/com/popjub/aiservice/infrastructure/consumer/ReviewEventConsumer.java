package com.popjub.aiservice.infrastructure.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.popjub.aiservice.application.event.ReviewCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.exception.AiErrorCode;
import com.popjub.aiservice.application.service.ReviewInspectionService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventConsumer {

	private final ObjectMapper objectMapper;
	private final ReviewInspectionService reviewInspectionService;

	@KafkaListener(topics = "review.created", groupId = "ai-service-group")
	public void consume(String message) {
		try {

			ReviewCreateEvent event = objectMapper.readValue(message, ReviewCreateEvent.class);

			reviewInspectionService.process(event);

		} catch (AiCustomException e) {
			// swallow: 재시도 금지, 메시지 정상 처리로 마무리
		} catch (Exception e) {
			throw new AiCustomException(AiErrorCode.KAFKA_EVENT_PARSE_FAIL);
		}
	}
}
