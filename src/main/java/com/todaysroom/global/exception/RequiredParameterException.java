package com.todaysroom.global.exception;

import com.todaysroom.global.exception.code.CommonResponseCode;
import lombok.Getter;

@Getter
public class RequiredParameterException extends RuntimeException{

    private final ResponseCode responseCode = CommonResponseCode.REQUIRED_ERROR;

    public RequiredParameterException(String requiredParameterName) {
        super(requiredParameterName);
    }
}
