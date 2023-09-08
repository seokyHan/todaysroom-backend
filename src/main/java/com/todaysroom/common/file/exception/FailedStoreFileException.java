package com.todaysroom.common.file.exception;

public class FailedStoreFileException extends RuntimeException{
    public FailedStoreFileException() {
        super("파일 저장 실패");
    }
}
