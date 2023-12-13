package com.todaysroom.global.exception;

import lombok.Getter;

import java.util.function.Supplier;

@Getter
public class CustomException extends RuntimeException {

    private final ResponseCode responseCode;
    private String[] values;

    public CustomException(ResponseCode responseCode) {
        this.responseCode = responseCode;
    }

    public CustomException(String message, ResponseCode responseCode) {
        super(message);
        this.responseCode = responseCode;
    }

    public CustomException(ResponseCode responseCode, String... values) {
        this.responseCode = responseCode;
        this.values = values;
    }

    public CustomException(String message, ResponseCode responseCode, String... values) {
        super(message);
        this.responseCode = responseCode;
        this.values = values;
    }

    public CustomException(Throwable cause, ResponseCode responseCode) {
        super(cause);
        this.responseCode = responseCode;
    }

    public CustomException(String message, Throwable cause, ResponseCode responseCode) {
        super(message, cause);
        this.responseCode = responseCode;
    }

}
