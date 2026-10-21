package com.natixis.restaurant.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.restaurant.entity.FoodItem;
import com.natixis.restaurant.entity.Restaurant;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface FoodItemRepository extends JpaRepository<FoodItem, Long>, JpaSpecificationExecutor<FoodItem>{
    Page<FoodItem> findAllByRestaurant(Restaurant restaurant, Pageable pageable);
    Optional<FoodItem> findById(Long id);
}
