package com.natixis.customer.restclient;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GetFoodItemsResponse {
    private Long id;
    private String name;
    private Double price;
    private String description;
    private Boolean isAvailable;
    private Integer quantity;
}
