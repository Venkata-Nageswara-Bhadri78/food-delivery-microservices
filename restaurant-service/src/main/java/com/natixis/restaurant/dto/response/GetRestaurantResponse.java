package com.natixis.restaurant.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
@NoArgsConstructor 
public class GetRestaurantResponse {
    private Long id;
    private String name;
    private String address;
    private String description;
}
