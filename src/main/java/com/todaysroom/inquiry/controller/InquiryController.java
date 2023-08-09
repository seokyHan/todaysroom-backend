package com.todaysroom.inquiry.controller;

import com.todaysroom.exception.NoUserException;
import com.todaysroom.inquiry.dto.InquiryRequestDto;
import com.todaysroom.inquiry.dto.InquiryResponseDto;
import com.todaysroom.inquiry.service.InquiryService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;

@RestController
@RequiredArgsConstructor
@RequestMapping("/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    @PostMapping("/create")
    public ResponseEntity<String> save(@RequestBody InquiryRequestDto inquiryRequestDto) throws NoUserException {
        InquiryResponseDto postResponseDto = inquiryService.save(inquiryRequestDto);
        return ResponseEntity.created(URI.create("/" + postResponseDto.id())).build();
    }
}
