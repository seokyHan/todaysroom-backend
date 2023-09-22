package com.todaysroom.inquiry.service;

import com.todaysroom.common.file.dto.UserFileRequestDto;
import com.todaysroom.common.file.entity.FilesLocation;
import com.todaysroom.common.file.entity.UserFiles;
import com.todaysroom.common.file.service.FileService;
import com.todaysroom.inquiry.exception.NoInquiryIdException;
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
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Slf4j
@RequiredArgsConstructor
public class InquiryService {

    private final InquiryRepository inquiryRepository;
    private final FileService fileService;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final UserRepository userRepository;

    @Transactional
    public List<InquiryResponseDto> getAllInquiries(){
        List<Inquiry> inquiries = inquiryRepository.findAll();

        return inquiries.stream().map(InquiryResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public List<InquiryResponseDto> getInquiriesByUserId(Long userId) throws NoUserException {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(NoUserException::new);
        List<Inquiry> inquiries = inquiryRepository.findByUserEntityId(userEntity.getId());

        return inquiries.stream().map(InquiryResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public InquiryResponseDto getInquiresById(Long id) throws NoInquiryIdException{
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(NoInquiryIdException::new);
        FilesLocation filesLocation = fileService.findByFileLocationId(1L);
        List<UserFiles> fileList = fileService.findByPostIdAndFileLocationId(inquiry.getId(), filesLocation.getId());

        if(fileList != null || !fileList.isEmpty()) {
            return InquiryResponseDto.of(inquiry, fileList);
        }

        return InquiryResponseDto.from(inquiry);
    }


    @Transactional
    public InquiryResponseDto create(InquiryRequestDto inquiryRequestDto, List<MultipartFile> fileList) throws NoUserException {
        UserEntity userEntity = userRepository.findById(inquiryRequestDto.userId()).orElseThrow(NoUserException::new);
        Inquiry inquiry = inquiryRepository.save(inquiryRequestDto.toSaveInquiryEntity(userEntity));

        if(fileList != null && !fileList.isEmpty()){
            FilesLocation filesLocation = fileService.findByFileLocationId(1L);

            UserFileRequestDto userFileDto = UserFileRequestDto.builder()
                    .postId(inquiry.getId())
                    .file(filesLocation)
                    .fileList(fileList)
                    .build();

            fileService.saveFiles(userFileDto);
        }

        return InquiryResponseDto.from(inquiry);
    }
}
