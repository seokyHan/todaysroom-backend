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

//    public static UserTokenInfoDto from(UserEntity userEntity, String accessToken, String refreshToken) {
//        return new UserTokenInfoDto(accessToken,
//                refreshToken,
//                userEntity.getId(),
//                userEntity.getUserEmail(),
//                userEntity.getUserName(),
//                userEntity.getNickname(),
//                userEntity.getRecentSearch());
//    }

    @Builder
    public UserTokenInfoDto {
    }
}
