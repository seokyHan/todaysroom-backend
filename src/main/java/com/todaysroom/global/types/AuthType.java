package com.todaysroom.global.types;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum AuthType {

    AUTHORITIES_KEY("auth"),
    COOKIE_HEADER("Set-Cookie"),
    REFRESHTOKEN_KEY("refreshToken"),
    HEADER_VALUE("refreshToken="),
    REISSUE_HEADER("cookie"),
    TOKEN_HEADER("Bearer ");

    private final String item;

}
