package com.todaysroom.inquiry.repository;


import com.todaysroom.inquiry.entity.InquiryAnswer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InquiryAnswerRepository extends JpaRepository<InquiryAnswer, Long> {
}
