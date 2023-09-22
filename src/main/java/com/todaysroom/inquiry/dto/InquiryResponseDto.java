package com.todaysroom.inquiry.dto;

import com.todaysroom.common.file.entity.UserFiles;
import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.inquiry.entity.InquiryAnswer;
import org.springframework.web.multipart.MultipartFile;


import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public record InquiryResponseDto(Long id,
                                 String title,
                                 String content,
                                 String inquiryType,
                                 boolean isComplete,
                                 LocalDateTime createdDate,
                                 LocalDateTime modifiedDate,
                                 List<InquiryAnswer> inquiryAnswers,
                                 Optional<List<UserFiles>> fileList) {

    public static InquiryResponseDto from(Inquiry inquiry) {
        return new InquiryResponseDto(inquiry.getId(), inquiry.getTitle(), inquiry.getContent(), inquiry.getInquiryType(), inquiry.isComplete(),
                inquiry.getCreatedDate(), inquiry.getModifiedDate(), inquiry.getInquiryAnswer(), Optional.empty());
    }

    public static InquiryResponseDto of(Inquiry inquiry, List<UserFiles> fileList) {
        return new InquiryResponseDto(inquiry.getId(), inquiry.getTitle(), inquiry.getContent(), inquiry.getInquiryType(), inquiry.isComplete(),
                inquiry.getCreatedDate(), inquiry.getModifiedDate(), inquiry.getInquiryAnswer(), Optional.ofNullable(fileList));
    }
}
