package com.todaysroom.user.exception;

public class NoRefreshTokenException extends RuntimeException{
    public NoRefreshTokenException(){
        super("refreshToken이 존재하지 않습니다.");
    }
}
