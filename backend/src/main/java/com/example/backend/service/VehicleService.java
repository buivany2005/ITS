package com.example.backend.service;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
@Transactional
public class VehicleService {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    /**
     * Get all vehicles
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getAllVehicles() {
        return vehicleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
    }
    
    /**
     * Get vehicles with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getVehiclesWithPagination(int page, int size, String query, String category) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Vehicle> vehiclePage;
        
        if (query != null && !query.isEmpty()) {
            vehiclePage = vehicleRepository.findByNameContainingIgnoreCase(query, pageable);
        } else if (category != null && !category.isEmpty() && !category.equals("all")) {
            VehicleType type = VehicleType.valueOf(category.toUpperCase());
            vehiclePage = vehicleRepository.findByVehicleType(type, pageable);
        } else {
            vehiclePage = vehicleRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("vehicles", vehiclePage.getContent());
        response.put("currentPage", vehiclePage.getNumber());
        response.put("totalItems", vehiclePage.getTotalElements());
        response.put("totalPages", vehiclePage.getTotalPages());
        
        return response;
    }
    
    /**
     * Get vehicle by ID
     */
    @Transactional(readOnly = true)
    public Optional<Vehicle> getVehicleById(Long id) {
        return vehicleRepository.findById(id);
    }
    
    /**
     * Get vehicles by type
     */
    @Transactional(readOnly = true)
    public List<Vehicle> getVehiclesByType(VehicleType type) {
        return vehicleRepository.findByVehicleType(type);
    }
    
    /**
     * Search vehicles by name
     */
    @Transactional(readOnly = true)
    public List<Vehicle> searchVehicles(String query) {
        return vehicleRepository.findByNameContainingIgnoreCase(query);
    }
    
    /**
     * Create new vehicle (admin)
     */
    public Vehicle createVehicle(Vehicle vehicle) {
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * Update vehicle (admin)
     */
    public Vehicle updateVehicle(Long id, Vehicle vehicleData) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Phương tiện không tồn tại"));
        
        // Update fields
        if (vehicleData.getName() != null) vehicle.setName(vehicleData.getName());
        if (vehicleData.getVehicleType() != null) vehicle.setVehicleType(vehicleData.getVehicleType());
        if (vehicleData.getBrand() != null) vehicle.setBrand(vehicleData.getBrand());
        if (vehicleData.getModel() != null) vehicle.setModel(vehicleData.getModel());
        if (vehicleData.getYear() != null) vehicle.setYear(vehicleData.getYear());
        if (vehicleData.getLicensePlate() != null) vehicle.setLicensePlate(vehicleData.getLicensePlate());
        if (vehicleData.getColor() != null) vehicle.setColor(vehicleData.getColor());
        if (vehicleData.getSeats() != null) vehicle.setSeats(vehicleData.getSeats());
        if (vehicleData.getTransmission() != null) vehicle.setTransmission(vehicleData.getTransmission());
        if (vehicleData.getFuelType() != null) vehicle.setFuelType(vehicleData.getFuelType());
        if (vehicleData.getPricePerDay() != null) vehicle.setPricePerDay(vehicleData.getPricePerDay());
        if (vehicleData.getDescription() != null) vehicle.setDescription(vehicleData.getDescription());
        if (vehicleData.getLocation() != null) vehicle.setLocation(vehicleData.getLocation());
        if (vehicleData.getImageUrl() != null) vehicle.setImageUrl(vehicleData.getImageUrl());
        if (vehicleData.getStatus() != null) vehicle.setStatus(vehicleData.getStatus());
        
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * Delete vehicle (admin)
     */
    public void deleteVehicle(Long id) {
        if (!vehicleRepository.existsById(id)) {
            throw new RuntimeException("Phương tiện không tồn tại");
        }
        vehicleRepository.deleteById(id);
    }
    
    /**
     * Update vehicle status
     */
    public Vehicle updateVehicleStatus(Long id, VehicleStatus status) {
        Vehicle vehicle = vehicleRepository.findById(id)
            .orElseThrow(() -> new RuntimeException("Phương tiện không tồn tại"));
        vehicle.setStatus(status);
        return vehicleRepository.save(vehicle);
    }
    
    /**
     * Get vehicle statistics
     */
    @Transactional(readOnly = true)
    public VehicleStats getVehicleStats() {
        VehicleStats stats = new VehicleStats();
        stats.setTotalVehicles(vehicleRepository.count());
        stats.setCarCount(vehicleRepository.findByVehicleType(VehicleType.OTO).size());
        stats.setMotorcycleCount(vehicleRepository.findByVehicleType(VehicleType.XEMAY).size());
        stats.setBicycleCount(vehicleRepository.findByVehicleType(VehicleType.XEDAP).size());
        return stats;
    }
    
    // Inner class for stats
    public static class VehicleStats {
        private long totalVehicles;
        private long carCount;
        private long motorcycleCount;
        private long bicycleCount;
        
        // Getters and Setters
        public long getTotalVehicles() { return totalVehicles; }
        public void setTotalVehicles(long totalVehicles) { this.totalVehicles = totalVehicles; }
        public long getCarCount() { return carCount; }
        public void setCarCount(long carCount) { this.carCount = carCount; }
        public long getMotorcycleCount() { return motorcycleCount; }
        public void setMotorcycleCount(long motorcycleCount) { this.motorcycleCount = motorcycleCount; }
        public long getBicycleCount() { return bicycleCount; }
        public void setBicycleCount(long bicycleCount) { this.bicycleCount = bicycleCount; }
    }
}
