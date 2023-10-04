package com.todaysroom.inquiry.exception;

public class NoInquiryAnswerIdException extends IllegalAccessException{
    public NoInquiryAnswerIdException() {

        super("답글 id가 존재하지 않습니다.");
    }
}