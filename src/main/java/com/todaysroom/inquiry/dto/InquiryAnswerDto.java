package com.todaysroom.inquiry.dto;

import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.inquiry.entity.InquiryAnswer;
import jakarta.validation.constraints.NotBlank;

public record InquiryAnswerDto(@NotBlank(message = "1대1문의 id가 필요합니다.") Long inquiryId,
                               @NotBlank(message = "답변 내용을 작성해 주세요.") String answerContent) {

    public InquiryAnswer toSaveInquiryAnswerEntity(Inquiry inquiry){
        InquiryAnswer inquiryAnswer = InquiryAnswer.builder()
                .inquiry(inquiry)
                .answerContent(answerContent)
                .build();

        return inquiryAnswer;
    }
}
