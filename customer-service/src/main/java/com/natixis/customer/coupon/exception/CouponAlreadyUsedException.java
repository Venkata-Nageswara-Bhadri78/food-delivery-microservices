package com.natixis.customer.coupon.exception;

public class CouponAlreadyUsedException extends RuntimeException{
    public CouponAlreadyUsedException(String message){
        super(message);
    }
}
