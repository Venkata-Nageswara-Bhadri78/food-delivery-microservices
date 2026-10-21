package com.natixis.customer.order.exception;

public class OrdersNotFoundException extends RuntimeException{
    public OrdersNotFoundException(String message){
        super(message);
    }
}
