package com.natixis.agent.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.natixis.agent.entity.Agent;
import com.natixis.agent.enums.AgentAvailabilityStatus;

public interface AgentRepository extends JpaRepository<Agent, Long>{
    boolean existsByUserId(Long userId);
    boolean existsByEmail(String email);
    Agent findByUserId(Long userId);
    Optional<Agent> findByEmail(String email);
    Page<Agent> findAll(Pageable page);
    Optional<Agent> findFirstByAvailabilityStatusAndOrderIdIsNull(AgentAvailabilityStatus status);
}
