package com.natixis.admin.dto.response;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
@AllArgsConstructor 
public class GetAgentResponse {
    private Long userId;
    private String email;
    List<Long> orderIds;
}
