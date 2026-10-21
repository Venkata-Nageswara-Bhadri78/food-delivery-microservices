package com.natixis.agent.exception;

public class OrderNotAssignedException extends RuntimeException {
    public OrderNotAssignedException(String message){
        super(message);
    }
}
