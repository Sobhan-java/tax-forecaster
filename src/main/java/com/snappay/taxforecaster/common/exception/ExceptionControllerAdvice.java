package com.snappay.taxforecaster.common.exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<?> handle(NotAcceptableException exception) {
        return new ResponseEntity<>(exception.getMessages(),exception.getStatus());
    }
}
