package com.todaysroom.inquiry.service;

import com.todaysroom.file.dto.UserFileRequestDto;
import com.todaysroom.file.entity.FilesLocation;
import com.todaysroom.file.entity.UserFiles;
import com.todaysroom.file.service.FileService;
import com.todaysroom.global.exception.CustomException;
import com.todaysroom.inquiry.dto.InquiryAnswerDto;
import com.todaysroom.inquiry.dto.InquiryUpdateDto;
import com.todaysroom.inquiry.entity.InquiryAnswer;
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

import static com.todaysroom.global.exception.code.AuthResponseCode.USER_NOT_FOUND;
import static com.todaysroom.global.exception.code.CommonResponseCode.REQUIRED_ERROR;

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
    public List<InquiryResponseDto> getInquiriesByUserId(Long userId) {
        UserEntity userEntity = userRepository.findById(userId).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "해당 User를 찾을 수 없습니다."));
        List<Inquiry> inquiries = inquiryRepository.findByUserEntityId(userEntity.getId());

        return inquiries.stream().map(InquiryResponseDto::from).collect(Collectors.toList());
    }

    @Transactional
    public InquiryResponseDto getInquiresById(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글이 없습니다."));
        FilesLocation filesLocation = fileService.findByFileLocationId(1L);
        List<UserFiles> fileList = fileService.findByPostIdAndFileLocationId(inquiry.getId(), filesLocation.getId());

        if(fileList != null || !fileList.isEmpty()) {
            return InquiryResponseDto.of(inquiry, fileList);
        }

        return InquiryResponseDto.from(inquiry);
    }


    @Transactional
    public InquiryResponseDto save(InquiryRequestDto inquiryRequestDto, List<MultipartFile> fileList) {
        UserEntity userEntity = userRepository.findById(inquiryRequestDto.userId()).orElseThrow(() -> new CustomException(USER_NOT_FOUND, "해당 User를 찾을 수 없습니다."));
        Inquiry inquiry = inquiryRepository.save(inquiryRequestDto.toSaveInquiryEntity(userEntity));

        if(fileList != null && !fileList.isEmpty()){
            fileSave(inquiry, fileList);
        }

        return InquiryResponseDto.from(inquiry);
    }

    @Transactional
    public Long update(InquiryUpdateDto inquiryUpdateDto, List<MultipartFile> fileList) {
        Inquiry originInquiry = inquiryRepository.findById(inquiryUpdateDto.id()).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글이 없습니다."));
        originInquiry.updateInquiry(inquiryUpdateDto);
        fileService.deleteByFileId(inquiryUpdateDto.deleteFileList());

        if(fileList != null && !fileList.isEmpty()){
            fileSave(originInquiry, fileList);
        }

        return originInquiry.getId();
    }

    @Transactional
    public void delete(Long id) {
        Inquiry inquiry = inquiryRepository.findById(id).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글이 없습니다."));
        inquiryRepository.deleteById(inquiry.getId());

        FilesLocation filesLocation = fileService.findByFileLocationId(1L);
        List<UserFiles> fileList = fileService.findByPostIdAndFileLocationId(inquiry.getId(), filesLocation.getId());

        if(fileList != null && !fileList.isEmpty()){
            fileService.deleteByFileId(fileList);
        }
    }

    @Transactional
    public Long answerSave(InquiryAnswerDto inquiryAnswerDto) {
        Inquiry inquiry = inquiryRepository.findById(inquiryAnswerDto.inquiryId()).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글이 없습니다."));
        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.save(inquiryAnswerDto.toSaveInquiryAnswerEntity(inquiry));
        isCompleteInquiryUpdate(inquiry.getId(), true);

        return inquiryAnswer.getId();
    }

    @Transactional
    public Long answerDelete(Long id, Long inquiryId) {
        InquiryAnswer inquiryAnswer = inquiryAnswerRepository.findById(id).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글의 답변이 없습니다."));
        inquiryAnswerRepository.deleteById(id);

        Inquiry originInquiry = inquiryRepository.findById(inquiryId).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글이 없습니다."));
        if(originInquiry.getInquiryAnswer().size() == 1){
            isCompleteInquiryUpdate(inquiryId, false);
        }

        return inquiryAnswer.getId();
    }

    @Transactional
    public void isCompleteInquiryUpdate(Long id, Boolean isComplete) {
        Inquiry originInquiry = inquiryRepository.findById(id).orElseThrow(() -> new CustomException(REQUIRED_ERROR, "문의 하신 게시글이 없습니다."));
        originInquiry.isCompleteInquiryUpdate(isComplete);
    }

    private void fileSave(Inquiry inquiry, List<MultipartFile> fileList){
        FilesLocation filesLocation = fileService.findByFileLocationId(1L);

        UserFileRequestDto userFileDto = UserFileRequestDto.builder()
                .postId(inquiry.getId())
                .file(filesLocation)
                .fileList(fileList)
                .build();

        fileService.saveFiles(userFileDto);
    }
}
