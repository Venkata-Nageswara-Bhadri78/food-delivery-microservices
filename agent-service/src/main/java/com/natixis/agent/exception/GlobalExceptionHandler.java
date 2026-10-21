package com.natixis.agent.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.natixis.agent.dto.response.ApiResponse;

@RestControllerAdvice 
public class GlobalExceptionHandler {

    @ExceptionHandler (AgentAlreadyExistsException.class)
    public ResponseEntity<ApiResponse<String>> handleAgentAlreadyExists(AgentAlreadyExistsException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

    @ExceptionHandler (AgentNotFoundException.class)
    public ResponseEntity<ApiResponse<String>> handleAgentAlreadyExists(AgentNotFoundException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }
    
    @ExceptionHandler (OrderNotAssignedException.class)
    public ResponseEntity<ApiResponse<String>> handleAgentAlreadyExists(OrderNotAssignedException exception){
        ApiResponse<String> response = new ApiResponse<>(
            false,
            exception.getMessage(),
            null
        );
        
        return new ResponseEntity<>(response, HttpStatus.CONFLICT);
    }

}
