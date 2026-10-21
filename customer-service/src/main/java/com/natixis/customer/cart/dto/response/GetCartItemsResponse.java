package com.natixis.customer.cart.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class GetCartItemsResponse {
    private Long id;
    private String name;
    private Double price;
    private Integer quantity;
    private String restaurantName;
}
