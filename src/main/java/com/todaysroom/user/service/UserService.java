package com.todaysroom.user.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysroom.exception.CustomException;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.jwt.JwtFilter;
import com.todaysroom.user.jwt.TokenProvider;
import com.todaysroom.user.redis.RefreshToken;
import com.todaysroom.user.redis.RefreshTokenRedisRepository;
import com.todaysroom.user.repository.UserRepository;
import com.todaysroom.user.types.AuthType;
import com.todaysroom.user.types.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

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

        RefreshToken refreshToken = RefreshToken.builder()
                .email(userTokenInfoDto.userEmail())
                .token(userTokenInfoDto.refreshToken())
                .authorities(authentication.getAuthorities())
                .build();

        refreshTokenRedisRepository.save(refreshToken);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> reissue(HttpServletRequest request) {
        String cookieRefreshToken = request.getHeader(AuthType.REISSUE_REFRESHTOKEN_HEADER.getByItem()).substring(13);

        // Redis 저장된 RefreshToken 찾은 후 없으면 401 에러
        RefreshToken refreshTokenEntity = refreshTokenRedisRepository.findByToken(cookieRefreshToken)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        // RefreshToken이 만료 됐는지
        if (!tokenProvider.validateToken(refreshTokenEntity.getToken())) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }

        UserEntity userInfo = userRepository.findByUserEmail(refreshTokenEntity.getEmail());

        if (userInfo == null) {
            return ResponseEntity.badRequest().build();
        }

        UserTokenInfoDto tokenInfo = tokenProvider.generateToken(refreshTokenEntity.getEmail(), refreshTokenEntity.getAuthorities());

        RefreshToken refreshToken = RefreshToken.builder()
                .email(refreshTokenEntity.getEmail())
                .token(tokenInfo.refreshToken())
                .authorities(refreshTokenEntity.getAuthorities())
                .build();

        refreshTokenRedisRepository.save(refreshToken);

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
        log.info("header에서 가져오는 쿠키 {}", request.getHeader(AuthType.REISSUE_REFRESHTOKEN_HEADER.getByItem()).substring(13));

        Cookie[] cookies = request.getCookies();

        for(Cookie c : cookies){
            log.info("쿠키이름 : {} 쿠키 값 : {}", c.getName(), c.getValue());
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
