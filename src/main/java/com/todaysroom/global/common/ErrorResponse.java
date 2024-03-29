package com.todaysroom.global.common;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ErrorResponse {

    private int status;
    private String msg;

    public ErrorResponse(int status, String msg) {
        super();
        this.status = status;
        this.msg = msg;
    }
}
