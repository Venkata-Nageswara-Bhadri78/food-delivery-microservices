package com.natixis.restaurant.service.impl;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import jakarta.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

import com.natixis.restaurant.entity.Restaurant;
import com.natixis.restaurant.exception.RestaurantNotFoundException;
import com.natixis.restaurant.dto.request.CreateRestaurantRequest;
import com.natixis.restaurant.dto.response.GetRestaurantResponse;
import com.natixis.restaurant.repository.RestaurantRepository;
import com.natixis.restaurant.service.RestaurantService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class RestaurantServiceImpl implements RestaurantService{

    private final RestaurantRepository restaurantRepository;
    
    public Long createRestaurant(CreateRestaurantRequest createRestaurantRequest){
        Restaurant restaurant = Restaurant.builder()
                                    .name(createRestaurantRequest.getName())
                                    .address(createRestaurantRequest.getAddress())
                                    .description(createRestaurantRequest.getDescription())
                                    .build();
        return restaurantRepository.save(restaurant).getId();
    }

    public Page<GetRestaurantResponse> getAllRestaurants(String name, String address, int page, String sortBy, String sortOrder) {
        if(page < 0){
            page = 0;
        }
        Sort.Direction direction = sortOrder.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, 15, Sort.by(direction, sortBy));

        Specification<Restaurant> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (name != null && !name.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("name")), "%" + name.toLowerCase() + "%"));
            }
            if (address != null && !address.trim().isEmpty()) {
                predicates.add(cb.like(cb.lower(root.get("address")), "%" + address.toLowerCase() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };

        Page<Restaurant> restaurantPage = restaurantRepository.findAll(spec, pageable);
        return restaurantPage.map(res -> new GetRestaurantResponse(
                res.getId(),
                res.getName(),
                res.getAddress(),
                res.getDescription()
            ));
    }

    public GetRestaurantResponse getRestaurantById(Long id){
        Restaurant res = restaurantRepository.findById(id).orElseThrow(() -> new RestaurantNotFoundException("Restaurant Not Found"));
        return new GetRestaurantResponse(
            res.getId(),
            res.getName(),
            res.getAddress(),
            res.getDescription()
        );
    }
    public GetRestaurantResponse getRestaurantByFoodItemId(Long id){
        Restaurant res = restaurantRepository.findByFoodItems_Id(id).orElseThrow(() -> new RestaurantNotFoundException("Restaurant Not Found"));
        return new GetRestaurantResponse(
            res.getId(),
            res.getName(),
            res.getAddress(),
            res.getDescription()
        );
    }

    public void deleteRestaurant(Long id){
        Restaurant res = restaurantRepository.findById(id).orElseThrow(() -> new RestaurantNotFoundException("Restaurant Not Found"));
        restaurantRepository.delete(res);
    }
}