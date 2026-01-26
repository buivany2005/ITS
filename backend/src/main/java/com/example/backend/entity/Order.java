package com.example.backend.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "orders")
public class Order {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "vehicle_id", nullable = false)
    private Vehicle vehicle;
    
    @Column(name = "date_from", nullable = false)
    private LocalDate dateFrom;
    
    @Column(name = "date_to", nullable = false)
    private LocalDate dateTo;
    
    @Column(name = "total_days")
    private Integer totalDays;
    
    @Column(name = "price_per_day", nullable = false)
    private BigDecimal pricePerDay;
    
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;
    
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OrderStatus status = OrderStatus.PENDING;
    
    @Column(name = "pickup_location")
    private String pickupLocation;
    
    @Column(name = "return_location")
    private String returnLocation;
    
    @Column(columnDefinition = "TEXT")
    private String notes;
    
    @Column(name = "customer_name")
    private String customerName;
    
    @Column(name = "customer_phone")
    private String customerPhone;
    
    @Column(name = "customer_email")
    private String customerEmail;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    public enum OrderStatus {
        PENDING,      // Chờ xác nhận
        CONFIRMED,    // Đã xác nhận
        IN_PROGRESS,  // Đang thuê
        COMPLETED,    // Hoàn thành
        CANCELLED     // Đã hủy
    }
    
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        calculateTotalDays();
        calculateTotalPrice();
    }
    
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
        calculateTotalDays();
        calculateTotalPrice();
    }
    
    private void calculateTotalDays() {
        if (dateFrom != null && dateTo != null) {
            totalDays = (int) java.time.temporal.ChronoUnit.DAYS.between(dateFrom, dateTo);
            if (totalDays <= 0) totalDays = 1;
        }
    }
    
    private void calculateTotalPrice() {
        if (pricePerDay != null && totalDays != null) {
            totalPrice = pricePerDay.multiply(BigDecimal.valueOf(totalDays));
        }
    }
}
