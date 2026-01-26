package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "vehicles")
public class Vehicle {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private String name;
    
    @Column(name = "vehicle_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private VehicleType vehicleType;
    
    private String brand;
    private String model;
    private Integer year;
    
    @Column(name = "license_plate")
    private String licensePlate;
    
    private String color;
    private Integer seats;
    private String transmission;
    
    @Column(name = "fuel_type")
    private String fuelType;
    
    @Column(name = "price_per_day", nullable = false)
    private BigDecimal pricePerDay;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    private String location;
    
    @Column(name = "image_url", columnDefinition = "TEXT")
    private String imageUrl;
    
    @Enumerated(EnumType.STRING)
    private VehicleStatus status = VehicleStatus.AVAILABLE;
    
    private BigDecimal rating;
    
    @Column(name = "total_rentals")
    private Integer totalRentals = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public enum VehicleType {
        OTO, XEMAY, XEDAP
    }
    
    public enum VehicleStatus {
        AVAILABLE, RENTED, MAINTENANCE
    }
}
