package com.natixis.agent.dto.response;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter 
@AllArgsConstructor 
public class GetAgentAssignedOrderResponse {
    private Long id;
    private String userName;
    private String phoneNumber;
    private String userAddress;
    private Double totalAmount;
    private LocalDateTime orderDate;
    private List<OrderItemDetailsResponse> orderItems;

}
