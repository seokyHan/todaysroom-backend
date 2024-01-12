package com.todaysroom.oauth2.handler;

import com.todaysroom.oauth2.common.CustomOAuth2User;
import com.todaysroom.global.types.Role;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.global.security.jwt.TokenProvider;
import com.todaysroom.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

import static com.todaysroom.global.types.AuthType.COOKIE_HEADER;
import static com.todaysroom.global.types.AuthType.REFRESHTOKEN_KEY;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;
    @Value("${oauth.redirect-url-guest}")
    private String guestRedirectUrl;
    @Value("${oauth.redirect-url-user}")
    private String userRedirectUrl;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            //최초 로그인 시
            if(oAuth2User.getRole() == Role.GUEST){
                String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail(), Role.GUEST);

                setCookie(response, "isFirst", "true");
                setTokenToCookie(response, "auth", accessToken, 36000, true, false);
                response.sendRedirect(userRedirectUrl);
            }
            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성

        } catch (Exception e){
            log.info("error : {}", e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException{
        String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail(), Role.USER);
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken(oAuth2User.getEmail(), Role.USER);
        String redisRtk = (String)redisTemplate.opsForValue().get(REFRESHTOKEN_KEY.getItem() + oAuth2User.getEmail());

        if(StringUtils.hasText(redisRtk)){
            redisTemplate.delete(REFRESHTOKEN_KEY.getItem() + oAuth2User.getEmail());
        }

        redisTemplate.opsForValue()
                .set(REFRESHTOKEN_KEY.getItem() + oAuth2User.getEmail(),
                        refreshToken,
                        tokenProvider.getExpiration(refreshToken),
                        TimeUnit.MILLISECONDS);

        UserEntity userEntity = userRepository.findByUserEmail(oAuth2User.getEmail());

        setCookie(response, "id", userEntity.getId().toString());
        setCookie(response, "nickname", userEntity.getNickname());
        setCookie(response, "recentSearch", userEntity.getRecentSearch() == null ? "" : userEntity.getRecentSearch());
        setCookie(response, "socialLogin", "success");
        setTokenToCookie(response, "auth", accessToken, 36000, true, false);
        setTokenToCookie(response, REFRESHTOKEN_KEY.getItem(), refreshToken, 1209600, true, true);

        response.sendRedirect(userRedirectUrl);
    }

    private void setCookie(HttpServletResponse response, String name, String value) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(name, URLEncoder.encode(value,"UTF-8"))
                .path("/")
                .build();

        response.addHeader(COOKIE_HEADER.getItem(), cookie.toString());
    }

    private void setTokenToCookie(HttpServletResponse response,
                                  String name,
                                  String value,
                                  int maxAge,
                                  boolean isSecure,
                                  boolean isHttp) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(name, URLEncoder.encode(value,"UTF-8"))
                .maxAge(maxAge)
                .path("/")
                .sameSite("None")
                .secure(isSecure)
                .httpOnly(isHttp)
                .build();

        response.addHeader(COOKIE_HEADER.getItem(), cookie.toString());
    }


}
