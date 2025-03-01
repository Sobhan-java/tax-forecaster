package com.snappay.taxforecaster.common.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

import java.util.List;

@Getter
public class NotAcceptableException extends RuntimeException {
    private final HttpStatus status;
    private final List<String> messages;

    public NotAcceptableException(HttpStatus status, List<String> messages) {
        this.status = status;
        this.messages = messages;
    }

    public NotAcceptableException(List<String> messages) {
        this.status = HttpStatus.BAD_REQUEST;
        this.messages = messages;
    }
}
