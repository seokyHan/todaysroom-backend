package com.todaysroom.user.types;

public enum AuthType {

    AUTHORITIES_KEY("auth"),
    AUTHORIZATION_HEADER("Authorization"),
    REFRESHTOKEN_HEADER("Set-Cookie"),
    REFRESHTOKEN_KEY("refreshToken"),
    REISSUE_REFRESHTOKEN_HEADER("cookie"),
    TOKEN_HEADER("Bearer ");

    final private String item;

    AuthType(String item) {
        this.item = item;
    }

    public String getByItem() {
        return item;
    }
}
