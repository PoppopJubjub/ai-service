package com.popjub.aiservice.application.port;

import com.popjub.aiservice.application.dto.command.AiCommand;
import com.popjub.aiservice.application.dto.result.AiResult;

public interface AiModelClient {
	AiResult check(AiCommand command);
}
