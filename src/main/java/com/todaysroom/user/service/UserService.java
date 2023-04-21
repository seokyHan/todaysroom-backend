package com.todaysroom.user.service;

import com.todaysroom.user.dto.UserInfoDto;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.jwt.JwtFilter;
import com.todaysroom.user.jwt.TokenProvider;
import com.todaysroom.user.redis.RefreshToken;
import com.todaysroom.user.redis.RefreshTokenRedisRepository;
import com.todaysroom.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Transactional
    public ResponseEntity<UserTokenInfoDto> getUserLoginInfo(UserLoginDto userLoginDto){

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.userEmail(), userLoginDto.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        // 기존 토큰 값이 존재하면 변경, 없으면 새로 생성
        refreshTokenRedisRepository.findById(userLoginDto.userEmail())
                .ifPresentOrElse(
                        (tokenEntity)->tokenEntity.changeToken(refreshToken),
                        ()->refreshTokenRedisRepository.save(
                                RefreshToken.builder().
                                        id(userLoginDto.userEmail()).
                                        token(refreshToken)
                                        .build()
                        )
                );

        UserInfoDto userInfo = UserInfoDto.from(userRepository.findByUserEmail(userLoginDto.userEmail()));
        UserTokenInfoDto userTokenInfoDto = UserTokenInfoDto.from(userInfo, accessToken, refreshToken);

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JwtFilter.AUTHORIZATION_HEADER, JwtFilter.AUTHORIZATION_HEADER + accessToken);

        return new ResponseEntity<>(userTokenInfoDto, httpHeaders, HttpStatus.OK);
    }

}
