package com.shurona.chat.mytalk.common.aop;


import com.shurona.chat.mytalk.common.dto.ErrorResponseDto;
import com.shurona.chat.mytalk.user.common.exception.UserException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalRestControllerExceptionHandler {

    // RuntimeException, IllegalArgumentException 등
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponseDto> handleRuntimeException(RuntimeException e) {
        log.error("RuntimeException occurred: {}", e.getMessage(), e);
        ErrorResponseDto errorResponse = new ErrorResponseDto("Runtime Exception occurred "
            + e.getMessage());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
    }

    // UserException 처리
    @ExceptionHandler(UserException.class)
    public ResponseEntity<ErrorResponseDto> handleUserException(UserException e) {
        ErrorResponseDto errorResponse = new ErrorResponseDto(e.getMessage());
        return ResponseEntity.status(e.getCode().getStatus()).body(errorResponse);
    }
}
