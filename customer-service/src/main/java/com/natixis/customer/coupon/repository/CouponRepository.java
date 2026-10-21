package com.natixis.customer.coupon.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.coupon.entity.Coupon;

public interface CouponRepository extends JpaRepository<Coupon, Long>{
    Optional<Coupon> findByCode(String code);
}
