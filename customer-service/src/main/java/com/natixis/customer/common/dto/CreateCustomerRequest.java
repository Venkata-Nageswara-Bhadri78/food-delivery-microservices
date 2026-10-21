package com.natixis.customer.common.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CreateCustomerRequest {
    
    @Email (message = "Invalid email format")
    @NotBlank (message = "Email is required")
    private String email;

    @NotNull (message = "UserId is required")
    private Long userId;

    @NotBlank (message = "Phone number is required")
    private String phone;

    @NotBlank (message = "Address is required")
    private String address;
}
