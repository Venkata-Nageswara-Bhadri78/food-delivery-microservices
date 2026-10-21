package com.natixis.customer.common.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter 
@Setter 
@Entity 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
@Table (name = "customers")
public class Customer {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String email;

    @Column (name = "user_id")
    private Long userId;

    @Column(nullable = false)
    private String phone;

    @Column(nullable = false)
    private String address;
}
