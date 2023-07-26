package com.todaysroom.oauth2.handler;

import com.todaysroom.oauth2.CustomOAuth2User;
import com.todaysroom.types.AuthType;
import com.todaysroom.types.Role;
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
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.concurrent.TimeUnit;

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
                log.info("oAuth2User : {}",oAuth2User.getEmail());
                String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail());
                Cookie cookie = new Cookie(TokenProvider.AUTHORIZATION_HEADER, accessToken);
                cookie.setMaxAge(36000);
                cookie.setPath("/");
                response.addHeader(TokenProvider.AUTHORIZATION_HEADER, accessToken);

                response.addCookie(cookie);
                response.sendRedirect("http://localhost:8080/signup?oauth=success"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트

                /**
                 * 주석 처리한 부분 - Role을 GUEST -> USER로 업데이트하는 로직.
                 * 이후에 회원가입 추가 폼 입력 시 업데이트하는 컨트롤러, 서비스를 만들면
                 * 그 시점에 Role Update를 진행
                 */
//                User findUser = userRepository.findByEmail(oAuth2User.getEmail())
//                                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));
//                findUser.authorizeUser();
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
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken();
        String redisRtk = (String)redisTemplate.opsForValue().get(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail());

        if(StringUtils.hasText(redisRtk)){
            redisTemplate.delete(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail());
        }

        redisTemplate.opsForValue()
                .set(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail(),
                        refreshToken,
                        tokenProvider.getExpiration(refreshToken),
                        TimeUnit.MILLISECONDS);

        ResponseCookie cookie = ResponseCookie.from(AuthType.REFRESHTOKEN_KEY.getByItem(),refreshToken)
                .maxAge(1209600)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        response.setHeader(TokenProvider.AUTHORIZATION_HEADER,  accessToken);
        response.setHeader(TokenProvider.REFRESHTOKEN_HEADER, cookie.toString());
        response.sendRedirect("http://localhost:8080?socialLogin=success");
    }

}
