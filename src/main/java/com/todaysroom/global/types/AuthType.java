package com.todaysroom.global.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthType {

    AUTH("auth"),
    REFRESH_TOKEN("refreshToken"),
    TOKEN_HEADER("Bearer ");

    private final String item;

}
