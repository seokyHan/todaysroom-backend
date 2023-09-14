package com.todaysroom.oauth2.handler;

import com.todaysroom.oauth2.CustomOAuth2User;
import com.todaysroom.types.AuthType;
import com.todaysroom.types.Role;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.jwt.TokenProvider;
import com.todaysroom.user.repository.UserRepository;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

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
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            //최초 로그인 시
//            if (oAuth2User.getRole() == Role.GUEST) {
//                String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail(), Role.GUEST);
//
//                setTokenToCookie(response, "auth", accessToken, 36000, true, false);
//                setCookie(response, "oauth2", "success");
//
//                response.sendRedirect(guestRedirectUrl); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
//            }
            loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성

        } catch (Exception e){
            log.info("error : {}", e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException{
        String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail(), Role.USER);
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken(oAuth2User.getEmail(), Role.USER);
        String redisRtk = (String)redisTemplate.opsForValue().get(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail());

        if(StringUtils.hasText(redisRtk)){
            redisTemplate.delete(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail());
        }

        redisTemplate.opsForValue()
                .set(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail(),
                        refreshToken,
                        tokenProvider.getExpiration(refreshToken),
                        TimeUnit.MILLISECONDS);

        UserEntity userEntity = userRepository.findByUserEmail(oAuth2User.getEmail());

        setCookie(response, "id", userEntity.getId().toString());
        setCookie(response, "nickname", userEntity.getNickname());
        setCookie(response, "recentSearch", userEntity.getRecentSearch() == null ? "" : userEntity.getRecentSearch());
        setCookie(response, "socialLogin", "success");
        setTokenToCookie(response, "auth", accessToken, 36000, true, false);
        setTokenToCookie(response, AuthType.REFRESHTOKEN_KEY.getByItem(), refreshToken, 1209600, true, true);

        response.sendRedirect(userRedirectUrl);
    }

    private void setCookie(HttpServletResponse response, String name, String value) throws UnsupportedEncodingException {
        ResponseCookie cookie = ResponseCookie.from(name, URLEncoder.encode(value,"UTF-8"))
                .path("/")
                .build();

        response.addHeader(TokenProvider.COOKIE_HEADER, cookie.toString());
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

        response.addHeader(TokenProvider.COOKIE_HEADER, cookie.toString());
    }


}
