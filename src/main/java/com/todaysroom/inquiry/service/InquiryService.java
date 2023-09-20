package com.todaysroom.inquiry.service;

import com.todaysroom.common.file.dto.UserFileDto;
import com.todaysroom.common.file.entity.Files;
import com.todaysroom.common.file.entity.UserFiles;
import com.todaysroom.common.file.repository.FilesRepository;
import com.todaysroom.common.file.repository.UserFilesRepository;
import com.todaysroom.common.file.service.FileService;
import com.todaysroom.common.file.exception.FileLocationNotFoundException;
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
    private final FilesRepository filesRepository;
    private final FileService fileService;
    private final InquiryAnswerRepository inquiryAnswerRepository;
    private final UserRepository userRepository;
    private final UserFilesRepository userFilesRepository;

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

    @Transactional InquiryResponseDto getInquiresById(Long id) throws NoInquiryIdException{
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(NoInquiryIdException::new);
        Files files = filesRepository.findById(1L).orElseThrow(FileLocationNotFoundException::new);
        List<UserFiles> fileList = userFilesRepository.findByPostIdAndFileId(inquiry.getId(), files.getId());

        if(fileList != null || !fileList.isEmpty()) {

        }

        return InquiryResponseDto.from(inquiry);

    }


    @Transactional
    public InquiryResponseDto create(InquiryRequestDto inquiryRequestDto, List<MultipartFile> fileList) throws NoUserException {
        UserEntity userEntity = userRepository.findById(inquiryRequestDto.userId()).orElseThrow(NoUserException::new);
        Inquiry inquiry = inquiryRepository.save(inquiryRequestDto.toSaveInquiryEntity(userEntity));

        if(fileList != null || !fileList.isEmpty()){
            Files files = filesRepository.findById(1L).orElseThrow(FileLocationNotFoundException::new);

            UserFileDto userFileDto = UserFileDto.builder()
                    .postId(inquiry.getId())
                    .file(files)
                    .fileList(fileList)
                    .build();

            fileService.saveFiles(userFileDto);
        }

        return InquiryResponseDto.from(inquiry);
    }
}
