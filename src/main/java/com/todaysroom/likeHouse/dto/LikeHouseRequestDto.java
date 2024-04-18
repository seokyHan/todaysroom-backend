package com.todaysroom.likeHouse.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

public record LikeHouseRequestDto(Long userId,
                                  @NotBlank(message = "코드가 없습니다.") String aptCode) {

}
