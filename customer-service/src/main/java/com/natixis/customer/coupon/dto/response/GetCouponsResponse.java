package com.natixis.customer.coupon.dto.response;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class GetCouponsResponse {
    private Long id;
    private String code;
    private String couponType;
    private Double discountValue;
    private LocalDateTime expirationTime;
    private Boolean isActive;
}
