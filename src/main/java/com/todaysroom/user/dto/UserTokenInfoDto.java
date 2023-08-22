package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;
import lombok.Builder;

import java.util.List;


public record UserTokenInfoDto(String accessToken,
                               String refreshToken,
                               Long id,
                               String userEmail,
                               String userName,
                               String nickname,
                               String recentSearch,
                               List<String> authorities) {

    @Builder
    public UserTokenInfoDto {
    }
}
