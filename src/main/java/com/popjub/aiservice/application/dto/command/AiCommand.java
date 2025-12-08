package com.popjub.aiservice.application.dto.command;

import java.util.UUID;

public record AiCommand(
	UUID reviewId,
	String text
) {}
