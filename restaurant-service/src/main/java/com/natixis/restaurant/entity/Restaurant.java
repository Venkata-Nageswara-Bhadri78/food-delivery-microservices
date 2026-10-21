package com.natixis.restaurant.entity;

import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Builder 
@Table (name = "restaurants")
public class Restaurant {
    @Id 
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private String description;

    @Column (nullable = false)
    private String address;

    @Builder.Default
    @OneToMany (mappedBy = "restaurant", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<FoodItem> foodItems = new ArrayList<>();
}

/*
1. User Table
This is completely independent table which is used for user registration and login

2. Restaurant
There should be multiple restaurants consisting only the restaurant related information like restaurant_id, name, address

3. Food Items Table
This table should contains food item details along with from which restaurant that food item belongs to.
One Restautant can contains multiple food items and one food item can be available from multiple restaurants

4. Cart
When user wants to place an order, the items will be added to cart. 
The cart table contains food items details from food table by it's id, restaurant info from restaurant id

Each cart can contains extracted information from Restaurant and FoodItems table
This table records contains the food item details, restaurant the food item belongs to.


5. Order
This is the same cart table but once user places an order then the same record is deleted from carts table and added to orders table

6. Coupen:
Once user can use one coupen for only one time, but he can use different coupens.
This contains the coupen id, coupen Code, expiration time
*/