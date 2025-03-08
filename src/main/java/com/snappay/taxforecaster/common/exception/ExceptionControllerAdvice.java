package com.snappay.taxforecaster.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.util.Collections;

@ControllerAdvice
public class ExceptionControllerAdvice {

    @ExceptionHandler(NotAcceptableException.class)
    public ResponseEntity<ErrorModel> handle(NotAcceptableException exception) {
        return new ResponseEntity<>(new ErrorModel(exception.getMessages()), exception.getStatus());
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorModel> handle(RuntimeException exception) {
        return new ResponseEntity<>(new ErrorModel(Collections.singletonList(exception.getMessage())), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
