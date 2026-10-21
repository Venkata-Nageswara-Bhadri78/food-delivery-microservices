package com.natixis.agent.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.natixis.agent.dto.request.CreateAgentRequest;
import com.natixis.agent.dto.response.ApiResponse;
import com.natixis.agent.dto.response.GetAgentResponse;
import com.natixis.agent.dto.response.GetOrderResponse;
import com.natixis.agent.service.AgentService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.RequestParam;


@RestController 
@RequestMapping("/agent")
@RequiredArgsConstructor 
public class AgentController {

    private final AgentService agentService;
    
    @GetMapping ("/test")
    public ResponseEntity<ApiResponse<String>> displayTest(){
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "We got you! You will become and delivery agent once you pass the verification check by our admin",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<ApiResponse<String>> createAgent(@Valid @RequestBody CreateAgentRequest createAgentRequest){
        agentService.createAgent(createAgentRequest);
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "We got you! You will become and delivery agent once you pass the verification check by our admin",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/approved")
    public ResponseEntity<ApiResponse<String>> isAgentApproved() {
        String result = agentService.isAgentApproved();
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Agent Approvement Status retrieved",
            result
        );

        return new ResponseEntity<>(response, HttpStatus.OK);
    }
    
    @GetMapping("/all")
    public ResponseEntity<ApiResponse<Page<GetAgentResponse>>> getAllAgents(@RequestParam String page) {
        Page<GetAgentResponse> agents = agentService.getAllAgents(Integer.parseInt(page));
        ApiResponse<Page<GetAgentResponse>> response = new ApiResponse<>(
            true,
            "Agents Info Retrieved",
            agents
        );
        response.getTotalPages();
        response.setPage(agents.getNumber());
        response.setSize(agents.getSize());
        response.setTotalElements(agents.getTotalElements());
        response.setTotalPages(agents.getTotalPages());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping ("/assign-order")
    public ResponseEntity<ApiResponse<Long>> getAvailableAgent(@RequestParam String id) {
        Long agentId = agentService.assignOrderToAvailableAgent(Long.parseLong(id));
        ApiResponse<Long> response = new ApiResponse<>(
            true,
            "Agent ID Retrieved",
            agentId
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/assignedorder")
    public ResponseEntity<ApiResponse<GetOrderResponse>> getAssignedOrder() {
        GetOrderResponse order = agentService.getAssignedOrder();
        ApiResponse<GetOrderResponse> response = new ApiResponse<>(
            true,
            "Order Id Retrieved",
            order
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PatchMapping("/update-order-status")
    public ResponseEntity<ApiResponse<String>> updateOrderStatus() {
        agentService.updateOrderDeliveredStatus();
        ApiResponse<String> response = new ApiResponse<>(
            true,
            "Order Status Updated",
            null
        );
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

}
