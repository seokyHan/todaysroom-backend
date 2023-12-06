package com.todaysroom.global.exception.code;

import com.todaysroom.global.exception.ResponseCode;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;

import static org.apache.commons.lang3.StringUtils.leftPad;

@Getter
@RequiredArgsConstructor
public enum AuthResponseCode implements ResponseCode {
    FORBIDDEN("0", HttpStatus.FORBIDDEN),
    UNAUTHORIZED("1", HttpStatus.UNAUTHORIZED),
    TOKEN_EXPIRED("2", HttpStatus.UNAUTHORIZED),
    USER_NOT_FOUND("3", HttpStatus.NOT_FOUND),
    AUTH_FAIL("4", HttpStatus.UNAUTHORIZED),
    USER_SECURITY_NOT_FOUND("5", HttpStatus.NOT_FOUND),
    UNAUTHORIZED_TOKEN_ERROR("6", HttpStatus.UNAUTHORIZED),
            ;

    private final String code;
    private final HttpStatus httpStatus;

    public String getCode() {
        return String.format("I-AUT-%s", leftPad(code, 4, "0"));

    }
}
