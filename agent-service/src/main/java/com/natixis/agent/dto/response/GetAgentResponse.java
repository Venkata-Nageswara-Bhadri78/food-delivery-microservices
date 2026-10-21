package com.natixis.agent.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
public class GetAgentResponse {
    private Long userId;
    private String email;
    Long orderIds;
}
