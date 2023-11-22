package com.app.kkiri.global.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum ExceptionCode {

	INVALID_REQUEST(1000, "올바르지 않은 요청입니다."),
	MISSING_PARAMETER(1001, "필수인 파라미터 값이 넘어오지 않았습니다."),
	DUPLICATED_MEMEBER_NICKNAME(1002, "중복된 닉네임입니다."),
	ALREADY_SAVED_SPACE(1003, "이미 가입된 스페이스입니다."),

	OVERSIZED_IMAGE(5001, "업로드 가능한 이미지 용량을 초과했습니다."),
	MISSING_IMAGE(5002, "기본 이미지가 없습니다."),

	INVALID_TOKEN(9101, "올바르지 않은 형식의 토큰입니다."),
	EXPIRED_TOKEN(9102, "기한이 만료된 토큰입니다."),
	FAIL_TO_VALIDATE_TOKEN(9103, "토큰 유효성 검사 중 오류가 발생했습니다."),
	TOKEN_NOT_FOUND(9104, "헤더에 토큰 정보가 포함되어 있지 않습니다."),
	UNAUTHORIZED_REQUEST(9105, "해당 요청에 대한 접근 권한이 없습니다."),

	INTERNAL_SEVER_ERROR(9999, "서버 에러가 발생하였습니다. 관리자에게 문의해 주세요.");

	private final int code;
	private final String message;
}
