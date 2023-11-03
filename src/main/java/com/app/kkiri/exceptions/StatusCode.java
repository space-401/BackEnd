package com.app.kkiri.exceptions;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum StatusCode {
    OK(200, "success"),
    ALREADY_SAVED_SPACE(400, "이미 가입된 회원입니다."),
    BAD_REQUEST(400, "failure"),
    INVALID_TOKEN(401, "인증에 실패했습니다.");

    private final int status;
    private final String message;
}
