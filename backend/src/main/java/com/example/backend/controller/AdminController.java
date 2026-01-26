package com.example.backend.controller;

import com.example.backend.dto.OrderResponse;
import com.example.backend.dto.VehicleRequest;
import com.example.backend.dto.VehicleResponse;
import com.example.backend.entity.Order.OrderStatus;
import com.example.backend.entity.User.UserRole;
import com.example.backend.entity.Vehicle;
import com.example.backend.entity.Vehicle.VehicleStatus;
import com.example.backend.entity.Vehicle.VehicleType;
import com.example.backend.service.OrderService;
import com.example.backend.service.UserService;
import com.example.backend.service.VehicleService;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.ResponseEntity;
import org.springframework.beans.factory.annotation.Autowired;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.multipart.MultipartFile;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
    public ResponseEntity<?> createVehicle(@Valid @RequestBody VehicleRequest request) {
        try {
            // Validate vehicle type
            VehicleType vehicleType;
            try {
                vehicleType = VehicleType.valueOf(request.type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Loại xe không hợp lệ. Hãy chọn: OTO, XEMAY, XEDAP"));
            }
            
            Vehicle vehicle = new Vehicle();
            vehicle.setName(request.name);
            vehicle.setVehicleType(vehicleType);
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
            return ResponseEntity.status(HttpStatus.CREATED).body(VehicleResponse.fromEntity(saved));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Lỗi khi thêm phương tiện: " + e.getMessage()));
        }
    }
    
    /**
     * Update vehicle
     */
    @PutMapping("/vehicles/{id}")
    public ResponseEntity<?> updateVehicle(
            @PathVariable Long id, 
            @Valid @RequestBody VehicleRequest request) {
        try {
            // Validate vehicle type
            VehicleType vehicleType;
            try {
                vehicleType = VehicleType.valueOf(request.type.toUpperCase());
            } catch (IllegalArgumentException e) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Loại xe không hợp lệ. Hãy chọn: OTO, XEMAY, XEDAP"));
            }
            
            Vehicle vehicle = new Vehicle();
            vehicle.setName(request.name);
            vehicle.setVehicleType(vehicleType);
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
            return ResponseEntity.ok(VehicleResponse.fromEntity(updated));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Lỗi khi cập nhật phương tiện: " + e.getMessage()));
        }
    }
    
    /**
     * Get vehicle by ID (for edit)
     */
    @GetMapping("/vehicles/{id}")
    public ResponseEntity<?> getVehicleById(@PathVariable Long id) {
        try {
            Vehicle vehicle = vehicleService.getVehicleById(id)
                .orElseThrow(() -> new RuntimeException("Phương tiện không tồn tại"));
            VehicleResponse response = VehicleResponse.fromEntity(vehicle);
            return ResponseEntity.ok(response);
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
    
    /**
     * Get all vehicles for admin
     */
    @GetMapping("/vehicles")
    public ResponseEntity<?> getAllVehicles(@RequestParam(required = false) String q,
                                           @RequestParam(required = false) String category) {
        try {
            List<Vehicle> vehicles;
            if (q != null && !q.isEmpty()) {
                vehicles = vehicleService.searchVehicles(q);
            } else if (category != null && !category.isEmpty() && !category.equals("all")) {
                VehicleType type = VehicleType.valueOf(category.toUpperCase());
                vehicles = vehicleService.getVehiclesByType(type);
            } else {
                vehicles = vehicleService.getAllVehicles();
            }
            
            // Convert to VehicleResponse DTO
            List<VehicleResponse> response = vehicles.stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // ==================== ORDER MANAGEMENT ====================
    
    /**
     * Get all orders
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "12") int size,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        try {
            Pageable pageable = PageRequest.of(page, size);
            Page<OrderResponse> ordersPage;
            java.time.LocalDate from = null;
            java.time.LocalDate to = null;
            if (dateFrom != null && !dateFrom.isEmpty() && dateTo != null && !dateTo.isEmpty()) {
                from = java.time.LocalDate.parse(dateFrom);
                to = java.time.LocalDate.parse(dateTo);
            }

            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                ordersPage = orderService.getAllOrdersByStatus(orderStatus, pageable, from, to);
            } else {
                ordersPage = orderService.getAllOrders(pageable, from, to);
            }
            Map<String, Object> response = Map.of(
                "content", ordersPage.getContent(),
                "totalElements", ordersPage.getTotalElements(),
                "totalPages", ordersPage.getTotalPages(),
                "currentPage", page,
                "size", size
            );
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get order by ID (for viewing details)
     */
    @GetMapping("/orders/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
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
    
    /**
     * Export orders to Excel
     */
    @GetMapping("/orders/export")
    public ResponseEntity<byte[]> exportOrders() {
        try {
            byte[] xlsx = orderService.exportOrdersToExcel();
            return ResponseEntity.ok()
                .header("Content-Type", "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                .header("Content-Disposition", "attachment; filename=orders.xlsx")
                .body(xlsx);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
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
    
    /**
     * Upload file
     */
    @PostMapping("/upload")
    public ResponseEntity<?> uploadFile(@RequestParam("file") MultipartFile file) {
        try {
            // Validate file
            if (file.isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File không được rỗng"));
            }
            
            // Validate file type
            String contentType = file.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Chỉ chấp nhận file hình ảnh"));
            }
            
            // Validate file size (max 5MB)
            if (file.getSize() > 5 * 1024 * 1024) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "File không được vượt quá 5MB"));
            }
            
            // Generate unique filename
            String originalFilename = file.getOriginalFilename();
            String extension = originalFilename != null ? 
                originalFilename.substring(originalFilename.lastIndexOf(".")) : ".jpg";
            String filename = System.currentTimeMillis() + "_" + 
                java.util.UUID.randomUUID().toString().substring(0, 8) + extension;
            
            // Save file to static/images directory
            java.nio.file.Path uploadPath = java.nio.file.Paths.get("src/main/resources/static/images");
            if (!java.nio.file.Files.exists(uploadPath)) {
                java.nio.file.Files.createDirectories(uploadPath);
            }
            
            java.nio.file.Path filePath = uploadPath.resolve(filename);
            java.nio.file.Files.copy(file.getInputStream(), filePath, 
                java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            
            // Return URL
            String url = "/images/" + filename;
            return ResponseEntity.ok(Map.of("url", url));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", "Upload thất bại: " + e.getMessage()));
        }
    }
}
