package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;

public record UserTokenInfoDto(String token,
                               Long id,
                               String name,
                               String userEmail,
                               String nickname,
                               String recentSearch) {

    public static UserTokenInfoDto from(UserInfoDto userInfoDto, String token) {
        return new UserTokenInfoDto(token,
                userInfoDto.id(),
                userInfoDto.name(),
                userInfoDto.userEmail(),
                userInfoDto.nickname(),
                userInfoDto.recentSearch());
    }

}
