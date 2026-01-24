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
import com.example.backend.util.ExcelExporter;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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
     * Get all vehicles for admin (with pagination)
     */
    @GetMapping("/vehicles")
    public ResponseEntity<?> getAllVehicles(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String category) {
        try {
            Map<String, Object> response = vehicleService.getVehiclesWithPagination(page, size, q, category);
            // Convert vehicles to VehicleResponse DTO
            List<VehicleResponse> vehicleResponses = ((List<Vehicle>) response.get("vehicles"))
                .stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
            response.put("vehicles", vehicleResponses);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Export vehicles to Excel
     */
    @GetMapping("/vehicles/export")
    public ResponseEntity<?> exportVehicles() {
        try {
            List<Vehicle> vehicles = vehicleService.getAllVehicles();
            List<VehicleResponse> responses = vehicles.stream()
                .map(VehicleResponse::fromEntity)
                .collect(Collectors.toList());
            
            byte[] excelData = ExcelExporter.exportVehiclesToExcel(responses);
            String filename = "danh-sach-xe-" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Không thể export file Excel: " + e.getMessage()));
        }
    }
    
    // ==================== ORDER MANAGEMENT ====================
    
    /**
     * Get all orders (with pagination and search)
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "12") int size,
            @RequestParam(required = false) String q,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String dateFrom,
            @RequestParam(required = false) String dateTo) {
        try {
            // Parse dates if provided
            java.time.LocalDate fromDate = null;
            java.time.LocalDate toDate = null;
            
            if (dateFrom != null && !dateFrom.isEmpty()) {
                fromDate = java.time.LocalDate.parse(dateFrom);
            }
            if (dateTo != null && !dateTo.isEmpty()) {
                toDate = java.time.LocalDate.parse(dateTo);
            }
            
            Map<String, Object> response = orderService.getOrdersWithPagination(
                page, size, q, status, fromDate, toDate);
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Export orders to Excel
     */
    @GetMapping("/orders/export")
    public ResponseEntity<?> exportOrders() {
        try {
            List<OrderResponse> orders = orderService.getAllOrders();
            byte[] excelData = ExcelExporter.exportOrdersToExcel(orders);
            String filename = "don-dat-xe-" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Không thể export file Excel: " + e.getMessage()));
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
     * Get statistics report
     */
    @GetMapping("/stats")
    public ResponseEntity<?> getStatistics() {
        try {
            Map<String, Object> stats = orderService.getStatisticsReport();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Export statistics to Excel
     */
    @GetMapping("/stats/export")
    public ResponseEntity<?> exportStatistics() {
        try {
            Map<String, Object> stats = orderService.getStatisticsReport();
            byte[] excelData = ExcelExporter.exportStatisticsReport(stats);
            String filename = "thong-ke-bao-cao-" + LocalDateTime.now().format(
                DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss")) + ".xlsx";
            
            return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_OCTET_STREAM_VALUE)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                .body(excelData);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Không thể export file Excel: " + e.getMessage()));
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
            OrderService orderService = this.orderService; // Use the injected service
            
            Map<String, Object> dashboard = Map.of(
                "vehicles", vehicleStats,
                "totalUsers", userService.getAllUsers().size()
            );
            
            return ResponseEntity.ok(dashboard);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
