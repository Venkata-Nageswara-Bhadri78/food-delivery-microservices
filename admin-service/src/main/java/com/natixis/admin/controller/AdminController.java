package com.natixis.admin.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.admin.dto.request.CreateAdminRequest;
import com.natixis.admin.dto.request.CreateFoodItemRequest;
import com.natixis.admin.dto.request.CreateRestaurantRequest;
import com.natixis.admin.dto.response.ApiResponse;
import com.natixis.admin.dto.response.GetAgentResponse;
import com.natixis.admin.dto.response.GetOrderResponse;
import com.natixis.admin.service.AdminService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@RestController 
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @PostMapping 
    public ResponseEntity<ApiResponse<Long>> createAdmin(@RequestBody CreateAdminRequest createAdminRequest){
        // System.out.println("ADMIN SERVICE REACHED..........");
        // System.out.println("EMAAIL:    -----"+createAdminRequest.getEmail());
        Long adminId = adminService.createAdmin(createAdminRequest);
        ApiResponse<Long> response = new ApiResponse<>(
            true,
            "Admin Registered Successfully",
            adminId
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping ("/restaurant")
    public ResponseEntity<ApiResponse<Long>> createRestaurant(@Valid @RequestBody CreateRestaurantRequest createRestaurantRequest){
        // System.out.println("REACHED REACHED BROOWWWWW");

        Long restaurantId = adminService.createRestaurant(createRestaurantRequest);
        ApiResponse<Long> response = new ApiResponse<>(
            true,
            "Restaurant Added Successfully Successfully",
            restaurantId
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping ("/fooditem")
    public ResponseEntity<ApiResponse<Long>> createFoodItem(@Valid @RequestBody CreateFoodItemRequest createFoodItemRequest){

        Long foodItemResponse = adminService.createFoodItem(createFoodItemRequest);
        ApiResponse<Long> response = new ApiResponse<>(
            true,
            "FoodItem Added Successfully",
            foodItemResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    

    @GetMapping ("/orders/all")
    public ResponseEntity<ApiResponse<Page<GetOrderResponse>>> getAllOrders(@RequestParam(defaultValue = "0") String page){
        Page<GetOrderResponse> ordersPage = adminService.getAllOrders(Integer.parseInt(page));
        ApiResponse<Page<GetOrderResponse>> response = new ApiResponse<>(
            true,
            "Order Retrieved",
            ordersPage
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    @GetMapping ("/agents/all")
    public ResponseEntity<ApiResponse<Page<GetAgentResponse>>> getAllAgents(@RequestParam(defaultValue = "0") String page){
        Page<GetAgentResponse> ordersPage = adminService.getAllAgents(Integer.parseInt(page));
        ApiResponse<Page<GetAgentResponse>> response = new ApiResponse<>(
            true,
            "Agents Retrieved",
            ordersPage
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{foodItemId}/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<String>> deleteFoodItem(@PathVariable Long restaurantId, @PathVariable Long foodItemId) {
        adminService.deleteFoodItem(restaurantId, foodItemId);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Food Item deleted successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping ("/{restaurantId}")
    public ResponseEntity<ApiResponse<String>> deleteRestaurant(@PathVariable Long restaurantId) {
        adminService.deleteRestaurant(restaurantId);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Restaurant deleted successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
