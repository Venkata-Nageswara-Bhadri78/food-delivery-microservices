package com.natixis.customer.order.controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.customer.common.dto.ApiResponse;
import com.natixis.customer.order.dto.response.CreateOrderResponse;
import com.natixis.customer.order.dto.response.GetOrderResponse;
import com.natixis.customer.order.service.OrderService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping("/orders")
@RequiredArgsConstructor 
public class OrderController {

    private final OrderService orderService;

    @PostMapping
    public ResponseEntity<ApiResponse<CreateOrderResponse>> createOrder(@Valid @RequestParam String code) {
        CreateOrderResponse createOrderResponse = orderService.createOrder(code);

        ApiResponse<CreateOrderResponse> response = new ApiResponse<>(
            true,
            "Orders Placed Successfully",
            createOrderResponse
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<GetOrderResponse>>> getAllOrders(@RequestParam(defaultValue = "0") String pageNumber) {
        Page<GetOrderResponse> orders = orderService.getAllOrdersPage(Integer.parseInt(pageNumber));

        ApiResponse<Page<GetOrderResponse>> pagedResponse = new ApiResponse<>(
            true,
            "Orders Retrieved Successfully",
            orders
        );
        return new ResponseEntity<>(pagedResponse, HttpStatus.OK);
    }
    
    @GetMapping
    public ResponseEntity<ApiResponse<Page<GetOrderResponse>>> getCustomerOrders(@RequestParam(defaultValue = "0") String page) {
        Integer p = Integer.parseInt(page);
        Page<GetOrderResponse> getOrderResponses = orderService.getCustomerOrders(p);
        ApiResponse<Page<GetOrderResponse>> response = new ApiResponse<>(
            true,
            "Orders Found",
            getOrderResponses
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/id")
    public ResponseEntity<ApiResponse<GetOrderResponse>> getOrderById(@RequestParam String id) {
        GetOrderResponse getOrderResponses = orderService.getOrderById(Long.parseLong(id));
        ApiResponse<GetOrderResponse> response = new ApiResponse<>(
            true,
            "Order Found",
            getOrderResponses
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping ("/delivered")
    public ResponseEntity<ApiResponse<String>> updateOrderDeliveredStatus(@RequestParam String id) {
        orderService.updateOrderDeliveredStatus(Long.parseLong(id));
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Order Status Updated Successfully",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
