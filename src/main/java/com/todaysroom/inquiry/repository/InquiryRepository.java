package com.todaysroom.inquiry.repository;

import com.todaysroom.inquiry.entity.Inquiry;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface InquiryRepository extends JpaRepository<Inquiry, Long> {
    //List<Inquiry> findByUserId(Long id);
}
