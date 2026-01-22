package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.time.LocalDate;

@RestController
@RequestMapping("/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
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
    }
    
    @GetMapping("/{id}")
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
    }
    
    @GetMapping("/search")
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
    }
}
