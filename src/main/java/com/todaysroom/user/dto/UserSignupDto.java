package com.todaysroom.user.dto;

import com.todaysroom.user.entity.UserEntity;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import org.hibernate.validator.constraints.Length;

public record UserSignupDto(@NotBlank(message = "이메일을 입력하세요.")
                            @Email(message = "이메일 형식을 확인하세요.")
                            String userEmail,
                            @NotBlank(message = "비밀번호를 입력하세요.")
                            @Length(min = 8, max = 20, message = "8~20자리의 비밀번호를 입력하세요")
                            String password,
                            @NotBlank(message = "이름을 입력하세요.")
                            @Length(min = 2, max = 10, message = "2~10자리의 닉네임을 입력하세요.")
                            String userName,
                            @NotBlank(message = "닉네임을 입력하세요.")
                            @Length(min = 2, max = 10, message = "2~10자리의 닉네임을 입력하세요.")
                            String nickname) {

    public UserEntity toUserEntity() {
        return UserEntity.builder()
                .userEmail(userEmail)
                .password(password)
                .userName(userName)
                .nickname(nickname)
                .build();
    }
}
