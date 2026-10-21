package com.natixis.customer.order.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class CreateOrderResponse {
    private Long orderId;
    private Long agentId;
}
