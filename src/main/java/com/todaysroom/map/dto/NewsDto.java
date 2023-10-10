package com.todaysroom.map.dto;

public record NewsDto(String imageUrl,
                      String newsTitle,
                      String newsContent,
                      String newsLink,
                      String writing,
                      String date
                      ) {

    public NewsDto {
    }
}
