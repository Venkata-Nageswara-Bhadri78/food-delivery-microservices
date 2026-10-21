package com.natixis.customer.cart.service.impl;

import java.util.List;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestClient;

import com.natixis.customer.cart.dto.request.AddCartItemRequest;
import com.natixis.customer.cart.dto.request.UpdateCartItemQuantityRequest;
import com.natixis.customer.cart.entity.Cart;
import com.natixis.customer.cart.entity.CartItem;
import com.natixis.customer.cart.exception.CartItemAlreadyExistsException;
import com.natixis.customer.cart.exception.CartItemNotFoundException;
import com.natixis.customer.cart.exception.InvalidCartItemException;
import com.natixis.customer.cart.repository.CartItemRepository;
import com.natixis.customer.cart.repository.CartRepository;
import com.natixis.customer.common.repository.CustomerRepository;
import com.natixis.customer.cart.service.CartService;
import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.exception.CustomerNotFoundException;
import com.natixis.customer.common.security.JwtService;
import com.natixis.customer.restclient.GetFoodItemsResponse;
import com.natixis.customer.restclient.GetRestaurantResponse;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor 
@Service 
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;

    private final RestClient restClient;

    public void addCartItem(AddCartItemRequest request){
        String email = jwtService.extractEmail();

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));

        ApiResponse<GetFoodItemsResponse> foodItemResponse = restClient.get()
                                                                    .uri("http://RESTAURANT-SERVICE/fooditem/{id}", request.getFoodItemId())
                                                                    .retrieve()
                                                                    .body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {});

        GetFoodItemsResponse foodItem = foodItemResponse.getData();

        ApiResponse<GetRestaurantResponse> restaurantResponse = restClient.get()
                .uri("http://RESTAURANT-SERVICE/restaurant/food/{id}", request.getFoodItemId())
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<GetRestaurantResponse>>() {});

        GetRestaurantResponse restarantInfo = restaurantResponse.getData();

        Cart savedCart = null;
        if(!cartRepository.existsByCustomer(customer)){
            Cart cart = Cart.builder()
                .customer(customer)
                .cartItems(null)
                .restaurantId(restarantInfo.getId())
                .build();
            savedCart = cartRepository.save(cart);
        }

        if(savedCart == null){
            savedCart = cartRepository.findByCustomer(customer).orElseThrow(() -> new CartItemNotFoundException("Cart Not Found"));
        }

        if (savedCart.getRestaurantId() == null) {
            savedCart.setRestaurantId(restarantInfo.getId());
            savedCart = cartRepository.save(savedCart);
        } else if (!savedCart.getRestaurantId().equals(restarantInfo.getId())) {
            throw new InvalidCartItemException(
                "Can't add item from multiple restaurants at a time"
            );
        }
        
        Boolean isCartItemPresent = cartItemRepository.existsByCartAndFoodItemId(savedCart, foodItem.getId());
        if(isCartItemPresent){
            throw new CartItemAlreadyExistsException("Cart Item Already Exists");
        }

        CartItem cartItem = CartItem.builder()
                            .cart(savedCart)
                            .foodItemId(foodItem.getId())
                            .quantity(request.getQuantity())
                            .build();
        cartItemRepository.save(cartItem);

    }

    public List<GetFoodItemsResponse> getCartItems(){
        String email = jwtService.extractEmail();

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        Cart cart = cartRepository.findByCustomer(customer).orElseThrow(() -> new CartItemNotFoundException("No Cart Found"));

        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);

        List<GetFoodItemsResponse> cartItemsResponses = cartItems.stream().map((foodItem) -> {
            ApiResponse<GetFoodItemsResponse> foodItemResponse = restClient.get().uri("http://RESTAURANT-SERVICE/fooditem/{id}", foodItem.getFoodItemId())
                    .retrieve().body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {});
            
            GetFoodItemsResponse item = foodItemResponse.getData();
            
            return new GetFoodItemsResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDescription(),
                item.getIsAvailable(),
                foodItem.getQuantity()
            );
        }).toList();
        return cartItemsResponses;
    }

    public void updateCartItemQuantity(UpdateCartItemQuantityRequest request){
        String email = jwtService.extractEmail();

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        ApiResponse<GetFoodItemsResponse> foodItemResponse = restClient.get()
                .uri("http://RESTAURANT-SERVICE/fooditem/{id}", request.getFoodItemId())
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {});

        GetFoodItemsResponse foodItem = foodItemResponse.getData();

        Cart cart = cartRepository.findByCustomer(customer).orElseThrow(() -> new CartItemNotFoundException("Cart Is Empty"));
        CartItem cartItem = cartItemRepository.findByCartAndFoodItemId(cart, foodItem.getId()).orElseThrow(() -> new CartItemNotFoundException("Cart Item Not Found for Given Food Item"));
        
        cartItem.setQuantity(request.getQuantity());
        cartItemRepository.save(cartItem);
    }

    @Transactional 
    public void deleteCartItem(Long foodItemId){
        String email = jwtService.extractEmail();

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        
        Cart cart = cartRepository.findByCustomer(customer).orElseThrow(() -> new CartItemNotFoundException("Cart Is Empty"));
        CartItem cartItem = cartItemRepository.findByCartAndFoodItemId(cart, foodItemId).orElseThrow(() -> new CartItemNotFoundException("Cart Item Not Found for Given Food Item"));

        cartItemRepository.delete(cartItem);

        if (!cartItemRepository.existsByCart(cart)) {
            cartRepository.delete(cart);
        }
    }
}