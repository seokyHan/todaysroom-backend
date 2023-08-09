package com.todaysroom.inquiry.dto;

import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.user.entity.UserEntity;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record InquiryRequestDto(@Positive(message = "사용자 없음") Long id,
                                @NotBlank(message = "문의 유형이 없습니다.") String inquiryType,
                                @NotBlank(message = "제목을 입력하세요.") String title,
                                @NotBlank(message = "내용을 입력하세요.") String content
                                ) {

    public Inquiry toSaveInquiryEntity(UserEntity user){
        Inquiry inquiry = Inquiry.builder()
                .inquiryType(inquiryType)
                .title(title)
                .content(content)
                .userEntity(user)
                .build();

        return inquiry;
    }
}
