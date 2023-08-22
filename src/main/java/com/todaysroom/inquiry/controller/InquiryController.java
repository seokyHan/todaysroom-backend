package com.todaysroom.inquiry.controller;

import com.todaysroom.exception.NoUserException;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
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

    @PostMapping("/create")
    public ResponseEntity<String> save(@RequestBody InquiryRequestDto inquiryRequestDto) throws NoUserException {
        InquiryResponseDto postResponseDto = inquiryService.save(inquiryRequestDto);
        return ResponseEntity.created(URI.create("/" + postResponseDto.id())).build();
    }
}
