package com.natixis.agent.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import com.natixis.agent.enums.OrderStatus;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class GetOrderResponse {


    private Double subTotal;
    private Double discount;
    private Double deliveryFee;
    private Double totalAmount;
    private OrderStatus orderStatus;
    private String pickupAddress;
    private String deliveryAddress;
    private LocalDateTime orderDate;

    private List<OrderItemDetailsResponse> orderItemDetailsResponse;
}