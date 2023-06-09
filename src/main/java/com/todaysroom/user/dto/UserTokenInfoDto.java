package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;
import lombok.Builder;


public record UserTokenInfoDto(String accessToken,
                               String refreshToken,
                               Long id,
                               String userEmail,
                               String userName,
                               String nickname,
                               String recentSearch) {

    @Builder
    public UserTokenInfoDto {
    }
}
