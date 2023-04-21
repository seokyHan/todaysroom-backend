package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserTokenInfoDto(String accessToken,
                               String refreshToken,
                               Long id,
                               @NotBlank(message = "이메일을 입력하세요.")
                               @Email(message = "이메일 형식을 확인하세요.")
                               String userEmail,
                               @NotBlank(message = "이름을 입력하세요.")
                               @Length(min = 2, max = 10, message = "2~10자리의 닉네임을 입력하세요.")
                               String userName,
                               @NotBlank(message = "닉네임을 입력하세요.")
                               @Length(min = 2, max = 10, message = "2~10자리의 닉네임을 입력하세요.")
                               String nickname,
                               String recentSearch) {

    public static UserTokenInfoDto from(UserInfoDto userInfoDto, String accessToken, String refreshToken) {
        return new UserTokenInfoDto(accessToken,
                refreshToken,
                userInfoDto.id(),
                userInfoDto.name(),
                userInfoDto.userEmail(),
                userInfoDto.nickname(),
                userInfoDto.recentSearch());
    }

}
