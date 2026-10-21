package com.natixis.customer.order.repository;

import org.springframework.data.domain.Pageable;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.common.entity.Customer;
import com.natixis.customer.order.entity.Order;

public interface OrderRepository extends JpaRepository<Order, Long> {
    Page<Order> findByCustomer(Customer customer, Pageable pageable);
    Page<Order> findByCustomerOrderByOrderDateDesc(Customer customer, Pageable pageable);
    Page<Order> findAll(Pageable pageable);

    Optional<Order> findById(Long id);
}
