package com.natixis.customer.cart.dto.request;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class RemoveCartItemRequest {

    @NotNull(message = "Select FoodItem to remove")
    private Long foodItemId;

}
