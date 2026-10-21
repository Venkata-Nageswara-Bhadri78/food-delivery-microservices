package com.natixis.agent.dto.request;

import com.natixis.agent.enums.VehicleType;

import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter 
public class CreateAgentRequest {
    private Long userId;
    private String email;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String drivingLicenseNumber;
}
