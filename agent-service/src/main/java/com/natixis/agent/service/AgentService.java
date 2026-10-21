package com.natixis.agent.service;

import org.springframework.data.domain.Page;

import com.natixis.agent.dto.request.CreateAgentRequest;
import com.natixis.agent.dto.response.GetAgentResponse;
import com.natixis.agent.dto.response.GetOrderResponse;

public interface AgentService {
    public Long createAgent(CreateAgentRequest createAgentRequest);
    public String isAgentApproved();
    public GetOrderResponse getAssignedOrder();
    public Page<GetAgentResponse> getAllAgents(Integer page);
    public Long assignOrderToAvailableAgent(Long orderId);

    public void updateOrderDeliveredStatus();
}
