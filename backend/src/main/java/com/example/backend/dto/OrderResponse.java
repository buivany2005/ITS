package com.example.backend.dto;

import com.example.backend.entity.Order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderResponse {
    private Long id;
    private Long userId;
    private String userName;
    private Long vehicleId;
    private String vehicleName;
    private String vehicleType;
    private String vehicleImageUrl;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private Integer totalDays;
    private BigDecimal pricePerDay;
    private BigDecimal totalPrice;
    private String status;
    private String pickupLocation;
    private String returnLocation;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String notes;
    private LocalDateTime createdAt;
    
    public static OrderResponse fromEntity(Order order) {
        OrderResponse response = new OrderResponse();
        response.setId(order.getId());
        response.setUserId(order.getUser().getId());
        response.setUserName(order.getUser().getFullName());
        response.setVehicleId(order.getVehicle().getId());
        response.setVehicleName(order.getVehicle().getName());
        response.setVehicleType(order.getVehicle().getVehicleType().name());
        response.setVehicleImageUrl(order.getVehicle().getImageUrl());
        response.setDateFrom(order.getDateFrom());
        response.setDateTo(order.getDateTo());
        response.setTotalDays(order.getTotalDays());
        response.setPricePerDay(order.getPricePerDay());
        response.setTotalPrice(order.getTotalPrice());
        response.setStatus(order.getStatus().name());
        response.setPickupLocation(order.getPickupLocation());
        response.setReturnLocation(order.getReturnLocation());
        response.setCustomerName(order.getCustomerName());
        response.setCustomerPhone(order.getCustomerPhone());
        response.setCustomerEmail(order.getCustomerEmail());
        response.setNotes(order.getNotes());
        response.setCreatedAt(order.getCreatedAt());
        return response;
    }
}
