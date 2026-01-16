package com.example.backend.repository;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VehicleRepository extends JpaRepository<Vehicle, Long> {
    
    List<Vehicle> findByVehicleType(VehicleType vehicleType);
    
    List<Vehicle> findByNameContainingIgnoreCase(String name);
}
