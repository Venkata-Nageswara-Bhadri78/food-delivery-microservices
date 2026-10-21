package com.natixis.auth.dto.request;

import com.natixis.auth.enums.VehicleType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter 
@Setter
@AllArgsConstructor  
public class CreateAgentRequest {
    private Long userId;
    private String email;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String drivingLicenseNumber;
}
