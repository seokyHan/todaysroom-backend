package com.todaysroom.user.jwt;

import com.todaysroom.global.exception.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.annotation.Bean;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.todaysroom.global.exception.code.AuthResponseCode.UNAUTHORIZED;

@Component
@RequiredArgsConstructor
@Slf4j
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final Message message;
    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                         AuthenticationException authException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType("application/json;charset=UTF-8");
        String returnJson = """
                {
                    "code": "%s",
                    "message": "%s"
                }
                """;
        String responseMessage = this.message.getResponseMessage(UNAUTHORIZED, "");
        var format = String.format(returnJson, UNAUTHORIZED.getCode(), responseMessage);
        try{
            response.getWriter().write(format);
            response.getWriter().flush();
            response.getWriter().close();
        }catch (IOException e){
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }

        response.getWriter().flush();
        response.getWriter().close();
    }
}
