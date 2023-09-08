package com.todaysroom.inquiry.service;

import com.todaysroom.user.exception.NoUserException;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.entity.Inquiry;
import com.todaysroom.inquiry.repository.InquiryAnswerRepository;
import com.todaysroom.inquiry.repository.InquiryRepository;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final UserRepository userRepository;


    @Transactional
    public List<InquiryResponseDto> getInquiries(Long userId) throws NoUserException {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(NoUserException::new);
        List<Inquiry> inquiries = inquiryRepository.findByUserEntityId(userEntity.getId());

        return inquiries.stream().map(InquiryResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public List<InquiryResponseDto> getAllInquiries(){
        List<Inquiry> inquiries = inquiryRepository.findAll();

        return inquiries.stream().map(InquiryResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public InquiryResponseDto save(InquiryRequestDto inquiryRequestDto) throws NoUserException {
        UserEntity userEntity = userRepository.findById(inquiryRequestDto.userId()).orElseThrow(NoUserException::new);

        return InquiryResponseDto.from(inquiryRepository.save(inquiryRequestDto.toSaveInquiryEntity(userEntity)));
    }
}
