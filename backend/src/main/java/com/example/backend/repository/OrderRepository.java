package com.example.backend.repository;

import com.example.backend.entity.Order;
import com.example.backend.entity.Order.OrderStatus;
<<<<<<< HEAD
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
<<<<<<< HEAD
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
>>>>>>> c7b20e3812e5add1651baa4d639b573578a1b157
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
    
    // Find orders by user and status
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
    
    // Find orders by status
    List<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status);
    
<<<<<<< HEAD
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
    
=======
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
    // Find orders by vehicle
    List<Order> findByVehicleId(Long vehicleId);
    
    // Check if vehicle is available for date range
    @Query("SELECT COUNT(o) FROM Order o WHERE o.vehicle.id = :vehicleId " +
<<<<<<< HEAD
           "AND o.status != com.example.backend.entity.Order$OrderStatus.CANCELLED " +
           "AND o.status != com.example.backend.entity.Order$OrderStatus.COMPLETED " +
=======
           "AND o.status NOT IN ('CANCELLED', 'COMPLETED') " +
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
           "AND ((o.dateFrom <= :dateTo AND o.dateTo >= :dateFrom))")
    long countConflictingOrders(@Param("vehicleId") Long vehicleId, 
                                @Param("dateFrom") LocalDate dateFrom, 
                                @Param("dateTo") LocalDate dateTo);
    
<<<<<<< HEAD
    // Check if vehicle is available (returns true if available, false if booked)
    default boolean isVehicleAvailable(Long vehicleId, LocalDate dateFrom, LocalDate dateTo) {
        return countConflictingOrders(vehicleId, dateFrom, dateTo) == 0;
    }
    
=======
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
    // Find orders by date range
    @Query("SELECT o FROM Order o WHERE o.createdAt >= :startDate ORDER BY o.createdAt DESC")
    List<Order> findRecentOrders(@Param("startDate") java.time.LocalDateTime startDate);
    
    // Count orders by status
    long countByStatus(OrderStatus status);
    
    // Find orders by status with pagination
    Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);

    // Find orders by date range (rental date within given window) with pagination
    Page<Order> findByDateFromGreaterThanEqualAndDateToLessThanEqualOrderByCreatedAtDesc(java.time.LocalDate dateFrom, java.time.LocalDate dateTo, Pageable pageable);

    // Find orders by status and date range with pagination
    Page<Order> findByStatusAndDateFromGreaterThanEqualAndDateToLessThanEqualOrderByCreatedAtDesc(OrderStatus status, java.time.LocalDate dateFrom, java.time.LocalDate dateTo, Pageable pageable);
    
    // Find user's orders by status
<<<<<<< HEAD
    Page<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status, Pageable pageable);
=======
    List<Order> findByUserIdAndStatusOrderByCreatedAtDesc(Long userId, OrderStatus status);
<<<<<<< HEAD
    
    // Search orders by vehicle name
    @Query("SELECT o FROM Order o WHERE LOWER(o.vehicle.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY o.createdAt DESC")
    List<Order> searchByVehicleName(@Param("query") String query);
    
    @Query("SELECT o FROM Order o WHERE LOWER(o.vehicle.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY o.createdAt DESC")
    Page<Order> searchByVehicleName(@Param("query") String query, Pageable pageable);
    
    // Search orders by vehicle name and date range
    @Query("SELECT o FROM Order o WHERE LOWER(o.vehicle.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "AND o.dateFrom >= :dateFrom AND o.dateTo <= :dateTo ORDER BY o.createdAt DESC")
    List<Order> searchByVehicleNameAndDateRange(@Param("query") String query, 
                                                @Param("dateFrom") LocalDate dateFrom, 
                                                @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT o FROM Order o WHERE LOWER(o.vehicle.name) LIKE LOWER(CONCAT('%', :query, '%')) " +
           "AND o.dateFrom >= :dateFrom AND o.dateTo <= :dateTo ORDER BY o.createdAt DESC")
    Page<Order> searchByVehicleNameAndDateRange(@Param("query") String query, 
                                                @Param("dateFrom") LocalDate dateFrom, 
                                                @Param("dateTo") LocalDate dateTo,
                                                Pageable pageable);
    
    // Find orders by date range
    @Query("SELECT o FROM Order o WHERE o.dateFrom >= :dateFrom AND o.dateTo <= :dateTo ORDER BY o.createdAt DESC")
    List<Order> findByDateRange(@Param("dateFrom") LocalDate dateFrom, 
                                @Param("dateTo") LocalDate dateTo);
    
    @Query("SELECT o FROM Order o WHERE o.dateFrom >= :dateFrom AND o.dateTo <= :dateTo ORDER BY o.createdAt DESC")
    Page<Order> findByDateRange(@Param("dateFrom") LocalDate dateFrom, 
                                @Param("dateTo") LocalDate dateTo,
                                Pageable pageable);
=======
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
>>>>>>> c7b20e3812e5add1651baa4d639b573578a1b157
}
