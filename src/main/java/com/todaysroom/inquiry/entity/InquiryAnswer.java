package com.todaysroom.inquiry.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class InquiryAnswer {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inquiry_id")
    @JsonBackReference(value = "inquiry-inquiry_answer")
    private Inquiry inquiry;

    @NotBlank(message = "내용을 입력하세요")
    private String answerContent;

    public void answerUpdate(String answerContent){
        this.answerContent=answerContent;
    }

    @Builder
    public InquiryAnswer(Long id, Inquiry inquiry, String answerContent) {
        this.id = id;
        this.inquiry = inquiry;
        this.answerContent = answerContent;
    }
}
