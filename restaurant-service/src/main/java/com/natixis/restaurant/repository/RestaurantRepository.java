package com.natixis.restaurant.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.restaurant.entity.Restaurant;

import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RestaurantRepository extends JpaRepository<Restaurant, Long>, JpaSpecificationExecutor<Restaurant>{
    Optional<Restaurant> findByFoodItems_Id(Long foodItemId);
}