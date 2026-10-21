package com.natixis.customer.coupon.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.common.exception.CustomerNotFoundException;
import com.natixis.customer.common.repository.CustomerRepository;
import com.natixis.customer.common.security.JwtService;
import com.natixis.customer.coupon.dto.response.GetCouponsResponse;
import com.natixis.customer.coupon.entity.Coupon;
import com.natixis.customer.coupon.entity.CouponUsage;
import com.natixis.customer.coupon.exception.CouponAlreadyUsedException;
import com.natixis.customer.coupon.exception.CouponNotFoundException;
import com.natixis.customer.coupon.repository.CouponRepository;
import com.natixis.customer.coupon.repository.CouponUsageRepository;
import com.natixis.customer.coupon.service.CouponService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class CouponServiceImpl implements CouponService {
    private final CouponUsageRepository couponUsageRepository;
    private final CouponRepository couponRepository;
    private final CustomerRepository customerRepository;

    private final JwtService jwtService;

    @Override 
    public Page<GetCouponsResponse> getCoupons(Integer page){
        Pageable pageable = PageRequest.of(page, 15);
        Page<Coupon> coupons = couponRepository.findAll(pageable);
        
        Page<GetCouponsResponse> couponsResponses = coupons.map((coupon) ->
            new GetCouponsResponse(
                coupon.getId(),
                coupon.getCode(),
                coupon.getCouponType(),
                coupon.getDiscountPercent(),
                coupon.getExpirationTime(),
                coupon.getIsActive()
            )
        );

        return couponsResponses;
    }

    public void addUsedCoupon(Long couponId){
        String email = jwtService.extractEmail();

        Customer customer = customerRepository.findByEmail(email).orElseThrow(() -> new CustomerNotFoundException("Customer Not Found"));
        
        Coupon coupon = couponRepository.findById(couponId).orElseThrow(() -> new CouponNotFoundException("Coupon Not Found"));
        if(couponUsageRepository.existsByCustomerAndCoupon(customer, coupon)){
            throw new CouponAlreadyUsedException("Coupon Already Used");
        }
        CouponUsage couponUsage = CouponUsage.builder()
                                    .customer(customer)
                                    .coupon(coupon)
                                    .build();
        couponUsageRepository.save(couponUsage);
    }

}
