package com.natixis.customer.common.exception;

public class CustomerAlreadyExistsException extends RuntimeException {
    public CustomerAlreadyExistsException(String message){
        super(message);
    }
}
