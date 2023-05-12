package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserTokenInfoDto(String accessToken,
                               String refreshToken,
                               Long id,
                               String userEmail,
                               String userName,
                               String nickname,
                               String recentSearch) {

    public static UserTokenInfoDto from(UserEntity userEntity, String accessToken, String refreshToken) {
        return new UserTokenInfoDto(accessToken,
                refreshToken,
                userEntity.getId(),
                userEntity.getUserName(),
                userEntity.getUserEmail(),
                userEntity.getNickname(),
                userEntity.getRecentSearch());
    }

}
