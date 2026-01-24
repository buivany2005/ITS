package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
<<<<<<< HEAD
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;
=======
import java.util.stream.Collectors;
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
<<<<<<< HEAD
    @Autowired
    private OrderRepository orderRepository;
    
    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllVehicles(
            @RequestParam(required = false) String vehicleType) {
        
        List<Vehicle> vehicles;
        if (vehicleType != null && !vehicleType.isEmpty()) {
            try {
                VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
                vehicles = vehicleRepository.findByVehicleType(type);
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        } else {
            vehicles = vehicleRepository.findAll();
        }
        
        // Add availability info to each vehicle
        List<Map<String, Object>> vehiclesWithAvailability = vehicles.stream()
            .map(vehicle -> {
                Map<String, Object> vehicleMap = new HashMap<>();
                vehicleMap.put("id", vehicle.getId());
                vehicleMap.put("name", vehicle.getName());
                vehicleMap.put("vehicleType", vehicle.getVehicleType());
                vehicleMap.put("brand", vehicle.getBrand());
                vehicleMap.put("model", vehicle.getModel());
                vehicleMap.put("year", vehicle.getYear());
                vehicleMap.put("licensePlate", vehicle.getLicensePlate());
                vehicleMap.put("color", vehicle.getColor());
                vehicleMap.put("seats", vehicle.getSeats());
                vehicleMap.put("transmission", vehicle.getTransmission());
                vehicleMap.put("fuelType", vehicle.getFuelType());
                vehicleMap.put("pricePerDay", vehicle.getPricePerDay());
                vehicleMap.put("description", vehicle.getDescription());
                vehicleMap.put("location", vehicle.getLocation());
                vehicleMap.put("imageUrl", vehicle.getImageUrl());
                vehicleMap.put("status", vehicle.getStatus());
                
                // Check if vehicle is currently booked (has active orders)
                boolean isAvailable = orderRepository.isVehicleAvailable(
                    vehicle.getId(), 
                    LocalDate.now(), 
                    LocalDate.now().plusDays(1)
                );
                vehicleMap.put("available", isAvailable);
                
                return vehicleMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(vehiclesWithAvailability);
=======
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
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
    }
    
    /**
     * Get vehicle by ID
     */
    @GetMapping("/{id}")
<<<<<<< HEAD
    public ResponseEntity<Map<String, Object>> getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(vehicle -> {
                    Map<String, Object> vehicleMap = new HashMap<>();
                    vehicleMap.put("id", vehicle.getId());
                    vehicleMap.put("name", vehicle.getName());
                    vehicleMap.put("vehicleType", vehicle.getVehicleType());
                    vehicleMap.put("brand", vehicle.getBrand());
                    vehicleMap.put("model", vehicle.getModel());
                    vehicleMap.put("year", vehicle.getYear());
                    vehicleMap.put("licensePlate", vehicle.getLicensePlate());
                    vehicleMap.put("color", vehicle.getColor());
                    vehicleMap.put("seats", vehicle.getSeats());
                    vehicleMap.put("transmission", vehicle.getTransmission());
                    vehicleMap.put("fuelType", vehicle.getFuelType());
                    vehicleMap.put("pricePerDay", vehicle.getPricePerDay());
                    vehicleMap.put("description", vehicle.getDescription());
                    vehicleMap.put("location", vehicle.getLocation());
                    vehicleMap.put("imageUrl", vehicle.getImageUrl());
                    vehicleMap.put("status", vehicle.getStatus());
                    
                    // Check availability
                    boolean isAvailable = orderRepository.isVehicleAvailable(
                        vehicle.getId(), 
                        LocalDate.now(), 
                        LocalDate.now().plusDays(1)
                    );
                    vehicleMap.put("available", isAvailable);
                    
                    return ResponseEntity.ok(vehicleMap);
                })
                .orElse(ResponseEntity.notFound().build());
=======
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
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
    }
    
    /**
     * Search vehicles
     */
    @GetMapping("/search")
<<<<<<< HEAD
    public ResponseEntity<List<Map<String, Object>>> searchVehicles(@RequestParam String q) {
        List<Vehicle> vehicles = vehicleRepository.findByNameContainingIgnoreCase(q);
        
        List<Map<String, Object>> vehiclesWithAvailability = vehicles.stream()
            .map(vehicle -> {
                Map<String, Object> vehicleMap = new HashMap<>();
                vehicleMap.put("id", vehicle.getId());
                vehicleMap.put("name", vehicle.getName());
                vehicleMap.put("vehicleType", vehicle.getVehicleType());
                vehicleMap.put("brand", vehicle.getBrand());
                vehicleMap.put("pricePerDay", vehicle.getPricePerDay());
                vehicleMap.put("location", vehicle.getLocation());
                vehicleMap.put("imageUrl", vehicle.getImageUrl());
                
                boolean isAvailable = orderRepository.isVehicleAvailable(
                    vehicle.getId(), 
                    LocalDate.now(), 
                    LocalDate.now().plusDays(1)
                );
                vehicleMap.put("available", isAvailable);
                
                return vehicleMap;
            })
            .collect(Collectors.toList());
        
        return ResponseEntity.ok(vehiclesWithAvailability);
=======
    public ResponseEntity<List<Vehicle>> searchVehicles(@RequestParam String q) {
        System.out.println("📡 GET /api/vehicles/search?q=" + q);
        List<Vehicle> vehicles = vehicleRepository.findByNameContainingIgnoreCase(q);
        System.out.println("✅ Search found " + vehicles.size() + " vehicles");
        return ResponseEntity.ok(vehicles);
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
    }
}