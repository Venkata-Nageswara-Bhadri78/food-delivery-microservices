package com.natixis.customer.common.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.common.entity.Customer;

public interface CustomerRepository extends JpaRepository<Customer, Long>{
    boolean existsByUserId(Long userId);
    Optional<Customer> findByEmail(String email);

}