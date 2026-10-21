package com.natixis.customer.common.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.common.dto.CreateCustomerRequest;
import com.natixis.customer.common.service.CustomerService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@RestController 
@RequestMapping ("/customer")
public class CustomerController {

    private final CustomerService customerService;
    
    @PostMapping 
    public ResponseEntity<ApiResponse<String>> createCustomer(@Valid @RequestBody CreateCustomerRequest request){
        customerService.createCustomer(request);

        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Customer Registered Successfully",
            null
        );
        return new ResponseEntity<>(
            response,
            HttpStatus.CREATED
        );
    }
    
}
