package com.todaysroom.user.service;


import com.todaysroom.global.exception.CustomException;
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

import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static com.todaysroom.global.exception.code.AuthResponseCode.*;
import static com.todaysroom.global.types.AuthType.*;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;
import static org.springframework.http.HttpHeaders.SET_COOKIE;

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

        setTokenInRedis(REFRESH_TOKEN + userTokenInfoDto.userEmail(),
                userTokenInfoDto.refreshToken(),
                tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity userLogout(HttpServletRequest request){
        String accessToken = tokenProvider.resolveToken(request);
        Authentication authentication = tokenProvider.getAuthentication(accessToken);
        String refreshToken = (String)redisTemplate.opsForValue().get(REFRESH_TOKEN + authentication.getName());

        if(refreshToken != null) redisTemplate.delete(REFRESH_TOKEN + authentication.getName());

        ZonedDateTime zdt = ZonedDateTime.of(LocalDateTime.now(), ZoneId.systemDefault());
        long now = zdt.toInstant().toEpochMilli();
        long expiration = tokenProvider.getExpiration(accessToken);

        setTokenInRedis(accessToken, "logout", (expiration - now), TimeUnit.MILLISECONDS);

        return ResponseEntity.ok()
                .header(SET_COOKIE, setLogOutCookie().toString())
                .body(null);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> reissue(String cookieRefreshToken) {
        Authentication authentication = tokenProvider.getAuthentication(cookieRefreshToken);

        String refreshToken = (String)redisTemplate.opsForValue().get(REFRESH_TOKEN + authentication.getName());
        if (refreshToken == null || ObjectUtils.isEmpty(refreshToken)) throw new CustomException(REFRESH_TOKEN_EXPIRED, "refreshToken 만료"); // RefreshToken이 만료 여부

        UserEntity userInfo = userRepository.findByUserEmail(authentication.getName());
        if (userInfo == null) throw new CustomException(USER_NOT_FOUND, "해당 User를 찾을 수 없습니다.");

        UserTokenInfoDto userTokenInfoDto = generateUserTokenInfo(authentication, userInfo);

        setTokenInRedis(REFRESH_TOKEN.getItem() + authentication.getName(),
                userTokenInfoDto.refreshToken(),
                tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity<UserTokenInfoDto> socialUserSignUp(){
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        String accessToken = tokenProvider.oAuth2CreateAccessToken(email, Role.USER);
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken(email, Role.USER);

        UserEntity userEntity = userRepository.findByUserEmail(email);
        userEntity.socialUserUpdate(Role.USER);

        Authority authority = authorityRepository.findById(2L).orElseThrow(() -> new CustomException(UNAUTHORIZED, "권한이 존재하지 않습니다."));
        UserAuthority userAuthority = userAuthorityRepository.findByUserEntity(userEntity);
        userAuthority.authUpdate(authority);

        userAuthorityRepository.save(userAuthority);
        userRepository.save(userEntity);

        UserTokenInfoDto userTokenInfoDto = generateUserTokenInfo(accessToken, refreshToken, userEntity);

        setTokenInRedis(REFRESH_TOKEN.getItem() + userTokenInfoDto.userEmail(),
                userTokenInfoDto.refreshToken(),
                tokenProvider.getExpiration(userTokenInfoDto.refreshToken()),
                TimeUnit.MILLISECONDS);

        return setResponseData(userTokenInfoDto);
    }

    @Transactional
    public ResponseEntity signup(UserSignupDto userSignupDto) {
        UserEntity signupUser = userSignupDto.toUserEntity();

        UserEntity userEntity = UserEntity.builder()
                .activated(true)
                .userEmail(signupUser.getUserEmail())
                .password(passwordEncoder.encode(signupUser.getPassword()))
                .userName(signupUser.getUserName())
                .nickname(signupUser.getNickname())
                .build();

        Authority authority = authorityRepository.findById(2L).orElseThrow(() -> new CustomException(UNAUTHORIZED, "권한이 존재하지 않습니다."));
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
        ResponseCookie cookie = ResponseCookie.from(REFRESH_TOKEN.getItem(), userTokenInfoDto.refreshToken())
                .maxAge(12096000)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(AUTHORIZATION, userTokenInfoDto.accessToken());
        httpHeaders.add(SET_COOKIE, cookie.toString());

        return ResponseEntity.ok()
                .headers(httpHeaders)
                .body(userTokenInfoDto);
    }

    private ResponseCookie setLogOutCookie(){
        return ResponseCookie.from(REFRESH_TOKEN.getItem(), "")
                .maxAge(1)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();
    }

    private void setTokenInRedis(String key, String value, long expiration, TimeUnit time){
        redisTemplate.opsForValue()
                .set(key, value, expiration, time);
    }

    private UserTokenInfoDto generateUserTokenInfo(Authentication authentication, UserEntity userEntity) {
        UserTokenInfoDto tokenInfo = tokenProvider.generateToken(authentication);
        String userAuthorities = userEntity.getAuthorities().stream()
                .map(authority -> authority.getAuth().getAuthorityName())
                .collect(Collectors.joining(","));

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
        String userAuthorities = userEntity.getAuthorities().stream()
                .map(authority -> authority.getAuth().getAuthorityName())
                .collect(Collectors.joining(","));

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
