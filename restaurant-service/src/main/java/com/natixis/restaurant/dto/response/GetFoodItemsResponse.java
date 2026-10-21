package com.natixis.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class GetFoodItemsResponse {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private Boolean isAvailable;
    private Integer quantity;
}
