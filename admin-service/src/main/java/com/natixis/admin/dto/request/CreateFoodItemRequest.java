package com.natixis.admin.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Setter 
@Getter 
public class CreateFoodItemRequest {
    @NotBlank (message = "FoodItem Name is mandatory")
    private String name;

    @NotBlank (message = "FoodItem Description is mandatory")
    private String description;

    @NotNull (message = "Food Item Price is Mandatory")
    @DecimalMin(value = "0.0", message = "Food Item Price cannot be negative")
    private Double price;

    @NotNull (message = "FoodItem Quantity is Mandatory")
    @Min(value = 0, message = "FoodItem Quantity cannot be negative")
    private Integer quantity;

    @NotNull (message = "Restaurant Id is mandatory")
    private Long restaurantId;
}
