package com.natixis.auth.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.auth.dto.request.LoginRequest;
import com.natixis.auth.dto.request.RegisterRequest;
import com.natixis.auth.dto.response.ApiResponse;
import com.natixis.auth.dto.response.LoginResponse;
import com.natixis.auth.service.AuthService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;


@RestController 
@RequiredArgsConstructor  

public class AuthController {

    private final AuthService authService;
    @PostMapping("/register")

    public ResponseEntity<ApiResponse<String>> register(@Valid @RequestBody RegisterRequest registerRequest){
        authService.register(registerRequest);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "User Registered Successfully",
            null
        );
        return new ResponseEntity<>(
            response,
            HttpStatus.CREATED
        );
    }
    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<LoginResponse>> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse loginResponse = authService.login(loginRequest);
        ApiResponse<LoginResponse> response = new ApiResponse<>(
            true, 
            "User Login Successful", 
            loginResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse<String>> logout() {
        authService.logout();
        ApiResponse<String> response = new ApiResponse<>(
            true, 
            "User Logged Out Successfully", 
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}
