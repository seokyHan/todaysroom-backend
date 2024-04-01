package com.todaysroom.global.security.filter;

import com.todaysroom.global.security.props.ExcludeProperties;
import com.todaysroom.global.security.jwt.TokenProvider;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Arrays;
import java.util.stream.Stream;

import static com.todaysroom.global.types.AuthType.TOKEN_HEADER;
import static java.lang.String.format;
import static org.apache.commons.lang3.StringUtils.equalsIgnoreCase;
import static org.springframework.http.HttpHeaders.AUTHORIZATION;

@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

    // GenericFilterBean 필터가 중복해서 탈 수 있음
    // OncePerRequestFilter 요청당 필터가 한번만 탈 수 있도록 험
    private final TokenProvider tokenProvider;
    private final ExcludeProperties excludeProperties;

    @Override
    public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String contextPath = request.getContextPath();
        String requestURI = request.getRequestURI();

        if(!isWhitePath(contextPath, requestURI)) {
            String token = resolveToken(request);
            if(!tokenProvider.validateToken(token)) return;
            userAuthFilter(token);
        }

        filterChain.doFilter(request, response);
    }

    private void userAuthFilter(String token){
        Authentication authentication = tokenProvider.getAuthentication(token);
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }

    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader(AUTHORIZATION);
        if(StringUtils.hasText(bearerToken) && bearerToken.startsWith(TOKEN_HEADER.getItem())){
            return bearerToken.substring(TOKEN_HEADER.getItem().length());
        }

        return null;
    }

    private boolean isWhitePath(String contextPath, String requestURI){
        Stream<String> stream = Arrays.stream(excludeProperties.path());

        return stream.anyMatch(req -> equalsIgnoreCase(requestURI, format("%s%s", contextPath, req)));
    }

}
