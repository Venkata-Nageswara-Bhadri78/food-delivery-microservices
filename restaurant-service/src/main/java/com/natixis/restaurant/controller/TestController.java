package com.natixis.restaurant.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.restaurant.dto.response.ApiResponse;

@RequestMapping("/res")
@RestController 
public class TestController {
    @GetMapping 
    public ResponseEntity<ApiResponse<String>> getFunc(){
        return ResponseEntity.ok(new ApiResponse<>(true, "SUCCESSFUL", "SUCCESSFUL"));
    }
}
