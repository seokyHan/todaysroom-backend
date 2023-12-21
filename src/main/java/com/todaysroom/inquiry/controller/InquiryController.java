package com.todaysroom.inquiry.controller;

import com.todaysroom.inquiry.dto.InquiryAnswerDto;
import com.todaysroom.inquiry.dto.InquiryUpdateDto;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
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
    public ResponseEntity<List<InquiryResponseDto>> getInquiriesByUserId(@RequestParam("userId") Long userId) {
        return ResponseEntity.ok(inquiryService.getInquiriesByUserId(userId));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<InquiryResponseDto> getInquiriesById(@PathVariable("id") Long id) {
        return ResponseEntity.ok(inquiryService.getInquiresById(id));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<InquiryResponseDto>> getAllInquiries(){
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestPart(value = "inquiryRequestDto") InquiryRequestDto inquiryRequestDto,
                                       @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList) {

        InquiryResponseDto postResponseDto = inquiryService.save(inquiryRequestDto, fileList);
        return ResponseEntity.created(URI.create("/" + postResponseDto.id())).build();

    }

    @PutMapping("/update")
    public ResponseEntity<String> modify(@RequestPart(value = "inquiryUpdateDto") InquiryUpdateDto inquiryUpdateDto,
                                         @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList) {
        return new ResponseEntity<>("id :" + inquiryService.update(inquiryUpdateDto, fileList) + " 수정완료", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> remove(@PathVariable("id") Long id) {
        inquiryService.delete(id);

        return new ResponseEntity<>("id :" + id + " 삭제 완료", HttpStatus.NO_CONTENT);
    }

    @PostMapping("/answer")
    public ResponseEntity<String> inquiryAnswerSave(@RequestBody InquiryAnswerDto inquiryAnswerDto){

        return ResponseEntity.ok("Inquiry Answer ID : " + inquiryService.answerSave(inquiryAnswerDto) + " 답변 저장 완료");
    }

    @DeleteMapping("/answer")
    public ResponseEntity<String> inquiryAnswerDelete(@RequestParam("id") Long id, @RequestParam("inquiryId") Long inquiryId) {

        return ResponseEntity.ok("Inquiry Answer ID : " + inquiryService.answerDelete(id, inquiryId) + " 답변 삭제 완료");
    }

}
