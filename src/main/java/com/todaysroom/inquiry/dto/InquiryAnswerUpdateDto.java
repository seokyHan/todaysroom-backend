package com.todaysroom.inquiry.dto;

import com.todaysroom.inquiry.entity.InquiryAnswer;
import jakarta.validation.constraints.NotBlank;

public record InquiryAnswerUpdateDto(@NotBlank(message = "답글 id가 필요합니다.") Long id,
                                     @NotBlank(message = "답변 내용을 작성해주세요") String answerContent) {

    public InquiryAnswer toUpdateInquiryAnswerEntity() {
        InquiryAnswer inquiryAnswer = InquiryAnswer.builder()
                .id(id)
                .answerContent(answerContent)
                .build();
        return inquiryAnswer;
    }
}
