package com.example.backend.repository;

import com.example.backend.entity.Order;
import com.example.backend.entity.Order.OrderStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
    
    // Find orders by user
    List<Order> findByUserIdOrderByCreatedAtDesc(Long userId);
    
    // Find orders by status
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    
    // Find orders by vehicle
    List<Order> findByVehicleId(Long vehicleId);
    
    // Check if vehicle is available for date range
    @Query("SELECT COUNT(o) FROM Order o WHERE o.vehicle.id = :vehicleId " +
           "AND o.status NOT IN ('CANCELLED', 'COMPLETED') " +
           "AND ((o.dateFrom <= :dateTo AND o.dateTo >= :dateFrom))")
    long countConflictingOrders(@Param("vehicleId") Long vehicleId, 
                                @Param("dateFrom") LocalDate dateFrom, 
                                @Param("dateTo") LocalDate dateTo);
    
    // Find orders by date range
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("startDate") java.time.LocalDateTime startDate);
    
    // Count orders by status
    long countByStatus(OrderStatus status);
    
    // Find user's orders by status
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
}
