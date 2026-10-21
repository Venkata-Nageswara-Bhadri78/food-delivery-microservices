package com.natixis.auth.dto.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
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
