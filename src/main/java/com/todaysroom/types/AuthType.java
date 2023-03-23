package com.todaysroom.types;

public enum AuthType {

    AUTHORITIES_KEY("auth");

    final private String item;

    AuthType(String item) {
        this.item = item;
    }

    public String getByItem() {
        return item;
    }
}
