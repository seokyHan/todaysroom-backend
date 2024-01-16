package com.todaysroom.map.dto;

public record CoordinatesDto(String x, String y) {

    public static CoordinatesDto of(String x, String y){
        return new CoordinatesDto(x, y);
    }
}
