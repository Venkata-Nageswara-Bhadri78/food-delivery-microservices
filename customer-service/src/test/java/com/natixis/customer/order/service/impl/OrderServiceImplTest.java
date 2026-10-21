package com.natixis.customer.order.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.springframework.http.MediaType;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.web.client.RestClient;

import com.natixis.customer.cart.entity.Cart;
import com.natixis.customer.cart.entity.CartItem;
import com.natixis.customer.cart.repository.CartItemRepository;
import com.natixis.customer.cart.repository.CartRepository;
import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.repository.CustomerRepository;
import com.natixis.customer.common.security.JwtService;
import com.natixis.customer.coupon.entity.Coupon;
import com.natixis.customer.coupon.exception.CouponAlreadyUsedException;
import com.natixis.customer.coupon.repository.CouponRepository;
import com.natixis.customer.coupon.repository.CouponUsageRepository;
import com.natixis.customer.order.dto.response.CreateOrderResponse;
import com.natixis.customer.order.entity.Order;
import com.natixis.customer.order.enums.OrderStatus;
import com.natixis.customer.order.repository.OrderRepository;
import com.natixis.customer.restclient.GetFoodItemsResponse;
import com.natixis.customer.restclient.GetRestaurantResponse;

@ExtendWith(MockitoExtension.class)
class OrderServiceImplTest {

    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private OrderRepository orderRepository;
    @Mock
    private CartRepository cartRepository;
    @Mock
    private CartItemRepository cartItemRepository;
    @Mock
    private CouponRepository couponRepository;
    @Mock
    private CouponUsageRepository couponUsageRepository;
    @Mock
    private RestClient restClient;
    @Mock
    private JwtService jwtService;
    @Mock
    private RestClient.RequestHeadersUriSpec<?> getSpec;
    @Mock
    private RestClient.RequestHeadersSpec<?> headersSpec;
    @Mock
    private RestClient.ResponseSpec responseSpec;
    @Mock
    private RestClient.ResponseSpec postResponseSpec;
    @Mock
    private RestClient.RequestBodyUriSpec postSpec;
    @Mock
    private RestClient.RequestBodyUriSpec patchSpec;
    @Mock
    private RestClient.ResponseSpec patchResponseSpec;

    private OrderServiceImpl orderService;
    private Customer customer;
    private Cart cart;

    @BeforeEach
    void setUp() {
        orderService = new OrderServiceImpl(
                customerRepository,
                orderRepository,
                cartRepository,
                cartItemRepository,
                couponRepository,
                couponUsageRepository,
                restClient,
                jwtService);
        customer = Customer.builder()
                .id(1L)
                .email("ada@test.com")
                .userId(3L)
                .phone("9999999999")
                .address("12 Main Street")
                .build();
        cart = Cart.builder().customer(customer).restaurantId(5L).build();
        when(jwtService.extractEmail()).thenReturn("ada@test.com");
        when(customerRepository.findByEmail("ada@test.com")).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));
    }

    @Test
    void createOrderAppliesCouponAndAssignsAgent() {
        CartItem item = CartItem.builder().cart(cart).foodItemId(7L).quantity(2).build();
        Coupon coupon = coupon();
        when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(item));
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponUsageRepository.existsByCustomerAndCoupon(customer, coupon)).thenReturn(false);
        when(orderRepository.save(any(Order.class))).thenAnswer(invocation -> {
            Order saved = invocation.getArgument(0);
            saved.setId(100L);
            return saved;
        });
        stubRestaurant();
        stubFood();
        stubUpdateQuantity();
        stubAssignAgent(15L);

        CreateOrderResponse response = orderService.createOrder("SAVE10");

        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        assertEquals(20.0, captor.getValue().getSubtotal());
        assertEquals(2.0, captor.getValue().getDiscount(), 0.001);
        assertEquals(18.0, captor.getValue().getTotalAmount(), 0.001);
        assertEquals(OrderStatus.CREATED, captor.getValue().getOrderStatus());
        assertEquals(100L, response.getOrderId());
        assertEquals(15L, response.getAgentId());
        verify(cartItemRepository).deleteAllByCart(cart);
        verify(cartRepository).deleteByCustomer(customer);
        verify(restClient).patch();
    }

    @Test
    void createOrderRejectsCouponThatWasAlreadyUsed() {
        CartItem item = CartItem.builder().cart(cart).foodItemId(7L).quantity(1).build();
        Coupon coupon = coupon();
        when(cartItemRepository.findAllByCart(cart)).thenReturn(List.of(item));
        when(couponRepository.findByCode("SAVE10")).thenReturn(Optional.of(coupon));
        when(couponUsageRepository.existsByCustomerAndCoupon(customer, coupon)).thenReturn(true);
        stubRestaurant();

        assertThrows(CouponAlreadyUsedException.class, () -> orderService.createOrder("SAVE10"));
        verify(orderRepository, never()).save(any());
    }

    private void stubRestaurant() {
        doReturn(getSpec).when(restClient).get();
        when(headersSpec.retrieve()).thenReturn(responseSpec);
        when(getSpec.uri(eq("http://RESTAURANT-SERVICE/restaurant/{id}"), any(Object[].class))).thenAnswer(invocation -> {
            when(responseSpec.body(ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<GetRestaurantResponse>>>any())).thenReturn(new ApiResponse<GetRestaurantResponse>(
                    true, "ok", new GetRestaurantResponse(5L, "Oven", "Kitchen Street", "Pizza place")));
            return headersSpec;
        });
    }

    private void stubFood() {
        when(getSpec.uri(eq("http://RESTAURANT-SERVICE/fooditem/{id}"), any(Object[].class))).thenAnswer(invocation -> {
            when(responseSpec.body(ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>>any())).thenReturn(new ApiResponse<GetFoodItemsResponse>(
                    true, "ok", new GetFoodItemsResponse(7L, "Pizza", 10.0, "Cheese", true, 4)));
            return headersSpec;
        });
    }

    private void stubUpdateQuantity() {
        doReturn(patchSpec).when(restClient).patch();
        when(patchSpec.uri("http://RESTAURANT-SERVICE/fooditem/update-fooditems-quantity")).thenReturn(patchSpec);
        when(patchSpec.contentType(MediaType.APPLICATION_JSON)).thenReturn(patchSpec);
        doReturn(patchSpec).when(patchSpec).body(any(Object.class));
        when(patchSpec.retrieve()).thenReturn(patchResponseSpec);
        when(patchResponseSpec.body(ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Object>>>any()))
                .thenReturn(new ApiResponse<Object>(true, "updated", null));
    }

    private void stubAssignAgent(Long agentId) {
        doReturn(postSpec).when(restClient).post();
        when(postSpec.uri(anyString(), any(Object[].class))).thenReturn(postSpec);
        when(postSpec.retrieve()).thenReturn(postResponseSpec);
        when(postResponseSpec.body(ArgumentMatchers.<ParameterizedTypeReference<ApiResponse<Long>>>any()))
                .thenReturn(new ApiResponse<Long>(true, "assigned", agentId));
    }

    private static Coupon coupon() {
        return Coupon.builder()
                .id(1L)
                .code("SAVE10")
                .couponType("PERCENT")
                .discountPercent(10.0)
                .expirationTime(LocalDateTime.now().plusDays(1))
                .isActive(true)
                .build();
    }
}
