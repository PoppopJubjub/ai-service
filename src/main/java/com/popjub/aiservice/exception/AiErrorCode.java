package com.popjub.aiservice.exception;

import org.springframework.http.HttpStatus;

import com.popjub.common.exception.BaseErrorCode;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AiErrorCode implements BaseErrorCode {

	INVALID_REQUEST("400:AI 요청값이 올바르지 않습니다.", HttpStatus.BAD_REQUEST),
	EMPTY_TEXT("400:AI 분석 텍스트가 비어 있습니다.", HttpStatus.BAD_REQUEST),
	INVALID_REVIEW_ID("400:리뷰 ID가 유효하지 않습니다.", HttpStatus.BAD_REQUEST),

	INVALID_REQUEST_BODY("400:잘못된 요청입니다.", HttpStatus.BAD_REQUEST),
	INVALID_REVIEW_ID_FORMAT("400:리뷰 ID 형식이 잘못되었습니다.", HttpStatus.BAD_REQUEST),
	INVALID_RESPONSE("500:AI 내부 응답 생성 오류", HttpStatus.INTERNAL_SERVER_ERROR),

	GEMINI_RESPONSE_EMPTY("502:Gemini 응답이 비어 있습니다.", HttpStatus.BAD_GATEWAY),
	GEMINI_CANDIDATE_EMPTY("502:Gemini 응답 candidates가 없습니다.", HttpStatus.BAD_GATEWAY),
	GEMINI_CONTENT_EMPTY("502:Gemini 응답 content가 없습니다.", HttpStatus.BAD_GATEWAY),
	GEMINI_PARSE_FAIL("502:Gemini 응답 파싱 실패", HttpStatus.BAD_GATEWAY),
	MODEL_RESPONSE_FAIL("502:모델 응답 반환 실패", HttpStatus.BAD_GATEWAY),
	GEMINI_API_FAIL("API 요청 실패", HttpStatus.BAD_GATEWAY),

	KAFKA_EVENT_PARSE_FAIL("400:Kafka 메시지 파싱 실패", HttpStatus.BAD_REQUEST);

	private final String message;
	private final HttpStatus status;
}
