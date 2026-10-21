package com.natixis.customer.cart.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;

@Getter 
public class AddCartItemRequest {
    @NotNull (message = "Item Must be selected to add to cart")
    private Long foodItemId;

    @NotNull (message = "Select Item Quantity")
    @Min (value = 1, message = "Quantity must be atleast of 1")
    private Integer quantity;
}
