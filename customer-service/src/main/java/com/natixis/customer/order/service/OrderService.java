package com.natixis.customer.order.service;

import org.springframework.data.domain.Page;

import com.natixis.customer.order.dto.response.CreateOrderResponse;
import com.natixis.customer.order.dto.response.GetOrderResponse;

public interface OrderService {
    public CreateOrderResponse createOrder(String code);
    public Page<GetOrderResponse> getCustomerOrders(Integer page);
    public Page<GetOrderResponse> getAllOrdersPage(Integer page);
    public GetOrderResponse getOrderById(Long id);

    public void updateOrderDeliveredStatus(Long id);
}
