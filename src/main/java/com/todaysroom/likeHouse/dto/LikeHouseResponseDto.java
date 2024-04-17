package com.todaysroom.likeHouse.dto;

import com.todaysroom.likeHouse.entity.LikeHouse;

public record LikeHouseResponseDto(String aptCode) {

    public static LikeHouseResponseDto from(LikeHouse likeHouse){
        return new LikeHouseResponseDto(likeHouse.getAptCode());
    }
}
