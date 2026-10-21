package com.natixis.restaurant.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Builder 
@Getter 
@Setter 
@NoArgsConstructor 
@AllArgsConstructor 
@Table (name = "food_items")
public class FoodItem {
    
    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private String name;

    @Column (nullable = false)
    private Double price;

    @Column (nullable = false)
    private String description;

    @Column (nullable = false)
    private Boolean isAvailable;

    @Column(nullable = false)
    @Min (0)
    private Integer quantity;

    @ManyToOne
    @JoinColumn(name = "restaurant_id", nullable = false)
    private Restaurant restaurant;

    @PrePersist
    @PreUpdate
    protected void updateItemAvailability(){
        isAvailable = quantity != null && quantity > 0;
    }
}
