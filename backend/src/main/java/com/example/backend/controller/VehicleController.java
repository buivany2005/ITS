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
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    /**
     * Get all vehicles or filter
     * Supports: ?vehicleType=OTO or ?q=honda or ?category=OTO
     */
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) String q) {
        
        try {
            System.out.println("📡 GET /api/vehicles - vehicleType=" + vehicleType + ", category=" + category + ", q=" + q);
            
            List<Vehicle> vehicles;
            
            // Get base list
            String typeParam = vehicleType != null ? vehicleType : category;
            
            if (typeParam != null && !typeParam.isEmpty() && !typeParam.equalsIgnoreCase("all")) {
                try {
                    VehicleType type = VehicleType.valueOf(typeParam.toUpperCase());
                    vehicles = vehicleRepository.findByVehicleType(type);
                    System.out.println("✅ Found " + vehicles.size() + " vehicles of type " + type);
                } catch (IllegalArgumentException e) {
                    System.out.println("⚠️ Invalid vehicle type: " + typeParam);
                    vehicles = vehicleRepository.findAll();
                }
            } else {
                vehicles = vehicleRepository.findAll();
                System.out.println("✅ Found " + vehicles.size() + " total vehicles");
            }
            
            // Filter by search query
            if (q != null && !q.trim().isEmpty()) {
                String query = q.toLowerCase();
                vehicles = vehicles.stream()
                    .filter(v -> 
                        v.getName().toLowerCase().contains(query) ||
                        (v.getBrand() != null && v.getBrand().toLowerCase().contains(query)) ||
                        (v.getModel() != null && v.getModel().toLowerCase().contains(query))
                    )
                    .collect(Collectors.toList());
                System.out.println("✅ After search filter: " + vehicles.size() + " vehicles");
            }
            
            // Only return AVAILABLE vehicles for public
            vehicles = vehicles.stream()
                .filter(v -> v.getStatus() == VehicleStatus.AVAILABLE)
                .collect(Collectors.toList());
            
            System.out.println("✅ Returning " + vehicles.size() + " available vehicles");
            return ResponseEntity.ok(vehicles);
            
        } catch (Exception e) {
            System.out.println("❌ Error in getAllVehicles: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.ok(vehicleRepository.findAll());
        }
    }
    
    /**
     * Get vehicle by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        System.out.println("📡 GET /api/vehicles/" + id);
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    System.out.println("✅ Found vehicle: " + vehicle.getName());
                    return ResponseEntity.ok(vehicle);
                })
                .orElseGet(() -> {
                    System.out.println("❌ Vehicle not found: " + id);
                    return ResponseEntity.notFound().build();
                });
    }
    
    /**
     * Search vehicles
     */
    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(@RequestParam String q) {
        System.out.println("📡 GET /api/vehicles/search?q=" + q);
        List<Vehicle> vehicles = vehicleRepository.findByNameContainingIgnoreCase(q);
        System.out.println("✅ Search found " + vehicles.size() + " vehicles");
        return ResponseEntity.ok(vehicles);
    }
}