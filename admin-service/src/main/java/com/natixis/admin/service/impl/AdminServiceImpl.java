package com.natixis.admin.service.impl;

import com.natixis.admin.repository.adminRepository;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.natixis.admin.dto.request.CreateAdminRequest;
import com.natixis.admin.dto.request.CreateFoodItemRequest;
import com.natixis.admin.dto.request.CreateRestaurantRequest;
import com.natixis.admin.dto.response.ApiResponse;
import com.natixis.admin.dto.response.GetAgentResponse;
import com.natixis.admin.dto.response.GetOrderResponse;
import com.natixis.admin.entity.Admin;
import com.natixis.admin.exception.AdminAlreadyExistsException;
import com.natixis.admin.service.AdminService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AdminServiceImpl implements AdminService{
    
    private final adminRepository adminRepository;
    private final RestClient restClient;

    public Long createAdmin(CreateAdminRequest request){
        if(adminRepository.existsByUserId(request.getUserId())){
            throw new AdminAlreadyExistsException("Admin Already Exists");
        }
        // System.out.println("EMAIL: "+request.getEmail());
        Admin admin = Admin.builder()
                        .userId(request.getUserId())
                        .email(request.getEmail())
                        .build();
        Admin savedAdmin = adminRepository.save(admin);

        return savedAdmin.getId();
    }

    public Long createRestaurant(CreateRestaurantRequest request){

        ApiResponse<Long> restaurantResponse = restClient.post()
            .uri("http://RESTAURANT-SERVICE/restaurant")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(new ParameterizedTypeReference<ApiResponse<Long>>() {});
        return restaurantResponse.getData();
    }

    public Long createFoodItem(CreateFoodItemRequest request){
        // System.out.println("REDFVDGF");
        ApiResponse<Long> restaurantResponse = restClient.post()
            .uri("http://RESTAURANT-SERVICE/fooditem")
            .contentType(MediaType.APPLICATION_JSON)
            .body(request)
            .retrieve()
            .body(new ParameterizedTypeReference<ApiResponse<Long>>() {});
        return restaurantResponse.getData();
    }

    public Page<GetOrderResponse> getAllOrders(Integer page){
        ApiResponse<Page<GetOrderResponse>> allOrders = restClient.get()
                .uri("http://CUSTOMER-SERVICE/orders/all?pageNumber={pageNumber}", page)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Page<GetOrderResponse>>>() {});
        return allOrders.getData();
    }

    public Page<GetAgentResponse> getAllAgents(Integer page){
        ApiResponse<Page<GetAgentResponse>> agents = restClient.get()
                .uri("http://AGENT-SERVICE/agents/all?page={page}", page)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<Page<GetAgentResponse>>>() {});
        return agents.getData();
    }

    public void deleteRestaurant(Long id){
        restClient.delete()
            .uri("http://RESTAURANT-SERVICE/restaurant/{id}", id)
            .retrieve()
            .body(new ParameterizedTypeReference<ApiResponse<String>>() {});
    }

    public void deleteFoodItem(Long restaurantId, Long foodItemId){

        restClient.delete()
            .uri("http://RESTAURANT-SERVICE/fooditem/{foodItemId}/restaurant/{restaurantId}", foodItemId, restaurantId)
            .retrieve()
            .body(new ParameterizedTypeReference<ApiResponse<String>>() {});
    }
}
