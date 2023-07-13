package com.todaysroom.user.types;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
    //JWT
    UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "ROLE-0002", "Unauthorized"),
    JWT_ACCESS_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0001", "Access token has expired"),
    JWT_REFRESH_TOKEN_EXPIRED(HttpStatus.UNAUTHORIZED, "TOKEN-0002", "Refresh token has expired"),
    JWT_REFRESH_TOKEN_NOT_MATCHED(HttpStatus.UNAUTHORIZED, "TOKEN-0003", "Refresh token not matched"),
    DUPLICATED_USER_EMAIL(HttpStatus.UNAUTHORIZED, "ROLE-0003", "duplicated userEmail");
    

    HttpStatus status;
    String code;
    String message;

    public HttpStatus getStatus() {
        return status;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    ErrorCode(HttpStatus status, String code, String message) {
        this.status = status;
        this.code = code;
        this.message = message;
    }
}
