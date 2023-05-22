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
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
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

        return getUserTokenInfoDtoResponseEntity(accessToken, refreshToken, userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> reissue(HttpServletRequest request) {
        //SecurityContextHolder.getContext().getAuthentication()
        // AccessToken decode 후 payload 값 추출
        String accessToken = tokenProvider.resolveToken(request);
        HashMap<String, String> payloadMap = getPayloadByToken(accessToken);
        String email = payloadMap.get("sub");

        // Redis 저장된 RefreshToken 찾은 후 없으면 401 에러
        RefreshToken refreshTokenEntity = refreshTokenRedisRepository.findById(email)
                .orElseThrow(() -> new CustomException(ErrorCode.UNAUTHORIZED));

        // RefreshToken이 만료 됐는지
        if (!tokenProvider.validateToken(refreshTokenEntity.getToken())) {
            throw new CustomException(ErrorCode.JWT_REFRESH_TOKEN_EXPIRED);
        }

        UserEntity userInfo = userRepository.findByUserEmail(email);

        if (userInfo == null) {
            return ResponseEntity.badRequest().build();
        }

        String newAccessToken = tokenProvider.createAccessToken(SecurityContextHolder.getContext().getAuthentication());
        String newRefreshToken = tokenProvider.createRefreshToken(SecurityContextHolder.getContext().getAuthentication());

        UserTokenInfoDto userTokenInfoDto = UserTokenInfoDto.from(userInfo, newAccessToken, newRefreshToken);

        return getUserTokenInfoDtoResponseEntity(accessToken, newRefreshToken, userTokenInfoDto);

    }

    private ResponseEntity<UserTokenInfoDto> getUserTokenInfoDtoResponseEntity(String accessToken, String refreshToken, UserTokenInfoDto userTokenInfoDto) {
        refreshTokenRedisRepository.save(
                RefreshToken.builder().
                        email(userTokenInfoDto.userEmail()).
                        token(refreshToken)
                        .build()
        );

        ResponseCookie cookie = ResponseCookie.from("refreshToken", refreshToken)
                .maxAge(1209600)
                .path("/")
                //.secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        HttpHeaders httpHeaders = addHttpHeaders(accessToken, cookie.toString());


        return new ResponseEntity<>(userTokenInfoDto, httpHeaders, HttpStatus.OK);
    }
    private HttpHeaders addHttpHeaders(String accessToken, String refreshToken){
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(TokenProvider.AUTHORIZATION_HEADER, TokenProvider.AUTHORIZATION_HEADER + accessToken);
        httpHeaders.add(TokenProvider.REFRESHTOKEN_HEADER, TokenProvider.REFRESHTOKEN_HEADER + refreshToken);

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
