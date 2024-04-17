package com.todaysroom.likeHouse.dto;

import com.todaysroom.likeHouse.entity.LikeHouse;

public record LikeHouseRequestDto(Long userId,
                                  String aptCode) {

}
