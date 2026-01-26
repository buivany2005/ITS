package com.example.backend.dto;

import com.example.backend.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleResponse {
    private Long id;
    private String name;
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private String color;
    private Integer seats;
    private String fuelType;
    private String description;
    private BigDecimal rentalPricePerDay;
    private String status;
    private String imageUrl;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public static VehicleResponse fromEntity(Vehicle vehicle) {
        return VehicleResponse.builder()
                .id(vehicle.getId())
                .name(vehicle.getName())
                .brand(vehicle.getBrand())
                .model(vehicle.getModel())
                .year(vehicle.getYear())
                .licensePlate(vehicle.getLicensePlate())
                .color(vehicle.getColor())
                .seats(vehicle.getSeats())
                .fuelType(vehicle.getFuelType())
                .description(vehicle.getDescription())
                .rentalPricePerDay(vehicle.getRentalPricePerDay())
                .status(vehicle.getStatus().name())
                .imageUrl(vehicle.getImageUrl())
                .createdAt(vehicle.getCreatedAt())
                .updatedAt(vehicle.getUpdatedAt())
                .build();
    }
}