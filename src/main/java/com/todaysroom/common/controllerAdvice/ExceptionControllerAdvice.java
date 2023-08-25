package com.todaysroom.common.controllerAdvice;

import com.todaysroom.common.ErrorResponse;
import com.todaysroom.exception.NoUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.todaysroom")
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(NoUserException.class)
    public ResponseEntity<ErrorResponse> NoUserException(NoUserException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
