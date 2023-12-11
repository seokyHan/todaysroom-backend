package com.todaysroom.user.service;


import com.todaysroom.global.exception.CustomException;
import com.todaysroom.oauth2.exception.AuthorityNotFoundException;
import com.todaysroom.global.types.Role;
import com.todaysroom.user.dto.UserLoginDto;
import com.todaysroom.user.dto.UserSignupDto;
import com.todaysroom.user.dto.UserTokenInfoDto;
import com.todaysroom.user.entity.Authority;
import com.todaysroom.user.entity.UserAuthority;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.global.security.jwt.TokenProvider;
import com.todaysroom.user.repository.AuthorityRepository;
import com.todaysroom.user.repository.UserAuthorityRepository;
import com.todaysroom.user.repository.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.ObjectUtils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.todaysroom.global.exception.code.AuthResponseCode.*;
import static com.todaysroom.global.types.AuthType.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private final RedisTemplate redisTemplate;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public ResponseEntity<UserTokenInfoDto> userLogin(UserLoginDto userLoginDto){

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(userLoginDto.userEmail(), userLoginDto.password());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserEntity userEntity = userRepository.findByUserEmail(userLoginDto.userEmail());

        UserTokenInfoDto userTokenInfoDto = generateUserTokenInfo(authentication, userEntity);

        setTokenInRedis(REFRESHTOKEN_KEY + userTokenInfoDto.userEmail(),
                userTokenInfoDto.refreshToken(),
                tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity userLogout(UserTokenInfoDto userTokenInfoDto){
        // 1. Access Token 에서 User email 을 가져옴.
        Authentication authentication = tokenProvider.getAuthentication(userTokenInfoDto.accessToken());
        String refreshToken = (String)redisTemplate.opsForValue().get(REFRESHTOKEN_KEY + authentication.getName());

        if(refreshToken != null){
            redisTemplate.delete(REFRESHTOKEN_KEY + authentication.getName());
        }

        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        long now = zdt.toInstant().toEpochMilli();
        long expiration = tokenProvider.getExpiration(userTokenInfoDto.accessToken());

        setTokenInRedis(userTokenInfoDto.accessToken(), "logout", (expiration - now), TimeUnit.MILLISECONDS);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> reissue(HttpServletRequest request) {
        String headerCookie = request.getHeader(REISSUE_HEADER.getItem());
        int start = headerCookie.indexOf(HEADER_VALUE.getItem()) + HEADER_VALUE.getItem().length();
        int end = headerCookie.indexOf(";", start);
        String cookieRefreshToken = headerCookie.substring(start,end);

        Authentication authentication = tokenProvider.getAuthentication(cookieRefreshToken);
        String refreshToken = (String)redisTemplate.opsForValue().get(REFRESHTOKEN_KEY + authentication.getName());

        // Redis 저장된 RefreshToken 찾은 후 없으면 401 에러
        if(ObjectUtils.isEmpty(refreshToken)){
            throw new CustomException("토큰 정보 미존재", UNAUTHORIZED);
        }

        // RefreshToken이 만료 됐는지
        if (redisTemplate.opsForValue().get(refreshToken) == null) {
            throw new CustomException("refreshToken 만료", TOKEN_EXPIRED);
        }

        UserEntity userInfo = userRepository.findByUserEmail(authentication.getName());

        if (userInfo == null) {
            throw new CustomException("해당 User를 찾을 수 없습니다.", USER_NOT_FOUND);
        }

        UserTokenInfoDto userTokenInfoDto = generateUserTokenInfo(authentication, userInfo);

        setTokenInRedis(REFRESHTOKEN_KEY.getItem() + authentication.getName(),
                userTokenInfoDto.refreshToken(),
                tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> socialUserSignUp() throws AuthorityNotFoundException{
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String accessToken = tokenProvider.oAuth2CreateAccessToken(email, Role.USER);
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken(email, Role.USER);

        UserEntity userEntity = userRepository.findByUserEmail(email);
        userEntity.socialUserUpdate(Role.USER);

        Authority authority = authorityRepository.findById(2L).orElseThrow(AuthorityNotFoundException::new);
        UserAuthority userAuthority = userAuthorityRepository.findByUserEntity(userEntity);
        userAuthority.authUpdate(authority);

        userAuthorityRepository.save(userAuthority);
        userRepository.save(userEntity);

        UserTokenInfoDto userTokenInfoDto = generateUserTokenInfo(accessToken, refreshToken, userEntity);

        setTokenInRedis(REFRESHTOKEN_KEY.getItem() + userTokenInfoDto.userEmail(),
                userTokenInfoDto.refreshToken(),
                tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity signup(UserSignupDto userSignupDto) throws AuthorityNotFoundException {
        UserEntity signupUser = userSignupDto.toUserEntity();

        UserEntity userEntity = UserEntity.builder()
                .activated(true)
                .userEmail(signupUser.getUserEmail())
                .password(passwordEncoder.encode(signupUser.getPassword()))
                .userName(signupUser.getUserName())
                .nickname(signupUser.getNickname())
                .build();

        Authority authority = authorityRepository.findById(2L).orElseThrow(AuthorityNotFoundException::new);
        UserAuthority userAuthority = UserAuthority.builder()
                        .userEntity(userEntity)
                        .auth(authority)
                        .build();
        userAuthorityRepository.save(userAuthority);
        userRepository.save(userEntity);

        return new ResponseEntity<>(HttpStatus.OK);
    }

    public boolean validateDuplicatedEmail (String userEmail){
        if(userRepository.existsByUserEmail(userEmail)){
            return false;
        }
        return true;
    }

    private ResponseEntity<UserTokenInfoDto> setResponseData(UserTokenInfoDto userTokenInfoDto) {
        ResponseCookie cookie = ResponseCookie.from(REFRESHTOKEN_KEY.getItem(), userTokenInfoDto.refreshToken())
                .maxAge(12096000)
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
        httpHeaders.add(AUTHORIZATION, accessToken);
        httpHeaders.add(TokenProvider.COOKIE_HEADER, refreshToken);

        return httpHeaders;
    }

    private void setTokenInRedis(String key, String value, long expiration, TimeUnit time){
        redisTemplate.opsForValue()
                .set(key, value, expiration, time);
    }

    private UserTokenInfoDto generateUserTokenInfo(Authentication authentication, UserEntity userEntity) {
        UserTokenInfoDto tokenInfo = tokenProvider.generateToken(authentication);
        List<String> userAuthorities = userEntity.getAuthorities().stream()
                .map(authority -> authority.getAuth().getAuthorityName())
                .collect(Collectors.toList());

        return UserTokenInfoDto.builder()
                .accessToken(tokenInfo.accessToken())
                .refreshToken(tokenInfo.refreshToken())
                .id(userEntity.getId())
                .userEmail(userEntity.getUserEmail())
                .userName(userEntity.getUserName())
                .nickname(userEntity.getNickname())
                .recentSearch(userEntity.getRecentSearch())
                .authorities(userAuthorities)
                .build();
    }

    private UserTokenInfoDto generateUserTokenInfo(String accessToken, String refreshToken, UserEntity userEntity) {
        List<String> userAuthorities = userEntity.getAuthorities().stream()
                .map(authority -> authority.getAuth().getAuthorityName())
                .collect(Collectors.toList());

        return UserTokenInfoDto.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .id(userEntity.getId())
                .userEmail(userEntity.getUserEmail())
                .userName(userEntity.getUserName())
                .nickname(userEntity.getNickname())
                .recentSearch(userEntity.getRecentSearch())
                .authorities(userAuthorities)
                .build();
    }


}
