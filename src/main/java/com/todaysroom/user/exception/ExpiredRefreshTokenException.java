package com.todaysroom.user.exception;

import com.todaysroom.types.ErrorCode;

public class ExpiredRefreshTokenException extends RuntimeException{

    public ExpiredRefreshTokenException(){
        super("refreshToken이 만료 되었습니다.");
    }

}
