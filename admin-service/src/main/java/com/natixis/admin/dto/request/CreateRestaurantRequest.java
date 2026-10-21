package com.natixis.admin.dto.request;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CreateRestaurantRequest {
    @NotBlank (message = "Restaurant Name is required")
    private String name;

    @NotBlank (message = "Restaurant Address is mandatory")
    private String address;

    @NotBlank (message = "Restaurant Description is mandatory")
    private String description;
}
