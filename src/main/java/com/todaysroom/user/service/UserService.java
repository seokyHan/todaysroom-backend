package com.todaysroom.user.service;


import com.todaysroom.exception.CustomException;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.jwt.TokenProvider;
import com.todaysroom.user.repository.UserRepository;
import com.todaysroom.user.types.AuthType;
import com.todaysroom.user.types.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.util.Date;

import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;

    @Transactional
    public ResponseEntity<UserTokenInfoDto> userLogin(UserLoginDto userLoginDto){

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.userEmail(), userLoginDto.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserTokenInfoDto tokenInfo = tokenProvider.generateToken(authentication);
        UserEntity userEntity = userRepository.findByUserEmail(userLoginDto.userEmail());

        UserTokenInfoDto userTokenInfoDto = UserTokenInfoDto.builder()
                .accessToken(tokenInfo.accessToken())
                .refreshToken(tokenInfo.refreshToken())
                .id(userEntity.getId())
                .userEmail(userEntity.getUserEmail())
                .userName(userEntity.getUserName())
                .nickname(userEntity.getNickname())
                .recentSearch(userEntity.getRecentSearch())
                .build();

        redisTemplate.opsForValue()
                .set(AuthType.REFRESHTOKEN_KEY.getByItem() + userTokenInfoDto.userEmail(),
                        userTokenInfoDto.refreshToken(),
                        tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                        TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity userLogout(UserTokenInfoDto userTokenInfoDto){
        // 1. Access Token 에서 User email 을 가져옴.
        Authentication authentication = tokenProvider.getAuthentication(userTokenInfoDto.accessToken());
        String refreshToken = (String)redisTemplate.opsForValue().get(AuthType.REFRESHTOKEN_KEY.getByItem() + authentication.getName());

        if(refreshToken != null){
            redisTemplate.delete(AuthType.REFRESHTOKEN_KEY.getByItem() + authentication.getName());
        }

        Long now = new Date().getTime();
        Long expiration = tokenProvider.getExpiration(userTokenInfoDto.accessToken());

        redisTemplate.opsForValue()
                .set(userTokenInfoDto.accessToken(), "logout", (expiration - now), TimeUnit.MILLISECONDS);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> reissue(HttpServletRequest request) {
        String headerCookie = request.getHeader(AuthType.REISSUE_HEADER.getByItem());
        int start = headerCookie.indexOf(AuthType.HEADER_VALUE.getByItem()) + AuthType.HEADER_VALUE.getByItem().length();
        int end = headerCookie.indexOf(";", start);
        String cookieRefreshToken = headerCookie.substring(start,end);

        Authentication authentication = tokenProvider.getAuthentication(cookieRefreshToken);
        String refreshToken = (String)redisTemplate.opsForValue().get(AuthType.REFRESHTOKEN_KEY.getByItem() + authentication.getName());

        // Redis 저장된 RefreshToken 찾은 후 없으면 401 에러
        if(ObjectUtils.isEmpty(refreshToken)){
            throw  new CustomException(ErrorCode.UNAUTHORIZED);
        }

        // RefreshToken이 만료 됐는지
        if (!tokenProvider.validateToken(refreshToken)) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }

        UserEntity userInfo = userRepository.findByUserEmail(authentication.getName());

        if (userInfo == null) {
            return ResponseEntity.badRequest().build();
        }

        UserTokenInfoDto tokenInfo = tokenProvider.generateToken(authentication);

        redisTemplate.opsForValue()
                .set(AuthType.REFRESHTOKEN_KEY.getByItem() + authentication.getName(),
                        tokenInfo.refreshToken(),
                        tokenProvider.getExpiration(tokenInfo.refreshToken()),
                        TimeUnit.MILLISECONDS);

        UserTokenInfoDto userTokenInfoDto = UserTokenInfoDto.builder()
                .accessToken(tokenInfo.accessToken())
                .refreshToken(tokenInfo.refreshToken())
                .id(userInfo.getId())
                .userEmail(userInfo.getUserEmail())
                .userName(userInfo.getUserName())
                .nickname(userInfo.getNickname())
                .recentSearch(userInfo.getRecentSearch())
                .build();

        return setResponseData(userTokenInfoDto);
    }

    public ResponseEntity refreshTokenTest (HttpServletRequest request){
        String cookie = request.getHeader(AuthType.REISSUE_HEADER.getByItem());

        int start = cookie.indexOf("refreshToken=") + "refreshToken=".length();
        int end = cookie.indexOf(";", start);
        String rtk = cookie.substring(start,end);
        log.info("header에서 RTK : {}", rtk);
        Authentication authentication = tokenProvider.getAuthentication(rtk);
        String refreshToken = (String)redisTemplate.opsForValue().get(AuthType.REFRESHTOKEN_KEY.getByItem() + authentication.getName());
        log.info("redis RTK : {}", rtk);
        if(ObjectUtils.isEmpty(refreshToken)){
            log.info("비어있음");
        }

        // RefreshToken이 만료 됐는지
        if (!tokenProvider.validateToken(refreshToken)) {
            log.info("만료");
        }

        return new ResponseEntity<>(HttpStatus.OK);
    }

    private ResponseEntity<UserTokenInfoDto> setResponseData(UserTokenInfoDto userTokenInfoDto) {
        ResponseCookie cookie = ResponseCookie.from(AuthType.REFRESHTOKEN_KEY.getByItem(), userTokenInfoDto.refreshToken())
                .maxAge(1209600)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        HttpHeaders httpHeaders = addHttpHeaders(userTokenInfoDto.accessToken(), cookie.toString());


        return new ResponseEntity<>(userTokenInfoDto, httpHeaders, HttpStatus.OK);
    }
    private HttpHeaders addHttpHeaders(String accessToken, String refreshToken){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TokenProvider.AUTHORIZATION_HEADER, accessToken);
        httpHeaders.add(TokenProvider.REFRESHTOKEN_HEADER, refreshToken);

        return httpHeaders;
    }


}
