package com.natixis.customer.coupon.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.coupon.entity.Coupon;
import com.natixis.customer.coupon.entity.CouponUsage;


public interface CouponUsageRepository extends JpaRepository<CouponUsage, Long>{
    boolean existsByCustomerAndCoupon(Customer customer, Coupon coupon);
}
