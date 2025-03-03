package com.snappay.taxforecaster.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<ErrorModel> handle(NotAcceptableException exception) {
        return new ResponseEntity<>(new ErrorModel(exception.getMessages()),exception.getStatus());
    }
}
