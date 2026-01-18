package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    /**
     * Get all vehicles or filter by type/query
     * Supports: ?vehicleType=OTO or ?q=honda or ?category=OTO
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q) {
        
        try {
            List<Vehicle> vehicles;
            
            // Filter by type/category
            String typeParam = vehicleType != null ? vehicleType : category;
            if (typeParam != null && !typeParam.isEmpty() && !typeParam.equalsIgnoreCase("all")) {
                VehicleType type = VehicleType.valueOf(typeParam.toUpperCase());
                vehicles = vehicleRepository.findByVehicleType(type);
            } else {
                vehicles = vehicleRepository.findAll();
            }
            
            // Filter by search query
            if (q != null && !q.trim().isEmpty()) {
                String query = q.toLowerCase();
                vehicles = vehicles.stream()
                    .filter(v -> v.getName().toLowerCase().contains(query) ||
                               (v.getBrand() != null && v.getBrand().toLowerCase().contains(query)) ||
                               (v.getModel() != null && v.getModel().toLowerCase().contains(query)))
                    .collect(Collectors.toList());
            }
            
            // Only return available vehicles for public endpoint
            vehicles = vehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(vehicles);
            
        } catch (IllegalArgumentException e) {
            // Invalid vehicle type
            return ResponseEntity.ok(vehicleRepository.findAll());
        }
    }
    
    /**
     * Get vehicle by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    /**
     * Search vehicles (alternative endpoint)
     */
    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(@RequestParam String q) {
        List<Vehicle> vehicles = vehicleRepository.findByNameContainingIgnoreCase(q);
        return ResponseEntity.ok(vehicles);
    }
}