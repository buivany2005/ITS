package com.example.backend.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

/**
 * DTO for creating/updating vehicles
 */
public class VehicleRequest {
    
    @NotBlank(message = "Tên xe không được để trống")
    @Size(min = 3, max = 100, message = "Tên xe phải từ 3 đến 100 ký tự")
    public String name;
    
    @NotBlank(message = "Loại xe không được để trống")
    public String type;
    
    @NotBlank(message = "Hãng xe không được để trống")
    @Size(min = 2, max = 50, message = "Hãng xe phải từ 2 đến 50 ký tự")
    public String brand;
    
    @NotBlank(message = "Model không được để trống")
    @Size(min = 1, max = 50, message = "Model phải từ 1 đến 50 ký tự")
    public String model;
    
    @NotNull(message = "Năm sản xuất không được để trống")
    @Min(value = 2000, message = "Năm sản xuất phải từ 2000 trở lên")
    @Max(value = 2030, message = "Năm sản xuất không được vượt quá 2030")
    public Integer year;
    
    @NotBlank(message = "Màu sắc không được để trống")
    public String color;
    
    @NotBlank(message = "Biển số xe không được để trống")
    @Pattern(regexp = "^[0-9]{2,3}[A-Z]-\\d{4,6}$", message = "Biển số xe không hợp lệ (ví dụ: 79A-27289)")
    public String licensePlate;
    
    @NotNull(message = "Giá thuê/ngày không được để trống")
    @DecimalMin(value = "1000", message = "Giá thuê phải ≥ 1000 VNĐ")
    @DecimalMax(value = "1000000", message = "Giá thuê không được vượt quá 1.000.000 VNĐ")
    public BigDecimal pricePerDay;
    
    @Size(max = 500, message = "Mô tả không được vượt quá 500 ký tự")
    public String description = "";
    
    @Size(max = 2048, message = "URL hình ảnh không được vượt quá 2048 ký tự")
    public String imageUrl = "";
    
    @NotNull(message = "Số chỗ ngồi không được để trống")
    @Min(value = 1, message = "Số chỗ ngồi phải ≥ 1")
    @Max(value = 50, message = "Số chỗ ngồi không được vượt quá 50")
    public Integer seats = 1;
    
    @Size(max = 50, message = "Loại nhiên liệu không được vượt quá 50 ký tự")
    public String fuelType = "";
    
    @Size(max = 50, message = "Truyền động không được vượt quá 50 ký tự")
    public String transmission = "";
    
    public String status = "AVAILABLE";
    
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
