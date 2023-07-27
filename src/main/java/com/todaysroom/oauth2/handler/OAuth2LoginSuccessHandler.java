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
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;
    private final RedisTemplate redisTemplate;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            if (oAuth2User.getRole() == Role.GUEST) {
                String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail());

                ResponseCookie accessTokenCookie = ResponseCookie.from(TokenProvider.AUTHORIZATION_HEADER,accessToken)
                        .maxAge(36000)
                        .path("/")
                        .build();

                ResponseCookie isSocalLogin = ResponseCookie.from("oauth2","success")
                        .path("/")
                        .build();

                response.addHeader(TokenProvider.COOKIE_HEADER,  accessTokenCookie.toString());
                response.addHeader(TokenProvider.COOKIE_HEADER,  isSocalLogin.toString());

                response.sendRedirect("http://localhost:8080/signup"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트
            }
            else {
                loginSuccess(response, oAuth2User); // 로그인에 성공한 경우 access, refresh 토큰 생성
            }

        } catch (Exception e){
            log.info("error : {}", e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException{
        String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail());
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken(oAuth2User.getEmail());
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

        ResponseCookie accessTokenCookie = ResponseCookie.from("auth",accessToken)
                .maxAge(36000)
                .path("/")
                .build();

        ResponseCookie userIdCookie = ResponseCookie.from("id", userEntity.getId().toString())
                .path("/")
                .build();

        ResponseCookie userNickNameCookie = ResponseCookie.from("nickname","userEntity.getNickname().toString()")
                .path("/")
                .build();

        ResponseCookie userRecentSearchTokenCookie = ResponseCookie.from("recentSearch","userEntity.getRecentSearch().toString()")
                .path("/")
                .build();

        ResponseCookie isSocalLogin = ResponseCookie.from("socialLogin","success")
                .path("/")
                .build();

        ResponseCookie cookie = ResponseCookie.from(AuthType.REFRESHTOKEN_KEY.getByItem(),refreshToken)
                .maxAge(1209600)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        response.addHeader(TokenProvider.COOKIE_HEADER,  accessTokenCookie.toString());
        response.addHeader(TokenProvider.COOKIE_HEADER, userIdCookie.toString());
        response.addHeader(TokenProvider.COOKIE_HEADER, userNickNameCookie.toString());
        response.addHeader(TokenProvider.COOKIE_HEADER, userRecentSearchTokenCookie.toString());
        response.addHeader(TokenProvider.COOKIE_HEADER, cookie.toString());
        response.sendRedirect("http://localhost:8080/");
    }

}
