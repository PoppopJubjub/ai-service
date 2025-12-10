package com.popjub.aiservice.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public class AiCustomException extends RuntimeException {

	private final AiErrorCode errorCode;
	private final String message;
	private final HttpStatus status;

	public AiCustomException(AiErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
		this.message = errorCode.getMessage();
		this.status = errorCode.getStatus();
	}
}
