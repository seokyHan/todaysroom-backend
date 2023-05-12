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
import com.todaysroom.user.types.ErrorCode;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

import java.util.Base64;
import java.util.HashMap;
import java.util.Optional;

@Service
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

        String accessToken = tokenProvider.createAccessToken(authentication);
        String refreshToken = tokenProvider.createRefreshToken(authentication);

        UserTokenInfoDto userTokenInfoDto = UserTokenInfoDto.from(userRepository.findByUserEmail(userLoginDto.userEmail()), accessToken, refreshToken);
        refreshTokenRedisRepository.save(
                RefreshToken.builder().
                        email(userTokenInfoDto.userEmail()).
                        token(refreshToken)
                        .build()
        );

        HttpHeaders httpHeaders = addHttpHeaders(accessToken);

        return new ResponseEntity<>(userTokenInfoDto, httpHeaders, HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> reissue(HttpServletRequest request) {
        //SecurityContextHolder.getContext().getAuthentication()
        // AccessToken decode 후 payload 값 추출
        String accessToken = tokenProvider.resolveToken(request);
        HashMap<String, String> payloadMap = getPayloadByToken(accessToken);
        String email = payloadMap.get("sub");

        // Redis 저장된 RefreshToken 찾은 후 없으면 401 에러
        Optional<RefreshToken> refreshToken = refreshTokenRedisRepository.findById(email);
        refreshToken.orElseThrow(()-> new CustomException(ErrorCode.UNAUTHORIZED));

        // RefreshToken이 만료 됐는지
        boolean isRefreshTokenValid = tokenProvider.validateToken(refreshToken.get().getToken());
        if(isRefreshTokenValid){

        }



        return new ResponseEntity<>(HttpStatus.OK);
    }

    private HttpHeaders addHttpHeaders(String accessToken){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TokenProvider.AUTHORIZATION_HEADER, TokenProvider.AUTHORIZATION_HEADER + accessToken);

        return httpHeaders;
    }

    private HashMap<String, String> getPayloadByToken(String token) {
        try {
            String[] splitJwt = token.split("\\.");

            Base64.Decoder decoder = Base64.getDecoder();
            String payload = new String(decoder.decode(splitJwt[1] .getBytes()));

            return new ObjectMapper().readValue(payload, HashMap.class);
        } catch (JsonProcessingException e) {
            //log.error(e.getMessage());
            return null;
        }
    }

}
