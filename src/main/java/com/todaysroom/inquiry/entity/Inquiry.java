package com.todaysroom.inquiry.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.todaysroom.common.BaseTimeEntity;
import com.todaysroom.inquiry.dto.InquiryUpdateDto;
import com.todaysroom.user.entity.UserEntity;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Inquiry extends BaseTimeEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    @JsonBackReference(value = "user-inquiry")
    private UserEntity userEntity;

    @NotBlank(message = "문의 유형이 없습니다")
    private String inquiryType;

    @NotBlank(message = "제목을 입력하세요")
    private String title;

    @NotBlank(message = "내용을 입력하세요")
    private String content;

    private boolean isComplete;

    @OneToMany(cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id", referencedColumnName = "id")
    @JsonManagedReference(value = "inquiry-inquiry_answer")
    private List<InquiryAnswer> inquiryAnswer;

    public void updateInquiry(InquiryUpdateDto inquiryUpdateDto){
        this.title = inquiryUpdateDto.title();
        this.content = inquiryUpdateDto.content();
    }

    public void isCompleteInquiryUpdate(boolean isComplete){
        this.isComplete = isComplete;
    }

    @Builder
    public Inquiry(Long id, UserEntity userEntity, String inquiryType, String title, String content, boolean isComplete, List<InquiryAnswer> inquiryAnswer) {
        this.id = id;
        this.userEntity = userEntity;
        this.inquiryType = inquiryType;
        this.title = title;
        this.content = content;
        this.isComplete = isComplete;
        this.inquiryAnswer = inquiryAnswer;
    }
}
