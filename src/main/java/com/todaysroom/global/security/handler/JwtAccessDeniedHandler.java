package com.todaysroom.global.security.handler;

import com.todaysroom.global.exception.Message;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

import static com.todaysroom.global.exception.code.AuthResponseCode.FORBIDDEN;

@Component
@RequiredArgsConstructor
public class JwtAccessDeniedHandler implements AccessDeniedHandler {
    private final Message message;
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {

        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        response.setContentType("application/json;charset=UTF-8");

        String returnJson = """
                {
                    "code": "%s",
                    "message": "%s"
                }
                """;
        String responseMessage = this.message.getResponseMessage(FORBIDDEN, "");
        String format = String.format(returnJson, FORBIDDEN.getCode(), responseMessage);

        try{
            response.getWriter().write(format);
            response.getWriter().flush();
            response.getWriter().close();
        }catch (IOException e){
            e.printStackTrace();
        }

        response.getWriter().flush();
        response.getWriter().close();
    }
}
