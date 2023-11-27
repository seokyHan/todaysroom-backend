package com.todaysroom.global.common.controllerAdvice;

import com.todaysroom.global.common.ErrorResponse;
import com.todaysroom.file.exception.FailedMakeDirectoryException;
import com.todaysroom.file.exception.FailedStoreFileException;
import com.todaysroom.inquiry.exception.NoInquiryIdException;
import com.todaysroom.oauth2.exception.AuthorityNotFoundException;
import com.todaysroom.user.exception.DuplicatedEmailException;
import com.todaysroom.user.exception.ExpiredRefreshTokenException;
import com.todaysroom.user.exception.NoRefreshTokenException;
import com.todaysroom.user.exception.NoUserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice(basePackages = "com.todaysroom")
@Slf4j
public class ExceptionControllerAdvice {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> methodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getBindingResult().getFieldError().getDefaultMessage()));
    }

    @ExceptionHandler(NoUserException.class)
    public ResponseEntity<ErrorResponse> NoUserException(NoUserException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
    @ExceptionHandler(DuplicatedEmailException.class)
    public ResponseEntity<ErrorResponse> DuplicatedEmailException(DuplicatedEmailException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }
    @ExceptionHandler(ExpiredRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> ExpiredRefreshTokenException(ExpiredRefreshTokenException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }
    @ExceptionHandler(NoRefreshTokenException.class)
    public ResponseEntity<ErrorResponse> NoRefreshTokenException(NoRefreshTokenException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
    @ExceptionHandler(AuthorityNotFoundException.class)
    public ResponseEntity<ErrorResponse> AuthorityNotFoundException(AuthorityNotFoundException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }
    @ExceptionHandler(NoInquiryIdException.class)
    public ResponseEntity<ErrorResponse> NoInquiryIdException(NoInquiryIdException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), e.getMessage()));
    }

    @ExceptionHandler(FailedMakeDirectoryException.class)
    public ResponseEntity<ErrorResponse> FailedMakeDirectoryException(FailedMakeDirectoryException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }

    @ExceptionHandler(FailedStoreFileException.class)
    public ResponseEntity<ErrorResponse> FailedStoreFileException(FailedStoreFileException e) {
        log.error(e.getMessage(), e);
        return ResponseEntity.badRequest().body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), e.getMessage()));
    }
}
