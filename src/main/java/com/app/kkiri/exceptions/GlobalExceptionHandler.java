package com.app.kkiri.exceptions;

import com.app.kkiri.domain.dto.StatusDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(value = {CustomException.class})
    protected ResponseEntity<StatusDTO> handlerCustomException(CustomException ex){
        HttpStatus.valueOf(ex.getStatusCode().getStatus());
        return new ResponseEntity<>(new StatusDTO(ex.getStatusCode().getStatus(), ex.getStatusCode().getMessage()), HttpStatus.valueOf(ex.getStatusCode().getStatus()));
    }

    @ExceptionHandler(value = MissingServletRequestParameterException.class)
    protected ResponseEntity<?> missingServlerRequestParameterException(MissingServletRequestParameterException ex){
        String name = ex.getParameterName();
        return new ResponseEntity<>(new StatusDTO(StatusCode.MISSING_PARAMETER.getStatus(), name + StatusCode.MISSING_PARAMETER.getMessage()), HttpStatus.valueOf(StatusCode.MISSING_PARAMETER.getStatus()));
    }
}
