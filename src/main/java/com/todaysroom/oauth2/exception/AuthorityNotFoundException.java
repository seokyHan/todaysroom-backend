package com.todaysroom.oauth2.exception;

public class AuthorityNotFoundException extends RuntimeException{
    public AuthorityNotFoundException(){
        super("권한이 존재하지 않습니다.");
    }
}
