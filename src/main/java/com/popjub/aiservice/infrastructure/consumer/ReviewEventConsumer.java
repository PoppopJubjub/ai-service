package com.popjub.aiservice.infrastructure.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import com.popjub.aiservice.application.event.ReviewCreateEvent;
import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewEventConsumer {

	private final ObjectMapper objectMapper;

	@KafkaListener(topics = "review.created", groupId = "ai-service-group")
	public void consume(String message) {
		try {
			ReviewCreateEvent event = objectMapper.readValue(message, ReviewCreateEvent.class);
			log.info("받은 리뷰 이벤트: reviewId={}, content={}", event.getReviewId(), event.getContent());

			// TODO: AI 검사 로직 호출 예정
			// TODO: DB 저장 예정
			// TODO: review-service로 blind 업데이트 예정

		} catch (Exception e) {
			log.error("Kafka message parsing error", e);
		}
	}
}
