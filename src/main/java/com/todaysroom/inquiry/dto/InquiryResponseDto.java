package com.todaysroom.inquiry.dto;

import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.inquiry.entity.InquiryAnswer;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.List;

public record InquiryResponseDto(Long id,
                                 String title,
                                 String content,
                                 String inquiryType,
                                 boolean isComplete,
                                 LocalDateTime createdDate,
                                 LocalDateTime modifyDate,
                                 List<InquiryAnswer> inquiryAnswers) {

    public static InquiryResponseDto from(Inquiry inquiry) {
        return new InquiryResponseDto(inquiry.getId(), inquiry.getTitle(), inquiry.getContent(), inquiry.getInquiryType(), inquiry.isComplete(),
                inquiry.getCreatedDate(), inquiry.getModifiedDate(), inquiry.getInquiryAnswer());
    }
}
