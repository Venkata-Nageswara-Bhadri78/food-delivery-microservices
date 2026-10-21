package com.natixis.auth.dto.request;

import com.natixis.auth.enums.UserType;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class LoginRequest {

    @Email (message = "Invalid email format")
    @NotBlank (message = "Email should not be null")
    private String email;

    @NotBlank (message = "Password is required")
    private String password;

    @NotNull (message = "usertype is mandatory")
    private UserType userType;
}
