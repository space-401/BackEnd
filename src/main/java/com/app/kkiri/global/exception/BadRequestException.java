package com.app.kkiri.global.exception;

import lombok.Getter;

@Getter
public class BadRequestException extends RuntimeException {

	private final int code;
	private final String message;

	public BadRequestException(final ExceptionCode exceptionCode) {
		// 파라미터를 final 키워드를 붙여 선언하면,
		// 파라미터로 받아오는 인자에 대한 값을 재할당 금지하는 이점이 있다.

		this.code = exceptionCode.getCode();
		this.message = exceptionCode.getMessage();
	}

}
