package com.todaysroom.oauth2.handler;

import com.todaysroom.oauth2.CustomOAuth2User;
import com.todaysroom.types.AuthType;
import com.todaysroom.types.Role;
import com.todaysroom.user.entity.UserAuthority;
import com.todaysroom.user.entity.UserEntity;
import com.todaysroom.user.jwt.TokenProvider;
import com.todaysroom.user.repository.AuthorityRepository;
import com.todaysroom.user.repository.UserRepository;
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
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2LoginSuccessHandler implements AuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;
    private final RedisTemplate redisTemplate;
    private final UserRepository userRepository;
    private final AuthorityRepository authorityRepository;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        log.info("OAuth2 Login 성공!");
        try{
            CustomOAuth2User oAuth2User = (CustomOAuth2User) authentication.getPrincipal();

            Optional<UserEntity> optionalUserEntity = userRepository.findOneWithAuthoritiesByUserEmail(oAuth2User.getEmail());
            List<UserAuthority> authorities = optionalUserEntity.get().getAuthorities();
            String temp = "";

            for (UserAuthority authority : authorities) {
                if(String.valueOf(authorityRepository.findById(authority.getId())).contains("ROLE_GUEST")){
                    temp = String.valueOf(authorityRepository.findById(authority.getId()));
                }
            }


            // User의 Role이 GUEST일 경우 처음 요청한 회원이므로 회원가입 페이지로 리다이렉트
//            if(oAuth2User.getRole() == Role.GUEST) {
//                String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail());
//                response.addHeader(jwtService.getAccessHeader(), "Bearer " + accessToken);
//                response.sendRedirect("oauth2/sign-up"); // 프론트의 회원가입 추가 정보 입력 폼으로 리다이렉트


        } catch (Exception e){
            log.info("error : {}", e.getMessage());
        }
    }

    private void loginSuccess(HttpServletResponse response, CustomOAuth2User oAuth2User) throws IOException{
        String accessToken = tokenProvider.oAuth2CreateAccessToken(oAuth2User.getEmail());
        String refreshToken = tokenProvider.oAuth2CreateRefreshToken();
        String redisRtk = (String)redisTemplate.opsForValue().get(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail());
        ResponseCookie cookie = ResponseCookie.from(AuthType.REFRESHTOKEN_KEY.getByItem(),refreshToken)
                .maxAge(1209600)
                .path("/")
                .secure(true)
                .sameSite("None")
                .httpOnly(true)
                .build();

        response.setHeader(TokenProvider.AUTHORIZATION_HEADER,  accessToken);
        response.setHeader(TokenProvider.REFRESHTOKEN_HEADER, cookie.toString());
        response.setStatus(HttpServletResponse.SC_OK);

        if(StringUtils.hasText(redisRtk)){
            redisTemplate.delete(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail());
        }

        redisTemplate.opsForValue()
                .set(AuthType.REFRESHTOKEN_KEY.getByItem() + oAuth2User.getEmail(),
                        refreshToken,
                        tokenProvider.getExpiration(refreshToken),
                        TimeUnit.MILLISECONDS);

    }



}
