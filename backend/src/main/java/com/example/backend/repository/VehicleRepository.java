package com.example.backend.repository;

import com.example.backend.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    // Các hàm cũ (Giữ nguyên để tránh lỗi chỗ khác)
    List<Vehicle> findByNameContainingIgnoreCase(String name);
    List<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType);

    // --- THÊM DÒNG NÀY ĐỂ SỬA LỖI ---
    // Hàm này cho phép tìm theo loại xe VÀ phân trang
    Page<Vehicle> findByVehicleType(Vehicle.VehicleType vehicleType, Pageable pageable);
}