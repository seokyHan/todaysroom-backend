package com.todaysroom.inquiry.service;

import com.todaysroom.exception.NoUserException;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.repository.InquiryAnswerRepository;
import com.todaysroom.inquiry.repository.InquiryRepository;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final UserRepository userRepository;

    @Transactional
    public InquiryResponseDto save(InquiryRequestDto inquiryRequestDto) throws NoUserException {
        UserEntity userEntity = userRepository.findById(inquiryRequestDto.id()).orElseThrow(NoUserException::new);

        return InquiryResponseDto.from(inquiryRepository.save(inquiryRequestDto.toSaveInquiryEntity(userEntity)));
    }
}
