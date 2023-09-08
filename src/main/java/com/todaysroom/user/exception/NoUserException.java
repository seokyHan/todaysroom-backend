package com.todaysroom.user.exception;

public class NoUserException extends IllegalAccessException{
    public NoUserException(){
        super("존재하는 사용자가 없습니다.");
    }
}
