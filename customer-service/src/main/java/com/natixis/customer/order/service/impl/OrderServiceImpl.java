package com.natixis.customer.order.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.natixis.customer.cart.entity.Cart;
import com.natixis.customer.cart.entity.CartItem;
import com.natixis.customer.cart.exception.CartItemNotFoundException;
import com.natixis.customer.cart.repository.CartItemRepository;
import com.natixis.customer.cart.repository.CartRepository;
import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.exception.CustomerNotFoundException;
import com.natixis.customer.common.repository.CustomerRepository;
import com.natixis.customer.common.security.JwtService;
import com.natixis.customer.coupon.entity.Coupon;
import com.natixis.customer.coupon.entity.CouponUsage;
import com.natixis.customer.coupon.exception.CouponAlreadyUsedException;
import com.natixis.customer.coupon.exception.CouponNotFoundException;
import com.natixis.customer.coupon.repository.CouponRepository;
import com.natixis.customer.coupon.repository.CouponUsageRepository;
import com.natixis.customer.order.dto.request.UpdateFoodItemQuantityRequest;
import com.natixis.customer.order.dto.response.CreateOrderResponse;
import com.natixis.customer.order.dto.response.GetOrderResponse;
import com.natixis.customer.order.dto.response.OrderItemDetailsResponse;
import com.natixis.customer.order.entity.Order;
import com.natixis.customer.order.entity.OrderItem;
import com.natixis.customer.order.enums.OrderStatus;
import com.natixis.customer.order.exception.AgentNotFoundException;
import com.natixis.customer.order.exception.OrdersNotFoundException;
import com.natixis.customer.order.repository.OrderRepository;
import com.natixis.customer.order.service.OrderService;
import com.natixis.customer.restclient.GetFoodItemsResponse;
import com.natixis.customer.restclient.GetRestaurantResponse;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class OrderServiceImpl implements OrderService {

    private final CustomerRepository customerRepository;
    private final OrderRepository orderRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final CouponRepository couponRepository;
    private final CouponUsageRepository couponUsageRepository;
    private final RestClient restClient;
    private final JwtService jwtService;

    @Transactional 
    public CreateOrderResponse createOrder(String code){
        String email = jwtService.extractEmail();
        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));

        Cart cart = cartRepository.findByCustomer(customer).orElseThrow(() -> new CartItemNotFoundException("No Items in Cart to Place an order"));

        ApiResponse<GetRestaurantResponse> restaurantResponse = restClient.get()
                .uri("http://RESTAURANT-SERVICE/restaurant/{id}", cart.getRestaurantId())
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<GetRestaurantResponse>>() {});
        GetRestaurantResponse restarantInfo = restaurantResponse.getData();

        List<CartItem> cartItems = cartItemRepository.findAllByCart(cart);
        if(cartItems.isEmpty()){
            throw new CartItemNotFoundException("No Cart Items to place an order");
        }
        
        Coupon coupon = couponRepository.findByCode(code).orElseThrow(() -> new CouponNotFoundException("Coupon Not Available"));

        if (couponUsageRepository.existsByCustomerAndCoupon(customer, coupon)) {
            throw new CouponAlreadyUsedException("Coupon Already Used");
        }

        Order order = Order.builder()
                        .customer(customer)
                        .subtotal(0.0)
                        .discount(0.0)
                        .deliveryFee(0.0)
                        .totalAmount(0.0)
                        .orderStatus(OrderStatus.CREATED)
                        .pickupAddress(restarantInfo.getAddress())
                        .deliveryAddress(customer.getAddress())
                        .build();

        Double subTotal = 0.0;
        List<OrderItem> orderItemsList = new ArrayList<>();
        for(CartItem cartItem : cartItems){
            ApiResponse<GetFoodItemsResponse> foodItemApiResponse = restClient.get()
                    .uri("http://RESTAURANT-SERVICE/fooditem/{id}", cartItem.getFoodItemId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {});
            GetFoodItemsResponse foodItemsResponse = foodItemApiResponse.getData();
            OrderItem tempOrderItem = OrderItem.builder()
                                        .order(order)
                                        .foodItemId(cartItem.getFoodItemId())
                                        .quantity(cartItem.getQuantity())
                                        .unitPrice(foodItemsResponse.getPrice())
                                        .totalPrice(foodItemsResponse.getPrice()*cartItem.getQuantity())
                                        .build();
            
            orderItemsList.add(tempOrderItem);
            subTotal += foodItemsResponse.getPrice()*cartItem.getQuantity();
        }

        List<UpdateFoodItemQuantityRequest> quantityUpdates = orderItemsList.stream()
                .map(item -> new UpdateFoodItemQuantityRequest(item.getFoodItemId(), item.getQuantity()))
                .toList();
        ApiResponse<String> quantityResponse = restClient.patch()
                .uri("http://RESTAURANT-SERVICE/fooditem/update-fooditems-quantity")
                .contentType(MediaType.APPLICATION_JSON)
                .body(quantityUpdates)
                .retrieve()
                .body(new ParameterizedTypeReference<ApiResponse<String>>() {});
        if (quantityResponse == null || !Boolean.TRUE.equals(quantityResponse.getSuccess())) {
            throw new CartItemNotFoundException("Unable to update food item quantity");
        }

        Double discountAmount = subTotal*(coupon==null ? 0 : coupon.getDiscountPercent()*0.01);
        Double finalTotalAmount = subTotal - discountAmount;

        order.setDiscount(discountAmount);
        order.setOrderItems(orderItemsList);
        order.setSubtotal(subTotal);
        order.setTotalAmount(finalTotalAmount);

        if(coupon != null){
            CouponUsage couponUsage = CouponUsage.builder()
                        .customer(customer)
                        .coupon(coupon)
                        .build();
            couponUsageRepository.save(couponUsage);
        }

        Order savedOrder = orderRepository.save(order);
        
        cartItemRepository.deleteAllByCart(cart);
        cartRepository.deleteByCustomer(customer);

        ApiResponse<Long> getAssignedAgent = restClient.post()
                    .uri("http://AGENT-SERVICE/agent/assign-order?id={id}", savedOrder.getId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<Long>>() {});
        if(!getAssignedAgent.getSuccess()){
            throw new AgentNotFoundException("Agent Not Found");
        }
        Long agentId = getAssignedAgent.getData();

        CreateOrderResponse createOrderResponse = new CreateOrderResponse(savedOrder.getId(), agentId);
        return createOrderResponse;
    }

    public Page<GetOrderResponse> getAllOrdersPage(Integer page){
        Pageable pageable = PageRequest.of(page, 20);
        Page<Order> orders = orderRepository.findAll(pageable);

        Page<GetOrderResponse> response = orders.map(order -> {
           List<OrderItemDetailsResponse> orderItems = order.getOrderItems().stream().map(item -> {
                GetFoodItemsResponse foodItem = restClient.get()
                    .uri("http://RESTAURANT-SERVICE/fooditem/{id}", item.getFoodItemId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {}).getData();

                return new OrderItemDetailsResponse(item.getFoodItemId(), foodItem.getName(), foodItem.getDescription(), page, item.getUnitPrice(), item.getTotalPrice());
           }).collect(Collectors.toList());
            return new GetOrderResponse(order.getSubtotal(),
                order.getDiscount(), 
                order.getDeliveryFee(), 
                order.getTotalAmount(), 
                order.getOrderStatus(), 
                order.getPickupAddress(), 
                order.getDeliveryAddress(), 
                order.getOrderDate(),
                orderItems
            );
        });

        return response;
    }

    public Page<GetOrderResponse> getCustomerOrders(Integer pageNumber){

        String email = jwtService.extractEmail();

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        Pageable pageable = PageRequest.of(pageNumber, 1);

        Page<Order> orderPage = orderRepository.findByCustomerOrderByOrderDateDesc(customer, pageable);
        if(orderPage.isEmpty()){
            throw new OrdersNotFoundException("No Orders Found");
        }

        Page<GetOrderResponse> response = orderPage.map(requiredOrder -> {
            List<OrderItemDetailsResponse> orderItemDetailsResponses = requiredOrder.getOrderItems().stream().map(item -> {
                ApiResponse<GetFoodItemsResponse> foodItemApiResponse = restClient.get()
                        .uri("http://RESTAURANT-SERVICE/fooditem/{id}", item.getFoodItemId())
                        .retrieve()
                        .body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {});
                GetFoodItemsResponse foodItemsResponse = foodItemApiResponse.getData();
                return new OrderItemDetailsResponse(
                    foodItemsResponse.getId(),
                    foodItemsResponse.getName(),
                    foodItemsResponse.getDescription(),
                    item.getQuantity(),
                    item.getUnitPrice(),
                    item.getTotalPrice()
                );
            }).collect(Collectors.toList());

            return new GetOrderResponse(
                requiredOrder.getSubtotal(),
                requiredOrder.getDiscount(),
                requiredOrder.getDeliveryFee(),
                requiredOrder.getTotalAmount(),
                requiredOrder.getOrderStatus(),
                requiredOrder.getPickupAddress(),
                requiredOrder.getDeliveryAddress(),
                requiredOrder.getOrderDate(),
                orderItemDetailsResponses
            );
        });

        return response;
    }

    public GetOrderResponse getOrderById(Long id){
        Order order = orderRepository.findById(id).orElseThrow(() -> new OrdersNotFoundException("Order Not Found"));

        List<OrderItemDetailsResponse> orderItemDetailsResponses = order.getOrderItems().stream().map(item -> {
            ApiResponse<GetFoodItemsResponse> foodItemApiResponse = restClient.get()
                    .uri("http://RESTAURANT-SERVICE/fooditem/{id}", item.getFoodItemId())
                    .retrieve()
                    .body(new ParameterizedTypeReference<ApiResponse<GetFoodItemsResponse>>() {});
            GetFoodItemsResponse foodItemsResponse = foodItemApiResponse.getData();
            return new OrderItemDetailsResponse(

                foodItemsResponse.getId(),
                foodItemsResponse.getName(),
                foodItemsResponse.getDescription(),
                item.getQuantity(),
                item.getUnitPrice(),
                item.getTotalPrice()
            );
        }).collect(Collectors.toList());

        GetOrderResponse getOrderResponse = new GetOrderResponse(
            order.getSubtotal(),
            order.getDiscount(),
            order.getDeliveryFee(),
            order.getTotalAmount(),
            order.getOrderStatus(),
            order.getPickupAddress(),
            order.getDeliveryAddress(),
            order.getOrderDate(),
            orderItemDetailsResponses
        );
        return getOrderResponse;
    }

    public void updateOrderDeliveredStatus(Long id){
        Order requiredOrder = orderRepository.findById(id).orElseThrow(() -> new OrdersNotFoundException("Order Not Found"));
        requiredOrder.setOrderStatus(OrderStatus.DELIVERIED);
        orderRepository.save(requiredOrder);
    }
}