package com.todaysroom.inquiry.dto;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

import java.util.List;

public record InquiryUpdateDto(@Id Long id,

                               @Positive(message = "User id 가 없습니다.")
                               Long userId,

                               @NotBlank(message = "내용을 입력하세요")
                               String inquiryType,

                               @NotBlank(message = "제목을 입력하세요")
                               String title,

                               @NotBlank(message = "내용을 입력하세요")
                               String content,
                               List<Long> deleteFileList
                               ) {
}
