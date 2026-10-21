package com.natixis.customer.coupon.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.coupon.dto.response.GetCouponsResponse;
import com.natixis.customer.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@RestController 
@RequiredArgsConstructor 
@RequestMapping ("/coupons")
public class CouponController {
    private final CouponService couponService;

    @GetMapping 
    public ResponseEntity<ApiResponse<Page<GetCouponsResponse>>> getCoupons(@RequestParam Integer page){
        Page<GetCouponsResponse> coupons = couponService.getCoupons(page);
        ApiResponse<Page<GetCouponsResponse>> response = new ApiResponse<>(
            true,
            "Coupons Received",
            coupons
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
