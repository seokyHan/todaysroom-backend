package com.todaysroom.user.controller;


import com.todaysroom.exception.CustomException;
import com.todaysroom.user.dto.UserSignupDto;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
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

        return userService.userLogin(userLoginDto);
    }

    @PostMapping("/logout")
    public ResponseEntity logout(@RequestBody UserTokenInfoDto userTokenInfoDto) {

        return userService.userLogout(userTokenInfoDto);
    }

    @PostMapping("/reissue")
    public ResponseEntity<UserTokenInfoDto> reissue(HttpServletRequest request){

        return userService.reissue(request);
    }

    @PostMapping("/signup")
    public ResponseEntity signup(UserSignupDto userSignupDto) throws Exception {

        return userService.signup(userSignupDto);
    }

    @PostMapping("/test")
    public ResponseEntity postMember(HttpServletRequest request) {

        return userService.refreshTokenTest(request);
    }



}
