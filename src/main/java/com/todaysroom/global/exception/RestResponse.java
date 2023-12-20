package com.todaysroom.global.exception;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@Getter
@Builder
public class RestResponse {

    private String code;
    private String message;

    public static ResponseEntity<RestResponse> toResponseEntity(ResponseCode responseCode, String message) {
        return ResponseEntity.status(responseCode.getHttpStatus()).body(RestResponse.builder()
                .code(responseCode.getCode())
                .message(message)
                .build());
    }

    public static ResponseEntity<RestResponse> toResponseEntity(HttpStatus httpStatus, String code, String message) {
        return ResponseEntity.status(httpStatus).body(RestResponse.builder()
                .code(code)
                .message(message)
                .build());
    }
    public RestResponse(String code, String message) {
        this.code = code;
        this.message = message;
    }

}
