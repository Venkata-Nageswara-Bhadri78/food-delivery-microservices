package com.natixis.customer.cart.service;

import java.util.List;

import com.natixis.customer.cart.dto.request.AddCartItemRequest;
import com.natixis.customer.cart.dto.request.UpdateCartItemQuantityRequest;
import com.natixis.customer.restclient.GetFoodItemsResponse;

public interface CartService {
    public void addCartItem(AddCartItemRequest request);
    public void updateCartItemQuantity(UpdateCartItemQuantityRequest UpdateCartItemQuantityRequest);
    public List<GetFoodItemsResponse> getCartItems();
    public void deleteCartItem(Long foodItemId);
}
