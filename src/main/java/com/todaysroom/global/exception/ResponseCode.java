package com.todaysroom.global.exception;

import org.springframework.http.HttpStatus;

public interface ResponseCode {

    String getCode();
    HttpStatus getHttpStatus();
}


