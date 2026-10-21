package com.natixis.customer.order.exception;

public class AgentNotFoundException extends RuntimeException {
    public AgentNotFoundException(String message){
        super(message);
    }
}
