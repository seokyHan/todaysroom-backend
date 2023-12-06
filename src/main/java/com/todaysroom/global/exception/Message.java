package com.todaysroom.global.exception;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.springframework.context.MessageSource;
import org.springframework.context.NoSuchMessageException;
import org.springframework.stereotype.Component;

import java.util.Locale;

@Component
@Slf4j
@RequiredArgsConstructor
public class Message {

    /**
     * 메세지 국제화(다국어 처리)
     * 메세지 설정 파일을 모아놓고 국가마다 로컬라이징
     */
    private final MessageSource messageSource;

    public String getResponseMessage(final ResponseCode responseCode, final String defaultMessage, final String... values) {
        String message = defaultMessage;

        try {
            log.info("responseCode.getCode() = {} ", responseCode.getCode());
            message = messageSource.getMessage(responseCode.getCode(), values, Locale.getDefault());
            log.info("message = {} ", message);
        } catch (NoSuchMessageException e) {
            log.error("{}", ExceptionUtils.getStackTrace(e));
        }
        return message;
    }
}
