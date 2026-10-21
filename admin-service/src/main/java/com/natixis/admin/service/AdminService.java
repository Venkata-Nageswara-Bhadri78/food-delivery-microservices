package com.natixis.admin.service;

import org.springframework.data.domain.Page;

import com.natixis.admin.dto.request.CreateAdminRequest;
import com.natixis.admin.dto.request.CreateFoodItemRequest;
import com.natixis.admin.dto.request.CreateRestaurantRequest;
import com.natixis.admin.dto.response.GetAgentResponse;
import com.natixis.admin.dto.response.GetOrderResponse;

public interface AdminService {
    public Long createAdmin(CreateAdminRequest request);

    public Long createRestaurant(CreateRestaurantRequest request);
    public Long createFoodItem(CreateFoodItemRequest request);
    
    public Page<GetOrderResponse> getAllOrders(Integer page);
    public Page<GetAgentResponse> getAllAgents(Integer page);

    public void deleteFoodItem(Long restaurantId, Long foodItemId);
    public void deleteRestaurant(Long restaurantId);
}
