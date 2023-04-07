package com.todaysroom.user.dto;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.Length;

public record UserLoginDto(@NotBlank(message = "이메일을 입력하세요.")
                           @Email(message = "이메일 형식을 확인하세요")
                           String userEmail,

                           @NotBlank(message = "비밀번호를 입력하세요.")
                           @Length(min = 8, max = 20, message = "8~20자리의 비밀번호를 입력하세요")
                           String password) {

}
