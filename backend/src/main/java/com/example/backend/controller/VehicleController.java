package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/vehicles")
@CrossOrigin(origins = "*")
public class VehicleController {
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @GetMapping
    public ResponseEntity<List<Vehicle>> getAllVehicles(
            @RequestParam(required = false) String vehicleType) {
        
        if (vehicleType != null && !vehicleType.isEmpty()) {
            try {
                VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
                return ResponseEntity.ok(vehicleRepository.findByVehicleType(type));
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest().build();
            }
        }
        
        return ResponseEntity.ok(vehicleRepository.findAll());
    }
    
    @GetMapping("/{id}")
    public ResponseEntity<Vehicle> getVehicleById(@PathVariable Long id) {
        return vehicleRepository.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Vehicle>> searchVehicles(@RequestParam String q) {
        return ResponseEntity.ok(vehicleRepository.findByNameContainingIgnoreCase(q));
    }
}
