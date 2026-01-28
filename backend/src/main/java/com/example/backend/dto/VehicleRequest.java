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
<<<<<<< HEAD
    
    @NotBlank(message = "Tên xe là bắt buộc")
    @Size(min = 3, max = 100, message = "Tên xe phải từ 3 đến 100 ký tự")
    public String name;
    
    @NotBlank(message = "Loại xe là bắt buộc")
    public String type;
    
    @NotBlank(message = "Hãng xe là bắt buộc")
    @Size(min = 2, max = 50, message = "Hãng xe phải từ 2 đến 50 ký tự")
    public String brand;
    
    @NotBlank(message = "Model là bắt buộc")
    @Size(min = 1, max = 50, message = "Model phải từ 1 đến 50 ký tự")
    public String model;
    
    @NotNull(message = "Năm sản xuất là bắt buộc")
    @Min(value = 2000, message = "Năm sản xuất phải từ 2000 trở lên")
    @Max(value = 2030, message = "Năm sản xuất không được vượt quá 2030")
    public Integer year;
    
    @NotBlank(message = "Màu sắc là bắt buộc")
    public String color;
    
    @NotBlank(message = "Biển số xe là bắt buộc")
    @Pattern(regexp = "\\d{2}[A-Z]-\\d+\\.\\d+", message = "Biển số xe không hợp lệ (ví dụ: 29-G1 123.45)")
    public String licensePlate;
    
    @NotNull(message = "Giá thuê/ngày là bắt buộc")
    @DecimalMin(value = "1000", message = "Giá thuê phải ≥ 1000 VNĐ")
    @DecimalMax(value = "1000000", message = "Giá thuê không được vượt quá 1.000.000 VNĐ")
    public BigDecimal pricePerDay;
    
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    public String description;
    
    @Size(max = 255, message = "URL hình ảnh không được vượt quá 255 ký tự")
    public String imageUrl;
    
    @Min(value = 1, message = "Số chỗ ngồi phải ≥ 1")
    @Max(value = 50, message = "Số chỗ ngồi không được vượt quá 50")
    public Integer seats;
    
    @Size(max = 50, message = "Loại nhiên liệu không được vượt quá 50 ký tự")
    public String fuelType;
    
    @Size(max = 50, message = "Truyền động không được vượt quá 50 ký tự")
    public String transmission;
    
    public String status;
    
    // Getters and Setters
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }
    
    public String getModel() { return model; }
    public void setModel(String model) { this.model = model; }
    
    public Integer getYear() { return year; }
    public void setYear(Integer year) { this.year = year; }
    
    public String getColor() { return color; }
    public void setColor(String color) { this.color = color; }
    
    public String getLicensePlate() { return licensePlate; }
    public void setLicensePlate(String licensePlate) { this.licensePlate = licensePlate; }
    
    public BigDecimal getPricePerDay() { return pricePerDay; }
    public void setPricePerDay(BigDecimal pricePerDay) { this.pricePerDay = pricePerDay; }
    
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    
    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }
    
    public Integer getSeats() { return seats; }
    public void setSeats(Integer seats) { this.seats = seats; }
    
    public String getFuelType() { return fuelType; }
    public void setFuelType(String fuelType) { this.fuelType = fuelType; }
    
    public String getTransmission() { return transmission; }
    public void setTransmission(String transmission) { this.transmission = transmission; }
    
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}
=======

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
>>>>>>> c7b20e3812e5add1651baa4d639b573578a1b157
