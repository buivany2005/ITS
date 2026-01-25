package com.example.backend.dto;

import com.example.backend.entity.Vehicle;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class VehicleResponse {
    private Long id;
    private String name;
    private String category;  // Mapped from vehicleType
    private String brand;
    private String model;
    private Integer year;
    private String licensePlate;
    private String color;
    private Integer seats;
    private String transmission;
    private String fuelType;
    private BigDecimal pricePerDay;
    private String description;
    private String location;
    private String thumbnail;  // Mapped from imageUrl
    private String status;
    private BigDecimal rating;
    private Integer totalRentals;
    private LocalDateTime createdAt;
    
    public static VehicleResponse fromEntity(Vehicle vehicle) {
        VehicleResponse response = new VehicleResponse();
        response.setId(vehicle.getId());
        response.setName(vehicle.getName());
        response.setCategory(vehicle.getVehicleType() != null ? 
            vehicle.getVehicleType().name() : null);
        response.setBrand(vehicle.getBrand());
        response.setModel(vehicle.getModel());
        response.setYear(vehicle.getYear());
        response.setLicensePlate(vehicle.getLicensePlate());
        response.setColor(vehicle.getColor());
        response.setSeats(vehicle.getSeats());
        response.setTransmission(vehicle.getTransmission());
        response.setFuelType(vehicle.getFuelType());
        response.setPricePerDay(vehicle.getPricePerDay());
        response.setDescription(vehicle.getDescription());
        response.setLocation(vehicle.getLocation());
        response.setThumbnail(vehicle.getImageUrl());
        response.setStatus(vehicle.getStatus() != null ? 
            vehicle.getStatus().name() : null);
        response.setRating(vehicle.getRating());
        response.setTotalRentals(vehicle.getTotalRentals());
        response.setCreatedAt(vehicle.getCreatedAt());
        return response;
    }
}