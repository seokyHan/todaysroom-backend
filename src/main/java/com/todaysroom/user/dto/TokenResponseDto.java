package com.todaysroom.user.dto;

import lombok.Builder;
import org.springframework.http.HttpStatus;

public record TokenResponseDto(HttpStatus status,
                               String message,
                               Object response,
                               String code) {

    @Builder
    public TokenResponseDto {}
}
