package com.natixis.agent.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestClient;

import com.natixis.agent.dto.request.CreateAgentRequest;
import com.natixis.agent.entity.Agent;
import com.natixis.agent.enums.AgentAvailabilityStatus;
import com.natixis.agent.exception.AgentAlreadyExistsException;
import com.natixis.agent.exception.AgentNotFoundException;
import com.natixis.agent.repository.AgentRepository;
import com.natixis.agent.security.JwtService;

@ExtendWith(MockitoExtension.class)
class AgentServiceImplTest {

    @Mock
    private AgentRepository agentRepository;
    @Mock
    private JwtService jwtService;
    @Mock
    private RestClient restClient;

    private AgentServiceImpl agentService;

    @BeforeEach
    void setUp() {
        agentService = new AgentServiceImpl(agentRepository, jwtService, restClient);
    }

    @Test
    void createAgentStartsAsNotApproved() {
        when(agentRepository.existsByUserId(8L)).thenReturn(false);
        when(agentRepository.save(any(Agent.class))).thenAnswer(invocation -> {
            Agent saved = invocation.getArgument(0);
            saved.setId(4L);
            return saved;
        });

        Long agentId = agentService.createAgent(request());

        ArgumentCaptor<Agent> captor = ArgumentCaptor.forClass(Agent.class);
        verify(agentRepository).save(captor.capture());
        assertEquals(4L, agentId);
        assertFalse(captor.getValue().isApproved());
        assertEquals(AgentAvailabilityStatus.NOT_APPROVED, captor.getValue().getAvailabilityStatus());
    }

    @Test
    void createAgentRejectsDuplicateUser() {
        when(agentRepository.existsByUserId(8L)).thenReturn(true);

        assertThrows(AgentAlreadyExistsException.class, () -> agentService.createAgent(request()));
        verify(agentRepository, never()).save(any());
    }

    @Test
    void assignOrderStoresOrderOnAvailableAgent() {
        Agent agent = Agent.builder()
                .id(4L)
                .userId(8L)
                .email("agent@test.com")
                .availabilityStatus(AgentAvailabilityStatus.AVAILABLE)
                .isApproved(true)
                .build();
        when(agentRepository.findFirstByAvailabilityStatusAndOrderIdIsNull(AgentAvailabilityStatus.AVAILABLE))
                .thenReturn(Optional.of(agent));

        Long assignedId = agentService.assignOrderToAvailableAgent(21L);

        assertEquals(4L, assignedId);
        assertEquals(21L, agent.getOrderId());
        verify(agentRepository).save(agent);
    }

    @Test
    void assignOrderFailsWhenNoAgentIsAvailable() {
        when(agentRepository.findFirstByAvailabilityStatusAndOrderIdIsNull(AgentAvailabilityStatus.AVAILABLE))
                .thenReturn(Optional.empty());

        assertThrows(AgentNotFoundException.class, () -> agentService.assignOrderToAvailableAgent(21L));
        verify(agentRepository, never()).save(any());
    }

    private static CreateAgentRequest request() {
        CreateAgentRequest request = new CreateAgentRequest();
        request.setUserId(8L);
        request.setEmail("agent@test.com");
        request.setVehicleNumber("KA01AB1234");
        request.setDrivingLicenseNumber("DL123");
        return request;
    }
}
