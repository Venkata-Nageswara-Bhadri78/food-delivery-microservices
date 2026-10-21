package com.natixis.customer.cart.exception;

public class InvalidCartItemException extends RuntimeException{
    public InvalidCartItemException(String message){
        super(message);
    }
}
