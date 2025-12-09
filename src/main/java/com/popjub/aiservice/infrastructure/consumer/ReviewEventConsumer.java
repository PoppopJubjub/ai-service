package com.popjub.aiservice.infrastructure.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.application.event.ReviewCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.application.service.AiService;
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
			log.info("받은 리뷰 이벤트: reviewId={}, content={}", event.getReviewId(), event.getContent());

			AiCommand command = new AiCommand(event.getReviewId(), event.getContent());
			AiResult result = aiService.check(command);

			boolean isBlind = result.isAbusive() && result.confidence() >= 0.80;

			if (isBlind) {
				log.info("리뷰 {} → BLIND 처리 실행", event.getReviewId());
				reviewClient.updateBlind(event.getReviewId(), true);
			} else {
				log.info("리뷰 {} → 정상(BLIND 아님)", event.getReviewId());
			}
			// TODO: DB 저장 예정

		} catch (Exception e) {
			log.error("Kafka message parsing error", e);
		}
	}
}
