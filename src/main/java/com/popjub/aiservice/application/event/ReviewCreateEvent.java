package com.popjub.aiservice.application.event;

import java.util.UUID;

import lombok.Getter;

@Getter
public class ReviewCreateEvent {
	private UUID reviewId;
	private String content;
}
