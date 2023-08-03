package com.todaysroom.inquiry.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;

public record InquiryUpdateDto(@Id Long id,

                               @NotBlank(message = "User id 가 없습니다.")
                               Long userId,

                               @NotBlank(message = "내용을 입력하세요")
                               String InquiryType,

                               @NotBlank(message = "제목을 입력하세요")
                               String title,

                               @NotBlank(message = "내용을 입력하세요")
                               String content
                               ) {
}
