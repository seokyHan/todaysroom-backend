package com.todaysroom.exception;

import com.todaysroom.user.types.ErrorCode;

public class CustomException extends RuntimeException{
    private ErrorCode error;

    public CustomException(ErrorCode e) {
        super(e.getMessage());
        this.error = e;
    }
}
