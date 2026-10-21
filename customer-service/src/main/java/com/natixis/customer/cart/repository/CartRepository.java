package com.natixis.customer.cart.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.cart.entity.Cart;
import com.natixis.customer.common.entity.Customer;


public interface CartRepository extends JpaRepository<Cart, Long> {
    Optional<Cart> findByCustomer(Customer customer);
    Boolean existsByCustomer(Customer customer);
    void deleteByCustomer(Customer customer);
}
