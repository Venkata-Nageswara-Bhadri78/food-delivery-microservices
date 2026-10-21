package com.natixis.customer.coupon.service;

import org.springframework.data.domain.Page;

import com.natixis.customer.coupon.dto.response.GetCouponsResponse;

public interface CouponService {
    public Page<GetCouponsResponse> getCoupons(Integer page);
    public void addUsedCoupon(Long couponId);
}
