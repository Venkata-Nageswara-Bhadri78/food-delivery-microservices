package com.natixis.customer.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class UpdateCartItemQuantityRequest {

    @NotNull  (message = "Select FoodItem to update Quantity")
    private Long foodItemId;

    @NotNull (message =  "Quantity is required")
    private Integer quantity;
}
