package com.natixis.restaurant.service;

import org.springframework.data.domain.Page;

import com.natixis.restaurant.dto.request.CreateRestaurantRequest;
import com.natixis.restaurant.dto.response.GetRestaurantResponse;

public interface RestaurantService {
    public Long createRestaurant(CreateRestaurantRequest createRestaurantRequest);
    public Page<GetRestaurantResponse> getAllRestaurants(String name, String address, int page, String sortBy, String sortOrder);
    public GetRestaurantResponse getRestaurantById(Long id);
    public GetRestaurantResponse getRestaurantByFoodItemId(Long id);
    public void deleteRestaurant(Long id);
    
    
}
