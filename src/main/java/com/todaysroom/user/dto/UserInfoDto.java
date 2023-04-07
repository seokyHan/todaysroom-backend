package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;

public record UserInfoDto(Long id,
                          String name,
                          String userEmail,
                          String nickname,
                          String recentSearch) {

    public static UserInfoDto from(UserEntity user) {
        return new UserInfoDto(user.getId(),
                user.getUserEmail(),
                user.getUserName(),
                user.getNickname(),
                user.getRecentSearch());
    }
}
