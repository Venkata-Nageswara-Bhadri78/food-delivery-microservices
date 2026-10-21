package com.natixis.customer.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class OrderItemDetailsResponse {
    private Long foodItemId;
    private String foodItemName;
    private String description;
    private Integer quantity;
    private Double unitPrice;
    private Double totalPrice;
}
