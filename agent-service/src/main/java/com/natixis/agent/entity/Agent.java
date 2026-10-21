package com.natixis.agent.entity;

import java.time.LocalDateTime;
import com.natixis.agent.enums.AgentAvailabilityStatus;
import com.natixis.agent.enums.VehicleType;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity 
@Getter 
@Setter 
@Builder 
@NoArgsConstructor 
@AllArgsConstructor 
public class Agent {

    @Id 
    @GeneratedValue (strategy = GenerationType.IDENTITY)
    private Long id;

    @Column (nullable = false)
    private Long userId;

    @Column (nullable = false, unique = true)
    private String email;

    @Builder.Default
    private Long orderId = null;

    private String vehicleNumber;

    private String drivingLicenseNumber;

    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;

    @Column (nullable = false)
    @Enumerated (EnumType.STRING)
    private AgentAvailabilityStatus availabilityStatus;

    @Builder.Default
    @Column(nullable = false)
    private boolean isApproved = false;

    @Column (nullable = false)
    private LocalDateTime createdAt;

    @Column (nullable = false)
    private LocalDateTime updatedAt;

    @PrePersist 
    protected void onCreate(){
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate 
    protected void onUpdate(){
        updatedAt = LocalDateTime.now();
    }
}
/*

    private Long userId;
    private String vehicleNumber;
    private VehicleType vehicleType;
    private String drivingLicenseNumber;
    
    */