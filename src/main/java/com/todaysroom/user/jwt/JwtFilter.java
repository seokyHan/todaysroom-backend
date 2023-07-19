package com.todaysroom.user.jwt;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.todaysroom.user.dto.ResponseDto;
import com.todaysroom.types.ErrorCode;
import io.jsonwebtoken.ExpiredJwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // GenericFilterBean 필터가 중복해서 탈 수 있음
    // OncePerRequestFilter 요청당 필터가 한번만 탈 수 있도록 험
    private final TokenProvider tokenProvider;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String jwt = tokenProvider.resolveToken(request);
        String requestURI = request.getRequestURI();

        try{
            // 토큰 유효성 체크
            if(StringUtils.hasText(jwt) && tokenProvider.validateToken(jwt)){
                if(!requestURI.equals("/users/reissue")){
                    Authentication authentication = tokenProvider.getAuthentication(jwt);
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    log.debug("Security Context에 '{}' 인증 정보를 저장했습니다, uri: {}", authentication.getName(), requestURI);
                }
            }
            filterChain.doFilter(request, response);
        }catch (ExpiredJwtException e){
            ResponseDto responseDto = ResponseDto.builder().
                    status(ErrorCode.JWT_ACCESS_TOKEN_EXPIRED.getStatus()).
                    message(ErrorCode.JWT_ACCESS_TOKEN_EXPIRED.getMessage()).
                    code(ErrorCode.JWT_ACCESS_TOKEN_EXPIRED.getCode())
                    .build();

            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write(new ObjectMapper().writeValueAsString(responseDto));
            response.getWriter().flush();
        }

    }

}
