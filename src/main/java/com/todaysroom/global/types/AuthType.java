package com.todaysroom.types;

public enum AuthType {

    AUTHORITIES_KEY("auth"),
    AUTHORIZATION_HEADER("Authorization"),
    COOKIE_HEADER("Set-Cookie"),
    REFRESHTOKEN_KEY("refreshToken"),
    HEADER_VALUE("refreshToken="),
    REISSUE_HEADER("cookie"),
    TOKEN_HEADER("Bearer ");

    final private String item;

    AuthType(String item) {
        this.item = item;
    }

    public String getByItem() {
        return item;
    }
}
