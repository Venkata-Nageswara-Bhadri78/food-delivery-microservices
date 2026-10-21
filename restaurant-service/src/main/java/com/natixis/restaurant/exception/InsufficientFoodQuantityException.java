package com.natixis.restaurant.exception;

public class InsufficientFoodQuantityException extends RuntimeException {
    public InsufficientFoodQuantityException(String message) {
        super(message);
    }
}
