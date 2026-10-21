package com.natixis.customer.order.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UpdateFoodItemQuantityRequest {
    private Long foodItemId;
    private Integer quantity;
}
