package com.example.backend.dto;

import com.example.backend.entity.Cart;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {
    private Long id;
    private Long vehicleId;
    private String vehicleName;
    private String vehicleType;
    private String vehicleImageUrl;
    private BigDecimal pricePerDay;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    
    public static CartResponse fromEntity(Cart cart) {
        CartResponse response = new CartResponse();
        response.setId(cart.getId());
        response.setVehicleId(cart.getVehicle().getId());
        response.setVehicleName(cart.getVehicle().getName());
        response.setVehicleType(cart.getVehicle().getVehicleType().name());
        response.setVehicleImageUrl(cart.getVehicle().getImageUrl());
        response.setPricePerDay(cart.getVehicle().getPricePerDay());
        response.setDateFrom(cart.getDateFrom());
        response.setDateTo(cart.getDateTo());
        return response;
    }
}