package com.natixis.customer.cart.service.impl;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestClient;

import com.natixis.customer.cart.dto.request.AddCartItemRequest;
import com.natixis.customer.cart.entity.Cart;
import com.natixis.customer.cart.exception.InvalidCartItemException;
import com.natixis.customer.cart.repository.CartItemRepository;
import com.natixis.customer.cart.repository.CartRepository;
import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.repository.CustomerRepository;
import com.natixis.customer.common.security.JwtService;
import com.natixis.customer.restclient.GetFoodItemsResponse;
import com.natixis.customer.restclient.GetRestaurantResponse;

@ExtendWith(MockitoExtension.class)
class CartServiceImplTest {

    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RestClient restClient;
    @Mock
    private RestClient.RequestHeadersUriSpec<?> getSpec;
    @Mock
    private RestClient.RequestHeadersSpec<?> headersSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;

    private CartServiceImpl cartService;
    private Customer customer;

    @BeforeEach
    void setUp() {
        cartService = new CartServiceImpl(cartRepository, cartItemRepository, customerRepository, jwtService, restClient);
        customer = Customer.builder()
                .id(1L)
                .email("ada@test.com")
                .userId(3L)
                .phone("9999999999")
                .address("12 Main Street")
                .build();
        when(jwtService.extractEmail()).thenReturn("ada@test.com");
        when(customerRepository.findByEmail("ada@test.com")).thenReturn(Optional.of(customer));
    }

    @Test
    void addCartItemCreatesCartForFirstItem() {
        stubRestaurantCalls(restaurant(5L), food(7L));
        when(cartRepository.existsByCustomer(customer)).thenReturn(false);
        when(cartRepository.save(any(Cart.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(cartItemRepository.existsByCartAndFoodItemId(any(Cart.class), eq(7L))).thenReturn(false);

        cartService.addCartItem(request(7L, 2));

        verify(cartRepository).save(any(Cart.class));
        verify(cartItemRepository).save(any());
    }

    @Test
    void addCartItemRejectsFoodFromAnotherRestaurant() {
        Cart cart = Cart.builder().customer(customer).restaurantId(5L).build();
        stubRestaurantCalls(restaurant(9L), food(7L));
        when(cartRepository.existsByCustomer(customer)).thenReturn(true);
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));

        assertThrows(InvalidCartItemException.class, () -> cartService.addCartItem(request(7L, 1)));
        verify(cartItemRepository, never()).save(any());
    }

    private void stubRestaurantCalls(GetRestaurantResponse restaurant, GetFoodItemsResponse food) {
        doReturn(getSpec).when(restClient).get();
        when(headersSpec.retrieve()).thenReturn(responseSpec);

        when(getSpec.uri(eq("http://RESTAURANT-SERVICE/fooditem/{id}"),any(Object[].class))).thenAnswer(invocation -> {
            when(responseSpec.body(
                    ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>>any()
            )).thenReturn(
                    new ApiResponse<GetFoodItemsResponse>(true, "ok", food)
            );
            return headersSpec;
        });

        when(getSpec.uri(eq("http://RESTAURANT-SERVICE/restaurant/food/{id}"),any(Object[].class)
        )).thenAnswer(invocation -> {
            when(responseSpec.body(
                    ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<GetRestaurantResponse>>>any()
            )).thenReturn(
                    new ApiResponse<GetRestaurantResponse>(true, "ok", restaurant)
            );
            return headersSpec;
        });
    }

    private static AddCartItemRequest request(Long foodItemId, Integer quantity) {
        AddCartItemRequest request = new AddCartItemRequest();
        ReflectionTestUtils.setField(request, "foodItemId", foodItemId);
        ReflectionTestUtils.setField(request, "quantity", quantity);
        return request;
    }

    private static GetFoodItemsResponse food(Long id) {
        return new GetFoodItemsResponse(id, "Pizza", 10.0, "Cheese", true, 4);
    }

    private static GetRestaurantResponse restaurant(Long id) {
        return new GetRestaurantResponse(id, "Oven", "Kitchen Street", "Pizza place");
    }
}
