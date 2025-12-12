package com.popjub.aiservice.infrastructure.producer;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.popjub.aiservice.application.event.ReviewBlindEvent;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.exception.AiErrorCode;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class ReviewBlindProducer {

	private static final String TOPIC = "review.blind";

	private final KafkaTemplate<String, String> kafkaTemplate;
	private final ObjectMapper objectMapper;

	public void send(ReviewBlindEvent event) {
		try {
			String message = objectMapper.writeValueAsString(event);

			kafkaTemplate.send(TOPIC, event.reviewId().toString(), message);
			log.info("[AI] blind update 이벤트 발행: topic={}, key={}, payload={}",
				TOPIC, event.reviewId(), message);

		} catch (Exception e) {
			throw new AiCustomException(AiErrorCode.KAFKA_EVENT_PARSE_FAIL);
		}
	}
}
