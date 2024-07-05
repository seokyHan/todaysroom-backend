package com.todaysroom.oauth2.handler;

import com.todaysroom.global.exception.CustomException;
import com.todaysroom.global.types.AuthType;
import com.todaysroom.oauth2.common.CustomOAuth2User;
import com.todaysroom.global.types.Role;
import com.todaysroom.oauth2.props.OAuth2Properties;
import com.todaysroom.user.entity.Authority;
import com.todaysroom.user.entity.UserAuthority;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.global.security.jwt.TokenProvider;
import com.todaysroom.user.repository.AuthorityRepository;
import com.todaysroom.user.repository.UserAuthorityRepository;
import com.todaysroom.user.repository.UserRepository;
import com.todaysroom.user.service.UserService;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static com.todaysroom.global.exception.code.AuthResponseCode.UNAUTHORIZED;
import static com.todaysroom.global.types.Role.USER;
import static org.springframework.http.HttpHeaders.SET_COOKIE;
import static com.todaysroom.global.types.AuthType.REFRESH_TOKEN;
import static com.todaysroom.global.types.AuthType.AUTH;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final RedisTemplate redisTemplate;
    private final OAuth2Properties oAuth2Properties;


    @Override
    @Transactional
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            //최초 로그인 시
            if(oAuth2User.getRole() == Role.GUEST){
                setCookie(response, "isFirst", "true");
                updateSocialUserAuthority(oAuth2User);
            }

            loginSuccess(response, oAuth2User);

        } catch (Exception e){
            log.info("OAuth2 error : {}", e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException{
        String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail(), USER);
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken(oAuth2User.getEmail(), USER);
        String redisRtk = (String)redisTemplate.opsForValue().get(REFRESH_TOKEN + oAuth2User.getEmail());

        if(StringUtils.hasText(redisRtk)){
            redisTemplate.delete(REFRESH_TOKEN + oAuth2User.getEmail());
        }

        redisTemplate.opsForValue()
                .set(REFRESH_TOKEN + oAuth2User.getEmail(),
                        refreshToken,
                        tokenProvider.getExpiration(refreshToken),
                        TimeUnit.MILLISECONDS);

        UserEntity userEntity = userRepository.findByUserEmail(oAuth2User.getEmail());

        setCookie(response, "id", userEntity.getId().toString());
        setCookie(response, "nickname", userEntity.getNickname());
        setCookie(response, "recentSearch", userEntity.getRecentSearch() == null ? "" : userEntity.getRecentSearch());
        setTokenToCookie(response, AUTH, accessToken, oAuth2Properties.accessTokenValidityInMilliseconds(), true, false);
        setTokenToCookie(response, REFRESH_TOKEN, refreshToken, oAuth2Properties.refreshTokenValidityInMilliseconds(), true, true);

        response.sendRedirect(oAuth2Properties.redirectUrl());
    }

    private void setCookie(HttpServletResponse response, String name, String value) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(name, URLEncoder.encode(value,"UTF-8"))
                .path("/")
                .build();

        response.addHeader(SET_COOKIE, cookie.toString());
    }

    private void setTokenToCookie(HttpServletResponse response,
                                  AuthType authType,
                                  String value,
                                  long maxAge,
                                  boolean isSecure,
                                  boolean isHttp) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(authType.getItem(), URLEncoder.encode(value,"UTF-8"))
                .maxAge(maxAge)
                .path("/")
                .sameSite("None")
                .secure(isSecure)
                .httpOnly(isHttp)
                .build();

        response.addHeader(SET_COOKIE, cookie.toString());
    }

    @Transactional
    public void updateSocialUserAuthority(CustomOAuth2User oAuth2User){
        UserEntity userEntity = userRepository.findByUserEmail(oAuth2User.getEmail());
        userEntity.socialUserUpdate(USER);

        Authority authority = authorityRepository.findById(2L).orElseThrow(() -> new CustomException(UNAUTHORIZED, "권한이 존재하지 않습니다."));
        UserAuthority userAuthority = userAuthorityRepository.findByUserEntity(userEntity);
        userAuthority.authUpdate(authority);

        userAuthorityRepository.save(userAuthority);
        userRepository.save(userEntity);
    }


}
