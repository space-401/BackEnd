package com.app.kkiri.global.exception;

import lombok.Getter;

@Getter
public class ImageException extends BadRequestException {
	// ?? 왜 RuntimeException 을 상속 받지 않고 BadRequestException 을 상속 받나 ??

	public ImageException(ExceptionCode exceptionCode) {
		super(exceptionCode);
	}
}
