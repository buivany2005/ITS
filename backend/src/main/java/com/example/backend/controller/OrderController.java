package com.example.backend.controller;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order.OrderStatus;
import com.example.backend.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "*")
public class OrderController {
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Create new order
     */
    @PostMapping
    public ResponseEntity<?> createOrder(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody OrderRequest request) {
        try {
            System.out.println("Received userId from header: " + userId);
            // For development, use a default user if not provided
            if (userId == null) {
                userId = 1L; // Default user for testing
            }
            System.out.println("Using userId: " + userId);
            
            OrderResponse order = orderService.createOrder(userId, request);
            return ResponseEntity.status(HttpStatus.CREATED).body(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get order by ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        try {
            OrderResponse order = orderService.getOrderById(id);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * Get user's orders
     */
    @GetMapping("/my-orders")
    public ResponseEntity<?> getMyOrders(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String status) {
        try {
            if (userId == null) {
                userId = 1L; // Default user for testing
            }
            
            List<OrderResponse> orders;
            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getUserOrdersByStatus(userId, orderStatus);
            } else {
                orders = orderService.getUserOrders(userId);
            }
            
            return ResponseEntity.ok(orders);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get all orders (Admin only)
     */
    @GetMapping("/all")
    public ResponseEntity<?> getAllOrders(
            @RequestParam(required = false) String status) {
        try {
            List<OrderResponse> orders;
            if (status != null && !status.isEmpty()) {
                OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
                orders = orderService.getAllOrdersByStatus(orderStatus);
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
     * Check vehicle availability
     */
    @GetMapping("/check-availability")
    public ResponseEntity<?> checkAvailability(
            @RequestParam Long vehicleId,
            @RequestParam String dateFrom,
            @RequestParam String dateTo) {
        try {
            java.time.LocalDate from = java.time.LocalDate.parse(dateFrom);
            java.time.LocalDate to = java.time.LocalDate.parse(dateTo);
            
            boolean available = orderService.isVehicleAvailable(vehicleId, from, to);
            return ResponseEntity.ok(Map.of("available", available));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
}
