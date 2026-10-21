package com.natixis.restaurant.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.restaurant.dto.request.CreateRestaurantRequest;
import com.natixis.restaurant.dto.response.ApiResponse;
import com.natixis.restaurant.dto.response.GetRestaurantResponse;
import com.natixis.restaurant.service.RestaurantService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.net.URI;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;

@RequiredArgsConstructor 
@RestController 
@RequestMapping("/restaurant")
public class RestaurantController {

    private final RestaurantService restaurantService;

    @PostMapping
    public ResponseEntity<ApiResponse<Long>> createRestaurant(@Valid @RequestBody CreateRestaurantRequest createRestaurantRequest) {
        System.out.println("RESTAURANT REGISTERED ENTERED");
        Long id = restaurantService.createRestaurant(createRestaurantRequest);
        ApiResponse<Long> response = new ApiResponse<>(
            true,
            "Restaurant Registered Successfully",
            id
        );
        return ResponseEntity.created(URI.create("/restaurant/" + id)).body(response);
    }
    
    @GetMapping 
    public ResponseEntity<ApiResponse<List<GetRestaurantResponse>>> getAllRestaurants(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String address,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "id") String sortBy,
            @RequestParam(defaultValue = "asc") String sortOrder){

        System.out.println("INFO GOT SUCCESSFULLY");

        Page<GetRestaurantResponse> restaurants = restaurantService.getAllRestaurants(name, address, page, sortBy, sortOrder);

        ApiResponse<List<GetRestaurantResponse>> response = new ApiResponse<>(
            true,
            "Restaurant Info Retrieved Successfully",
            restaurants.getContent()
        );
        response.setPage(restaurants.getNumber());
        response.setSize(restaurants.getSize());
        response.setTotalElements(restaurants.getTotalElements());
        response.setTotalPages(restaurants.getTotalPages());

        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping ("/{id}")
    public ResponseEntity<ApiResponse<GetRestaurantResponse>> getRestaurantById(@PathVariable Long id){
        GetRestaurantResponse restaurantInfo = restaurantService.getRestaurantById(id);
        ApiResponse<GetRestaurantResponse> response = new ApiResponse<>(
            true,
            "Restaurant Info Found",
            restaurantInfo
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/food/{id}")
    public ResponseEntity<ApiResponse<GetRestaurantResponse>> getRestaurantByFoodItemId(@PathVariable Long id){
        GetRestaurantResponse restaurantInfo = restaurantService.getRestaurantByFoodItemId(id);
        ApiResponse<GetRestaurantResponse> response = new ApiResponse<>(
            true,
            "Restaurant Info Found",
            restaurantInfo
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<ApiResponse<String>> deleteRestaurant(@PathVariable Long id) {
        restaurantService.deleteRestaurant(id);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Restaurant and all its food items deleted successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
