package com.example.backend.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderRequest {
    private Long vehicleId;
    private LocalDate dateFrom;
    private LocalDate dateTo;
    private String pickupLocation;
    private String returnLocation;
    private String customerName;
    private String customerPhone;
    private String customerEmail;
    private String notes;
}
