package com.todaysroom.exception;

public class NoInquiryIdException extends IllegalAccessException{
    public NoInquiryIdException() {
        super("문의 하신 게시글 id가 없습니다.");
    }
}
