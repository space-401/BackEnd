package com.app.kkiri.global.exception;

import static org.springframework.http.HttpStatus.*;
import static com.app.kkiri.global.exception.ExceptionCode.*;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

@RestControllerAdvice
public class GlobalExceptionHandler extends ResponseEntityExceptionHandler {

    private final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(
        MethodArgumentNotValidException ex,
        HttpHeaders headers,
        HttpStatus status,
        WebRequest request
    ) {
        LOGGER.warn(ex.getMessage(), ex);

        final String errMessage = Objects.requireNonNull(ex.getBindingResult().getFieldError()).getDefaultMessage();

        return ResponseEntity.badRequest().body(new ExceptionResponse(INVALID_REQUEST.getCode(), errMessage));
    }
    // ?? 이건 어떤 예외를 처리하는 메소드 일까 ??

    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<ExceptionResponse> handleBadRequestException(final BadRequestException ex) {
        LOGGER.warn(ex.getMessage(), ex);

        return ResponseEntity.badRequest().body(new ExceptionResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(AuthException.class)
    public ResponseEntity<ExceptionResponse> handleAuthException(final AuthException ex) {
        LOGGER.warn(ex.getMessage(), ex);

        return ResponseEntity.status(UNAUTHORIZED).body(new ExceptionResponse(ex.getCode(), ex.getMessage()));
    }

    @ExceptionHandler(DeleteFailureException.class)
    public ResponseEntity<?> delteFailureException(final DeleteFailureException ex) {
        LOGGER.warn(ex.getMessage(), ex);

        Map<String, Object> map = new HashMap<>();
        map.put("code", 1005);
        map.put("message", ex.getMessage());

        return ResponseEntity.badRequest().body(map);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionResponse> handleException(final Exception ex) {
        LOGGER.warn(ex.getMessage(), ex);

        return ResponseEntity.internalServerError()
            .body(new ExceptionResponse(INTERNAL_SEVER_ERROR.getCode(), INTERNAL_SEVER_ERROR.getMessage()));
    }


}
