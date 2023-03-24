package com.todaysroom.types;

public enum AuthType {

    AUTHORITIES_KEY("auth"),
    AUTHORIZATION_HEADER("Authorization"),
    TOKEN_HEADER("Bearer ");

    final private String item;

    AuthType(String item) {
        this.item = item;
    }

    public String getByItem() {
        return item;
    }
}
