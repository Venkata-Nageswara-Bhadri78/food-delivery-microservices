package com.natixis.customer.cart.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.customer.cart.entity.Cart;
import com.natixis.customer.cart.entity.CartItem;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    List<CartItem> findAllByCart(Cart cart);
    Optional<CartItem> findByCartAndFoodItemId(Cart cart, Long foodItemId);
    Boolean existsByCartAndFoodItemId(Cart cart, Long foodItemId);
    void deleteAllByCart(Cart cart);
    boolean existsByCart(Cart cart);
    
}
