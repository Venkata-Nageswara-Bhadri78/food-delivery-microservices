package com.natixis.customer.cart.exception;

public class CartItemAlreadyExistsException extends RuntimeException {
    public CartItemAlreadyExistsException(String message){
        super(message);
    }
}
