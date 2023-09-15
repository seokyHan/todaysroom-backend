package com.todaysroom.inquiry.controller;

import com.todaysroom.user.exception.NoUserException;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
@Slf4j
public class InquiryController {

    private final InquiryService inquiryService;

    @GetMapping
    public ResponseEntity<List<InquiryResponseDto>> getInquiries(@RequestParam("userId") Long userId) throws NoUserException {
        return ResponseEntity.ok(inquiryService.getInquiries(userId));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<InquiryResponseDto>> getAllInquiries(){
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @PostMapping(value ="/create")
    public ResponseEntity<String> save(@RequestPart(value = "inquiryRequestDto") InquiryRequestDto inquiryRequestDto,
                                       @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList) throws NoUserException {
        log.info("id : {}", inquiryRequestDto.userId());
        log.info("type : {}", inquiryRequestDto.inquiryType());
        log.info("title : {}", inquiryRequestDto.title());
        log.info("content : {}", inquiryRequestDto.content());
        for(MultipartFile f : fileList){
            log.info("fileList : {}", f.getOriginalFilename());
        }


        if(!fileList.isEmpty()){
            // 파일 업로드 추가 로직 구현
       }
        InquiryResponseDto postResponseDto = inquiryService.save(inquiryRequestDto);
        return ResponseEntity.created(URI.create("/" + postResponseDto.id())).build();
    }
}
