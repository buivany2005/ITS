package com.example.backend.controller;

import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.repository.VehicleRepository;
import com.example.backend.repository.OrderRepository;
import com.example.backend.entity.Order;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.PageRequest;

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
    
    // --- HÀM GET ALL ĐÃ SỬA: PHÂN TRANG + LOGIC CŨ ---
    @GetMapping
    public ResponseEntity<Page<Map<String, Object>>> getAllVehicles(
            @RequestParam(required = false) String vehicleType,
            @RequestParam(defaultValue = "0") int page,  // Trang số mấy
            @RequestParam(defaultValue = "12") int size  // 12 xe/trang
    ) {
        Pageable pageable = PageRequest.of(page, size);
        Page<Vehicle> vehiclePage;

        // 1. Lấy dữ liệu thô từ DB (Theo Type hoặc All)
        if (vehicleType != null && !vehicleType.isEmpty() && !vehicleType.equals("all")) {
            try {
                VehicleType type = VehicleType.valueOf(vehicleType.toUpperCase());
                // Gọi hàm tìm kiếm có phân trang (được khai báo ở Repository bên dưới)
                vehiclePage = vehicleRepository.findByVehicleType(type, pageable);
            } catch (IllegalArgumentException e) {
                // Trả về trang rỗng nếu sai loại xe
                return ResponseEntity.ok(Page.empty());
            }
        } else {
            // Lấy tất cả có phân trang
            vehiclePage = vehicleRepository.findAll(pageable);
        }

        // 2. Map dữ liệu (GIỮ NGUYÊN LOGIC CŨ CỦA BẠN)
        // Dùng hàm .map() của Page để giữ thông tin phân trang (totalPages, totalElements...)
        Page<Map<String, Object>> resultPage = vehiclePage.map(vehicle -> {
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
            
            // Logic check available cũ của bạn
            boolean isAvailable = orderRepository.isVehicleAvailable(
                vehicle.getId(), 
                LocalDate.now(), 
                LocalDate.now().plusDays(1)
            );
            vehicleMap.put("available", isAvailable);
            
            return vehicleMap;
        });

        return ResponseEntity.ok(resultPage);
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

    // LƯU XE 
    @PostMapping
    public ResponseEntity<Vehicle> createVehicle(@RequestBody Vehicle vehicle) {
        return ResponseEntity.ok(vehicleRepository.save(vehicle));
    }
}