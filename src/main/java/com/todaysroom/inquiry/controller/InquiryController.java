package com.todaysroom.inquiry.controller;

import com.todaysroom.inquiry.dto.InquiryUpdateDto;
import com.todaysroom.inquiry.exception.NoInquiryIdException;
import com.todaysroom.user.exception.NoUserException;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.service.InquiryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
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
    public ResponseEntity<List<InquiryResponseDto>> getInquiriesByUserId(@RequestParam("userId") Long userId) throws NoUserException {
        return ResponseEntity.ok(inquiryService.getInquiriesByUserId(userId));
    }

    @GetMapping("/detail/{id}")
    public ResponseEntity<InquiryResponseDto> getInquiriesById(@PathVariable("id") Long id) throws NoInquiryIdException {
        return ResponseEntity.ok(inquiryService.getInquiresById(id));
    }

    @GetMapping("/admin")
    public ResponseEntity<List<InquiryResponseDto>> getAllInquiries(){
        return ResponseEntity.ok(inquiryService.getAllInquiries());
    }

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestPart(value = "inquiryRequestDto") InquiryRequestDto inquiryRequestDto,
                                       @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList) throws NoUserException {

        InquiryResponseDto postResponseDto = inquiryService.save(inquiryRequestDto, fileList);
        return ResponseEntity.created(URI.create("/" + postResponseDto.id())).build();

    }

    @PutMapping("/update")
    public ResponseEntity<String> modify(@RequestPart(value = "inquiryUpdateDto") InquiryUpdateDto inquiryUpdateDto,
                                         @RequestPart(value = "fileList", required = false) List<MultipartFile> fileList) throws NoInquiryIdException {
        return new ResponseEntity<>("id :" + inquiryService.update(inquiryUpdateDto, fileList) + " 수정완료", HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> remove(@PathVariable("id") Long id) throws NoInquiryIdException {
        inquiryService.delete(id);

        return new ResponseEntity<>("id :" + id + " 삭제 완료", HttpStatus.NO_CONTENT);
    }
}
