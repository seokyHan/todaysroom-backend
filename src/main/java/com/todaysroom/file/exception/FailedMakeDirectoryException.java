package com.todaysroom.common.file.exception;

public class FailedMakeDirectoryException extends RuntimeException{
    public FailedMakeDirectoryException() {
        super("디렉토리 생성 실패");
    }

}
