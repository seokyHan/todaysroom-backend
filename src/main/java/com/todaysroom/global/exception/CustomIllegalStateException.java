package com.todaysroom.global.exception;

import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class CustomIllegalStateException extends RuntimeException {
    private final HttpStatus httpStatus;
    private final ResponseCode responseCode;

    public CustomIllegalStateException(HttpStatus httpStatus, ResponseCode responseCode) {
        this.httpStatus = httpStatus;
        this.responseCode = responseCode;
    }
}
