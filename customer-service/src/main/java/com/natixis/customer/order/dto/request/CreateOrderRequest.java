package com.natixis.customer.order.dto.request;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CreateOrderRequest {
    private String couponCode;
}
