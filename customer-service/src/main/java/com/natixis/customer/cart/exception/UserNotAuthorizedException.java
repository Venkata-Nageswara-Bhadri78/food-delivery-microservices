package com.natixis.customer.cart.exception;

public class UserNotAuthorizedException extends RuntimeException {
    public UserNotAuthorizedException(String message){
        super(message);
    }
}
