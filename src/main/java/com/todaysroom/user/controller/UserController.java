package com.todaysroom.user.controller;


import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/users")
public class UserController {
    private final UserService userService;


    @PostMapping("/login")
    public ResponseEntity<UserTokenInfoDto> login(@Valid @RequestBody UserLoginDto userLoginDto) {

        return userService.getUserLoginInfo(userLoginDto);
    }




}
