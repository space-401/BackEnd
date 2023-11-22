package com.app.kkiri.global.exception;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class ExceptionResponse {

	private final int code;
	private final String message;
}
