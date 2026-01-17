package com.example.backend.controller;

import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order.OrderStatus;
import com.example.backend.entity.User.UserRole;
import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.service.OrderService;
import com.example.backend.service.UserService;
import com.example.backend.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/admin")
@CrossOrigin(origins = "*")
public class AdminController {
    
    @Autowired
    private VehicleService vehicleService;
    
    @Autowired
    private OrderService orderService;
    
    @Autowired
    private UserService userService;
    
    // ==================== VEHICLE MANAGEMENT ====================
    
    /**
     * Create new vehicle
     */
    @PostMapping("/vehicles")
    public ResponseEntity<?> createVehicle(@RequestBody VehicleRequest request) {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(request.name);
            vehicle.setVehicleType(VehicleType.valueOf(request.type.toUpperCase()));
            vehicle.setBrand(request.brand);
            vehicle.setModel(request.model);
            vehicle.setYear(request.year);
            vehicle.setColor(request.color);
            vehicle.setLicensePlate(request.licensePlate);
            vehicle.setPricePerDay(request.pricePerDay);
            vehicle.setDescription(request.description);
            vehicle.setImageUrl(request.imageUrl);
            vehicle.setSeats(request.seats);
            vehicle.setFuelType(request.fuelType);
            vehicle.setTransmission(request.transmission);
            vehicle.setStatus(VehicleStatus.AVAILABLE);
            
            Vehicle saved = vehicleService.createVehicle(vehicle);
            return ResponseEntity.status(HttpStatus.CREATED).body(saved);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update vehicle
     */
    @PutMapping("/vehicles/{id}")
    public ResponseEntity<?> updateVehicle(
            @PathVariable Long id, 
            @RequestBody VehicleRequest request) {
        try {
            Vehicle vehicle = new Vehicle();
            vehicle.setName(request.name);
            vehicle.setVehicleType(VehicleType.valueOf(request.type.toUpperCase()));
            vehicle.setBrand(request.brand);
            vehicle.setModel(request.model);
            vehicle.setYear(request.year);
            vehicle.setColor(request.color);
            vehicle.setLicensePlate(request.licensePlate);
            vehicle.setPricePerDay(request.pricePerDay);
            vehicle.setDescription(request.description);
            vehicle.setImageUrl(request.imageUrl);
            vehicle.setSeats(request.seats);
            vehicle.setFuelType(request.fuelType);
            vehicle.setTransmission(request.transmission);
            if (request.status != null) {
                vehicle.setStatus(VehicleStatus.valueOf(request.status.toUpperCase()));
            }
            
            Vehicle updated = vehicleService.updateVehicle(id, vehicle);
            return ResponseEntity.ok(updated);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete vehicle
     */
    @DeleteMapping("/vehicles/{id}")
    public ResponseEntity<?> deleteVehicle(@PathVariable Long id) {
        try {
            vehicleService.deleteVehicle(id);
            return ResponseEntity.ok(Map.of("message", "Vehicle deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update vehicle status
     */
    @PatchMapping("/vehicles/{id}/status")
    public ResponseEntity<?> updateVehicleStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            VehicleStatus status = VehicleStatus.valueOf(request.get("status").toUpperCase());
            Vehicle vehicle = vehicleService.updateVehicleStatus(id, status);
            return ResponseEntity.ok(vehicle);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get vehicle statistics
     */
    @GetMapping("/vehicles/stats")
    public ResponseEntity<?> getVehicleStats() {
        VehicleService.VehicleStats stats = vehicleService.getVehicleStats();
        return ResponseEntity.ok(stats);
    }
    
    // ==================== ORDER MANAGEMENT ====================
    
    /**
     * Get all orders
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(@RequestParam(required = false) String status) {
        try {
            List<OrderResponse> orders;
            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getOrdersByStatus(orderStatus);
            } else {
                orders = orderService.getAllOrders();
            }
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update order status
     */
    @PatchMapping("/orders/{id}/status")
    public ResponseEntity<?> updateOrderStatus(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            OrderStatus status = OrderStatus.valueOf(request.get("status").toUpperCase());
            OrderResponse order = orderService.updateOrderStatus(id, status);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get order statistics
     */
    @GetMapping("/orders/stats")
    public ResponseEntity<?> getOrderStats() {
        OrderService.OrderStats stats = orderService.getOrderStats();
        return ResponseEntity.ok(stats);
    }
    
    // ==================== USER MANAGEMENT ====================
    
    /**
     * Get all users
     */
    @GetMapping("/users")
    public ResponseEntity<?> getAllUsers() {
        try {
            return ResponseEntity.ok(userService.getAllUsers());
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update user role
     */
    @PatchMapping("/users/{id}/role")
    public ResponseEntity<?> updateUserRole(
            @PathVariable Long id,
            @RequestBody Map<String, String> request) {
        try {
            UserRole role = UserRole.valueOf(request.get("role").toUpperCase());
            var user = userService.updateUserRole(id, role);
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Delete user
     */
    @DeleteMapping("/users/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable Long id) {
        try {
            userService.deleteUser(id);
            return ResponseEntity.ok(Map.of("message", "User deleted successfully"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== DASHBOARD ====================
    
    /**
     * Get dashboard statistics
     */
    @GetMapping("/dashboard")
    public ResponseEntity<?> getDashboardStats() {
        try {
            VehicleService.VehicleStats vehicleStats = vehicleService.getVehicleStats();
            OrderService.OrderStats orderStats = orderService.getOrderStats();
            
            Map<String, Object> dashboard = Map.of(
                "vehicles", vehicleStats,
                "orders", orderStats,
                "totalUsers", userService.getAllUsers().size()
            );
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== INNER CLASSES ====================
    
    static class VehicleRequest {
        public String name;
        public String type;
        public String brand;
        public String model;
        public Integer year;
        public String color;
        public String licensePlate;
        public BigDecimal pricePerDay;
        public String description;
        public String imageUrl;
        public Integer seats;
        public String fuelType;
        public String transmission;
        public String status;
    }
}
