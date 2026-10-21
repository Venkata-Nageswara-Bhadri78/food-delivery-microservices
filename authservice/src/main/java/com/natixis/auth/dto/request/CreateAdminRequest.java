package com.natixis.auth.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
public class CreateAdminRequest {
    @NotNull (message = "user id is required")
    private Long userId;

    @NotBlank (message = "Email is required")
    private String email;
}