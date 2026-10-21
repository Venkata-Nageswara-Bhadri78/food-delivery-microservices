package com.natixis.agent.service.impl;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;

import com.natixis.agent.dto.request.CreateAgentRequest;
import com.natixis.agent.dto.response.ApiResponse;
import com.natixis.agent.dto.response.GetAgentResponse;
import com.natixis.agent.dto.response.GetOrderResponse;
import com.natixis.agent.entity.Agent;
import com.natixis.agent.enums.AgentAvailabilityStatus;
import com.natixis.agent.exception.AgentAlreadyExistsException;
import com.natixis.agent.exception.AgentNotFoundException;
import com.natixis.agent.exception.OrderNotAssignedException;
import com.natixis.agent.repository.AgentRepository;
import com.natixis.agent.security.JwtService;
import com.natixis.agent.service.AgentService;

import lombok.RequiredArgsConstructor;

@Service 
@RequiredArgsConstructor 
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final JwtService jwtService;

    private final RestClient restClient;

    public Long createAgent(CreateAgentRequest request){

        if(agentRepository.existsByUserId(request.getUserId())){
            throw new AgentAlreadyExistsException("Agent Already Applied");
        }

        Agent agent = Agent.builder()
                        .userId(request.getUserId())
                        .email(request.getEmail())
                        .vehicleNumber(request.getVehicleNumber())
                        .drivingLicenseNumber(request.getDrivingLicenseNumber())
                        .vehicleType(request.getVehicleType())
                        .availabilityStatus(AgentAvailabilityStatus.NOT_APPROVED)
                        .isApproved(false)
                        .build();
        Agent savedAgent = agentRepository.save(agent);
        return savedAgent.getId();
    }

    public String isAgentApproved(){
        String email = jwtService.extractEmail();
        Agent agent = agentRepository.findByEmail(email).orElseThrow(() -> new AgentNotFoundException("Agent Not Found"));
        if(agent.isApproved()){
            return "AGENT APPROVED";
        }
        return "AGENT STILL NOT APPROVED";
    }

    public GetOrderResponse getAssignedOrder(){
        String email = jwtService.extractEmail();
        Agent agent = agentRepository.findByEmail(email).orElseThrow(() -> new AgentNotFoundException("Agent Not Found"));
        if(agent.getOrderId()==null){
            throw new OrderNotAssignedException("No Order Assigned to you Yet");
        }
        ApiResponse<GetOrderResponse> orderResponse = restClient.get()
                            .uri("http://CUSTOMER-SERVICE/orders/id?id={id}", agent.getOrderId())
                            .retrieve()
                            .body(new ParameterizedTypeReference<ApiResponse<GetOrderResponse>>() {});
        return orderResponse.getData();
    }

    public Long assignOrderToAvailableAgent(Long orderId){
        Agent agent = agentRepository.findFirstByAvailabilityStatusAndOrderIdIsNull(AgentAvailabilityStatus.AVAILABLE).orElseThrow(() -> new AgentNotFoundException("Agent Not Available"));
        agent.setOrderId(orderId);
        agentRepository.save(agent);
        return agent.getId();
    }
    public Page<GetAgentResponse> getAllAgents(Integer page){
        Pageable pageable = PageRequest.of(page, 10);
        Page<Agent> agents = agentRepository.findAll(pageable);
        Page<GetAgentResponse> agentsRes = agents.map(agent -> {
            return new GetAgentResponse(agent.getUserId(), agent.getEmail(), agent.getOrderId());
        });
        return agentsRes;
    }

    public void updateOrderDeliveredStatus(){
        Agent agent = agentRepository.findByEmail(jwtService.extractEmail()).orElseThrow(() -> new AgentNotFoundException("Agent Not Found"));

        restClient.patch().uri("http://CUSTOMER-SERVICE/orders/delivered?id={id}", agent.getOrderId())
            .retrieve()
            .body(new ParameterizedTypeReference<ApiResponse<String>>() {});
        agent.setOrderId(null);
        agentRepository.save(agent);
    }
}
