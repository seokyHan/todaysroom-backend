package com.todaysroom.common.file.exception;

public class FileLocationNotFoundException extends RuntimeException{

    public FileLocationNotFoundException() {
            super("저장하려는 파일의 서비스를 찾을 수 없습니다.");
        }

}
