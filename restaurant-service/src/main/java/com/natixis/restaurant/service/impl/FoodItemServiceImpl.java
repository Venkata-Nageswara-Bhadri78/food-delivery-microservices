package com.natixis.restaurant.service.impl;

import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import com.natixis.restaurant.dto.request.CreateFoodItemRequest;
import com.natixis.restaurant.dto.request.UpdateFoodItemQuantityRequest;
import com.natixis.restaurant.dto.response.GetFoodItemsResponse;
import com.natixis.restaurant.entity.FoodItem;
import com.natixis.restaurant.entity.Restaurant;
import com.natixis.restaurant.exception.FoodItemNotFoundException;
import com.natixis.restaurant.exception.InsufficientFoodQuantityException;
import com.natixis.restaurant.exception.RestaurantNotFoundException;
import com.natixis.restaurant.repository.FoodItemRepository;
import com.natixis.restaurant.repository.RestaurantRepository;
import com.natixis.restaurant.service.FoodItemService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class FoodItemServiceImpl implements FoodItemService {

    private final FoodItemRepository foodItemRepository;
    private final RestaurantRepository restaurantRepository;

    public Long createFoodItem(CreateFoodItemRequest createFoodItemRequest) {
        boolean isAvailable = createFoodItemRequest.getQuantity() > 0 ? true : false;
        Restaurant restaurant = restaurantRepository.findById(createFoodItemRequest.getRestaurantId())
                .orElseThrow(() -> new RestaurantNotFoundException("Restaurant Not Found"));
        FoodItem foodItem = FoodItem.builder()
                .name(createFoodItemRequest.getName())
                .price(createFoodItemRequest.getPrice())
                .description(createFoodItemRequest.getDescription())
                .isAvailable(isAvailable)
                .quantity(createFoodItemRequest.getQuantity())
                .restaurant(restaurant)
                .build();
        return foodItemRepository.save(foodItem).getId();
    }

    public Page<GetFoodItemsResponse> getFoodItems(Long restaurantId, String name, Double minPrice, Double maxPrice, Boolean isAvailable, int page, String sortBy, String sortOrder){
        if(page < 0 ){
            page = 0;
        }
        Restaurant restaurant = restaurantRepository.findById(restaurantId).orElseThrow(() -> new RestaurantNotFoundException("Restaurant Not Found"));

        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, 15, Sort.by(direction, sortBy));

        Specification<FoodItem> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("restaurant"), restaurant));

            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (minPrice != null) {
                predicates.add(cb.ge(root.get("price"), minPrice));
            }
            if (maxPrice != null) {
                predicates.add(cb.le(root.get("price"), maxPrice));
            }
            if (isAvailable != null) {
                predicates.add(cb.equal(root.get("isAvailable"), isAvailable));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<FoodItem> foodItem = foodItemRepository.findAll(spec, pageable);
        return foodItem.map(item -> new GetFoodItemsResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDescription(),
                item.getIsAvailable(),
                item.getQuantity()
            ));
    }
    public GetFoodItemsResponse getFoodItemById(Long id){
        FoodItem item = foodItemRepository.findById(id).orElseThrow(() -> new FoodItemNotFoundException("Food Item Not Found"));

        GetFoodItemsResponse response = new GetFoodItemsResponse(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getDescription(),
                item.getIsAvailable(),
            item.getQuantity()
        );
        return response;
    }

    public void deleteFoodItem(Long restaurantId, Long foodItemId){
        FoodItem item = foodItemRepository.findById(foodItemId)
                .orElseThrow(() -> new FoodItemNotFoundException("Food Item Not Found"));
        if (!item.getRestaurant().getId().equals(restaurantId)) {
            throw new FoodItemNotFoundException("Food Item does not belong to the given Restaurant");
        }
        foodItemRepository.delete(item);
    }

    @Transactional
    public void updateFoodItemsQuantity(List<UpdateFoodItemQuantityRequest> items) {
        for (UpdateFoodItemQuantityRequest request : items) {
            FoodItem item = foodItemRepository.findById(request.getFoodItemId())
                    .orElseThrow(() -> new FoodItemNotFoundException("Food Item Not Found"));
            int remaining = item.getQuantity() - request.getQuantity();
            if (remaining < 0) {
                throw new InsufficientFoodQuantityException("Not enough quantity available for " + item.getName());
            }
            item.setQuantity(remaining);
            foodItemRepository.save(item);
        }
    }
}