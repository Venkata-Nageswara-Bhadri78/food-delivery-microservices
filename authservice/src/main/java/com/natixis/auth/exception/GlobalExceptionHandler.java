package com.natixis.auth.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.natixis.auth.dto.response.ApiResponse;

@RestControllerAdvice 
public class GlobalExceptionHandler {
    
    @ExceptionHandler (EmailAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleEmailAlreadyExists(EmailAlreadyExistsException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (UserNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleUserNotFoundException(UserNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );

        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler (InvalidCredentialsException.class)
    public ResponseEntity<ApiResponse<String>> handleInvalidCredentials(InvalidCredentialsException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
    }

}
