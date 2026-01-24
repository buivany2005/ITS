package com.example.backend.repository;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    
    Page<Vehicle> findByVehicleType(VehicleType vehicleType, Pageable pageable);
    
    List<Vehicle> findByNameContainingIgnoreCase(String name);
    
    Page<Vehicle> findByNameContainingIgnoreCase(String name, Pageable pageable);
    
    long countByStatus(VehicleStatus status);
}
