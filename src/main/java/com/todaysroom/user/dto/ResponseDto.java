package com.todaysroom.user.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

public record ResponseDto(HttpStatus status,
                          String message,
                          Object response,
                          String code) {

    @Builder
    public ResponseDto {}
}
