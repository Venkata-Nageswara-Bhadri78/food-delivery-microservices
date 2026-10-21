package com.natixis.customer.order.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.order.entity.Order;
import com.natixis.customer.order.entity.OrderItem;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {
    public List<OrderItem> findByOrder(Order order);
    
}
