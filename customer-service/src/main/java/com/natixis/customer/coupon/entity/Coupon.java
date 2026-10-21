package com.natixis.customer.coupon.entity;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "coupons")
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String code;

    @Column(nullable = false)
    private String couponType;

    @Column(nullable = false)
    private Double discountPercent;

    @Column(nullable = false)
    private LocalDateTime expirationTime;

    @Column(nullable = false)
    private Boolean isActive;

    @Builder.Default
    @OneToMany(
        mappedBy = "coupon"
    )
    private List<CouponUsage> couponUsages = new ArrayList<>();
}