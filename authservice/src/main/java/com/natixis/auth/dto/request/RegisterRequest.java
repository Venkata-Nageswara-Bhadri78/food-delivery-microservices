package com.natixis.auth.dto.request;
import com.natixis.auth.enums.UserType;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class RegisterRequest {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Email is required")
    private String email;

    @NotBlank (message = "Password is required")
    @Size (min = 8, message = "Password must be at least of 8 chars")
    private String password;

    @NotBlank (message = "Phone number is required")
    private String phone;

    @NotBlank (message = "Address is required")
    private String address;

    @NotNull (message = "UserType is Mandatory") 
    private UserType userType;
}
