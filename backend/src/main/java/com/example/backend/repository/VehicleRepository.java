package com.example.backend.repository;

import com.example.backend.entity.Vehicle;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {

    // Tìm vehicles theo status
    Page<Vehicle> findByStatus(Vehicle.VehicleStatus status, Pageable pageable);
    
    List<Vehicle> findByStatus(Vehicle.VehicleStatus status);

    // Đếm vehicles theo status
    Long countByStatus(Vehicle.VehicleStatus status);

    // Tìm kiếm vehicles theo từ khóa (tên, brand, model)
    @Query("SELECT v FROM Vehicle v WHERE " +
           "LOWER(v.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.brand) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
           "LOWER(v.model) LIKE LOWER(CONCAT('%', :keyword, '%'))")
    Page<Vehicle> searchVehicles(@Param("keyword") String keyword, Pageable pageable);

    // Tìm vehicles có sẵn trong khoảng thời gian
    @Query("SELECT v FROM Vehicle v WHERE v.status = 'AVAILABLE' AND " +
           "v.id NOT IN (" +
           "    SELECT o.vehicle.id FROM Order o WHERE " +
           "    o.status NOT IN ('CANCELLED', 'COMPLETED') AND " +
           "    ((o.startDate <= :endDate AND o.endDate >= :startDate))" +
           ")")
    List<Vehicle> findAvailableVehiclesByDateRange(
        @Param("startDate") LocalDate startDate,
        @Param("endDate") LocalDate endDate
    );

    // Tìm vehicle theo license plate
    Vehicle findByLicensePlate(String licensePlate);

    // Kiểm tra license plate đã tồn tại
    boolean existsByLicensePlate(String licensePlate);
}