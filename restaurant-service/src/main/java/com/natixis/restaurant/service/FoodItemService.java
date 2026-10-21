package com.natixis.restaurant.service;

import org.springframework.data.domain.Page;

import java.util.List;

import com.natixis.restaurant.dto.request.CreateFoodItemRequest;
import com.natixis.restaurant.dto.request.UpdateFoodItemQuantityRequest;
import com.natixis.restaurant.dto.response.GetFoodItemsResponse;

public interface FoodItemService {
    public Long createFoodItem(CreateFoodItemRequest createFoodItemRequest);
    public Page<GetFoodItemsResponse> getFoodItems(Long restaurantId, String name, Double minPrice, Double maxPrice, Boolean isAvailable, int page, String sortBy, String sortOrder);
    public GetFoodItemsResponse getFoodItemById(Long id);
    public void deleteFoodItem(Long restaurantId, Long foodItemId);
    public void updateFoodItemsQuantity(List<UpdateFoodItemQuantityRequest> items);
}
