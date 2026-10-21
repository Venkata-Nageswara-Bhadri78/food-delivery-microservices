package com.natixis.customer.cart.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.customer.cart.dto.request.AddCartItemRequest;
import com.natixis.customer.cart.dto.request.RemoveCartItemRequest;
import com.natixis.customer.cart.dto.request.UpdateCartItemQuantityRequest;
import com.natixis.customer.cart.service.CartService;
import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.restclient.GetFoodItemsResponse;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController 
@RequiredArgsConstructor 
@RequestMapping("/cart")

public class CartController {

    private final CartService cartService;
    
    @PostMapping
    public ResponseEntity<ApiResponse<String>> addCartItem(@Valid @RequestBody AddCartItemRequest addCartItemRequest) {
        cartService.addCartItem(addCartItemRequest);

        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Item Successfully added to Cart",
            null
        );

        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }
    @GetMapping
    public ResponseEntity<ApiResponse<List<GetFoodItemsResponse>>> getCartItems() {
        List<GetFoodItemsResponse> cartItems = cartService.getCartItems();

        ApiResponse<List<GetFoodItemsResponse>> response = new ApiResponse<>(
            true,
            "Cart Items Found",
            cartItems
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/quantity")
    public ResponseEntity<ApiResponse<String>> updateCartItemQuantity(@Valid @RequestBody UpdateCartItemQuantityRequest request){
        cartService.updateCartItemQuantity(request);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Cart Item Quantity Updated Successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @DeleteMapping("{foodItemId}/remove")
    public ResponseEntity<ApiResponse<String>> deleteCartItem(@Valid @PathVariable Long foodItemId){
        cartService.deleteCartItem(foodItemId);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Cart Item Deleted Successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/remove")
    public ResponseEntity<ApiResponse<String>> removeCartItem(@Valid @RequestBody RemoveCartItemRequest request) {
        cartService.deleteCartItem(request.getFoodItemId());
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Cart Item Removed Successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
}
