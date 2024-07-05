package com.todaysroom.user.controller;


import com.todaysroom.user.dto.UserSignupDto;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.service.UserService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import org.springframework.web.bind.annotation.*;

import java.util.Map;

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
    public ResponseEntity logout(HttpServletRequest request) {
        return userService.userLogout(request);
    }

    @PostMapping("/reissue")
    public ResponseEntity<UserTokenInfoDto> reissue(@CookieValue("refreshToken") String cookieRefreshToken, HttpServletResponse response){
        return userService.reissue(cookieRefreshToken, response);
    }

    @PostMapping("/signup")
    public ResponseEntity signup(@Valid @RequestBody UserSignupDto userSignupDto){
        return userService.signup(userSignupDto);
    }

    @GetMapping("/email-check")
    public ResponseEntity<Boolean> isDuplicateEmail(@RequestParam("userEmail") String userEmail){
        return new ResponseEntity(userService.validateDuplicatedEmail(userEmail), HttpStatus.OK);
    }

}
