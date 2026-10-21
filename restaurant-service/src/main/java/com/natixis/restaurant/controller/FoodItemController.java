package com.natixis.restaurant.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.restaurant.dto.request.CreateFoodItemRequest;
import com.natixis.restaurant.dto.request.UpdateFoodItemQuantityRequest;
import com.natixis.restaurant.dto.response.ApiResponse;
import com.natixis.restaurant.dto.response.GetFoodItemsResponse;
import com.natixis.restaurant.service.FoodItemService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;


@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/fooditem")
public class FoodItemController {

    private final FoodItemService foodItemService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createFoodItem(@Valid @RequestBody CreateFoodItemRequest createFoodItemRequest) {
        
        Long id = foodItemService.createFoodItem(createFoodItemRequest);
        ApiResponse<Long> response = new ApiResponse<>(
            true,
            "Food Item Added To Restaurant Successfully",
            id
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/restaurant/{id}")
    public ResponseEntity<ApiResponse<List<GetFoodItemsResponse>>> getFoodItems(
            @PathVariable Long id, 
            @RequestParam(required = false) String name,
            @RequestParam(required = false) Double minPrice,
            @RequestParam(required = false) Double maxPrice,
            @RequestParam(required = false) Boolean isAvailable,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder){

        Page<GetFoodItemsResponse> foodItems = foodItemService.getFoodItems(id, name, minPrice, maxPrice, isAvailable, page, sortBy, sortOrder);

        ApiResponse<List<GetFoodItemsResponse>> response = new ApiResponse<>(
            true,
            "Food Items List Retrieved",
            foodItems.getContent()
        );
        response.setPage(foodItems.getNumber());
        response.setSize(foodItems.getSize());
        response.setTotalElements(foodItems.getTotalElements());
        response.setTotalPages(foodItems.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<GetFoodItemsResponse>> getFoodItemById(@PathVariable Long id){
        GetFoodItemsResponse foodItemResponse = foodItemService.getFoodItemById(id);
        ApiResponse<GetFoodItemsResponse> response = new ApiResponse<>(
            true,
            "Food Item Info Retrieved",
            foodItemResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/update-fooditems-quantity")
    public ResponseEntity<ApiResponse<String>> updateFoodItemsQuantity(
            @Valid @RequestBody List<@Valid UpdateFoodItemQuantityRequest> items) {
        foodItemService.updateFoodItemsQuantity(items);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Food item quantity updated successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{foodItemId}/restaurant/{restaurantId}")
    public ResponseEntity<ApiResponse<String>> deleteFoodItem(@PathVariable Long restaurantId, @PathVariable Long foodItemId) {
        foodItemService.deleteFoodItem(restaurantId, foodItemId);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Food Item deleted successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
