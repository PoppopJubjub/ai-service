package com.popjub.aiservice.application.service;

import org.springframework.stereotype.Service;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;
import com.popjub.aiservice.application.event.ReviewBlindEvent;
import com.popjub.aiservice.application.event.ReviewCreateEvent;
import com.popjub.aiservice.exception.AiCustomException;
import com.popjub.aiservice.exception.AiErrorCode;
import com.popjub.aiservice.infrastructure.producer.ReviewBlindProducer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewInspectionService {

	private final AiService aiService;
	private final ReviewBlindProducer reviewBlindProducer;
	private static final double BLIND_THRESHOLD = 0.80;

	public void process(ReviewCreateEvent event) {

		if (event.reviewId() == null || event.content() == null) {
			throw new AiCustomException(AiErrorCode.INVALID_REQUEST);
		}

		AiCommand command = new AiCommand(event.reviewId(), event.content());
		AiResult result = aiService.check(command);

		boolean isBlind = result.isAbusive() && result.confidence() >= BLIND_THRESHOLD;

		log.info("[AI] Moderation 결과: reviewId={}, abusive={}, confidence={}, blind={}",
			event.reviewId(), result.isAbusive(), result.confidence(), isBlind);

		ReviewBlindEvent blindEvent = new ReviewBlindEvent(
			event.reviewId(),
			isBlind,
			result.confidence(),
			result.category(),
			result.reason()
		);

		reviewBlindProducer.send(blindEvent);
	}
}
