package com.todaysroom.global.security.filter;

import com.todaysroom.global.exception.CustomException;
import com.todaysroom.global.exception.Message;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExceptionHandlerFilter extends OncePerRequestFilter {

    private final Message message;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        } catch (CustomException ex){
            log.error("exception exception handler filter {}", ExceptionUtils.getStackTrace(ex));
            setErrorResponse(ex.getResponseCode().getHttpStatus(), response, ex);
        }
    }

    public void setErrorResponse(HttpStatus status, HttpServletResponse response, CustomException ex){
        response.setStatus(status.value());
        response.setContentType("application/json;charset=UTF-8");

        String message = extractMessage(ex);
        var retMessage = this.message.getResponseMessage(ex.getResponseCode(), "", message);
        String returnJson = """
                {
                    "code": "%s",
                    "message": "%s"
                }
                """;
        var format = String.format(returnJson,ex.getResponseCode().getCode(), retMessage);
        try{
            response.getWriter().write(format);
            response.getWriter().flush();
            response.getWriter().close();
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private String extractMessage(CustomException ex) {
        try {
            return String.join(",", ex.getValues());
        } catch (Exception e) {
            return "";
        }
    }

}