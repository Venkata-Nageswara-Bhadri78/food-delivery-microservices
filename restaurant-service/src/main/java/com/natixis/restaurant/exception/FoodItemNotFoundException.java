package com.natixis.restaurant.exception;

public class FoodItemNotFoundException extends RuntimeException{
    public FoodItemNotFoundException(String message){
        super(message);
    }
}
