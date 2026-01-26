package com.example.backend.dto;

import com.example.backend.entity.Vehicle;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VehicleRequest {

    @NotBlank(message = "Tên xe không được để trống")
    private String name;

    @NotBlank(message = "Hãng xe không được để trống")
    private String brand;

    @NotBlank(message = "Model không được để trống")
    private String model;

    @NotNull(message = "Năm sản xuất không được để trống")
    @Positive(message = "Năm sản xuất phải là số dương")
    private Integer year;

    @NotBlank(message = "Biển số xe không được để trống")
    private String licensePlate;

    private String color;

    @NotNull(message = "Số chỗ ngồi không được để trống")
    @Positive(message = "Số chỗ ngồi phải lớn hơn 0")
    private Integer seats;

    private String fuelType;

    private String description;

    @NotNull(message = "Giá thuê/ngày không được để trống")
    @Positive(message = "Giá thuê phải lớn hơn 0")
    private BigDecimal rentalPricePerDay;

    @NotBlank(message = "Trạng thái không được để trống")
    private String status;

    private String imageUrl;

    public Vehicle toEntity() {
        Vehicle vehicle = new Vehicle();
        vehicle.setName(this.name);
        vehicle.setBrand(this.brand);
        vehicle.setModel(this.model);
        vehicle.setYear(this.year);
        vehicle.setLicensePlate(this.licensePlate);
        vehicle.setColor(this.color);
        vehicle.setSeats(this.seats);
        vehicle.setFuelType(this.fuelType);
        vehicle.setDescription(this.description);
        vehicle.setRentalPricePerDay(this.rentalPricePerDay);
        
        try {
            vehicle.setStatus(Vehicle.VehicleStatus.valueOf(this.status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
        }
        
        vehicle.setImageUrl(this.imageUrl);
        return vehicle;
    }

    public void updateEntity(Vehicle vehicle) {
        vehicle.setName(this.name);
        vehicle.setBrand(this.brand);
        vehicle.setModel(this.model);
        vehicle.setYear(this.year);
        vehicle.setLicensePlate(this.licensePlate);
        vehicle.setColor(this.color);
        vehicle.setSeats(this.seats);
        vehicle.setFuelType(this.fuelType);
        vehicle.setDescription(this.description);
        vehicle.setRentalPricePerDay(this.rentalPricePerDay);
        
        try {
            vehicle.setStatus(Vehicle.VehicleStatus.valueOf(this.status.toUpperCase()));
        } catch (IllegalArgumentException e) {
            // Keep old status
        }
        
        if (this.imageUrl != null && !this.imageUrl.isEmpty()) {
            vehicle.setImageUrl(this.imageUrl);
        }
    }

    public static VehicleRequest fromEntity(Vehicle vehicle) {
        return VehicleRequest.builder()
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
                .build();
    }
}