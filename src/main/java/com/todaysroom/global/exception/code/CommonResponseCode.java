package com.todaysroom.global.exception.code;

import com.todaysroom.global.exception.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.leftPad;

@Getter
@RequiredArgsConstructor
public enum CommonResponseCode implements ResponseCode {
    // 공통 코드 정의
    SUCCESS("0", HttpStatus.OK),
    UNKNOWN_ERROR_PARAMS("1", HttpStatus.INTERNAL_SERVER_ERROR),
    REQUIRED_ERROR("2", HttpStatus.BAD_REQUEST),
    CODE_NOT_FOUND("3", HttpStatus.BAD_REQUEST),
    PARAMETER_TYPE_ERROR("4", HttpStatus.BAD_REQUEST),
    PARAMETER_ERROR("5", HttpStatus.BAD_REQUEST),
    NOT_FOUND("6", HttpStatus.NOT_FOUND),
    ALREADY_IN_PROGRESS("7", HttpStatus.BAD_REQUEST),
    API_NOT_FOUND("8", HttpStatus.NOT_FOUND),
    CONNECT_FAILED("9", HttpStatus.BAD_GATEWAY),
    BAD_REQUEST("10", HttpStatus.BAD_REQUEST),
    ALREADY_BEEN_PROCESSED("11", HttpStatus.ALREADY_REPORTED),
    MEMBER_NOT_FOUND("12", HttpStatus.NOT_FOUND),
    FILE_NOT_FOUND("13", HttpStatus.NOT_FOUND),
    SFTP_UPLOAD_ERROR("14", HttpStatus.INTERNAL_SERVER_ERROR),
    DIRECTORY_MAKE_ERROR("15", HttpStatus.INTERNAL_SERVER_ERROR),
    FILE_LOCATION_NOT_FOUND("16", HttpStatus.NOT_FOUND),
    UNKNOWN_ERROR("9999", HttpStatus.INTERNAL_SERVER_ERROR),

    ;

    private final String code;
    private final HttpStatus httpStatus;

    public String getCode() {
        return String.format("I-COM-%s", leftPad(code, 4, "0"));
    }
}