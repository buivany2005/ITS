package com.example.backend.controller;

import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order.OrderStatus;
import com.example.backend.entity.User;
import com.example.backend.service.OrderService;
import com.example.backend.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/user")
@CrossOrigin(origins = "*")
public class UserController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private OrderService orderService;
    
    /**
     * Get user profile
     */
    @GetMapping("/profile")
    public ResponseEntity<?> getProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }
            
            User user = userService.getUserById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            
            // Create response without password
            ProfileResponse response = new ProfileResponse();
            response.setId(user.getId());
            response.setFullName(user.getFullName());
            response.setEmail(user.getEmail());
            response.setPhone(user.getPhone());
            response.setAddress(user.getAddress());
            response.setRole(user.getRole().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Update user profile
     */
    @PutMapping("/profile")
    public ResponseEntity<?> updateProfile(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody UpdateProfileRequest request) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            User userData = new User();
            userData.setFullName(request.getFullName());
            userData.setPhone(request.getPhone());
            userData.setAddress(request.getAddress());
            
            User updated = userService.updateProfile(userId, userData);
            
            ProfileResponse response = new ProfileResponse();
            response.setId(updated.getId());
            response.setFullName(updated.getFullName());
            response.setEmail(updated.getEmail());
            response.setPhone(updated.getPhone());
            response.setAddress(updated.getAddress());
            response.setRole(updated.getRole().toString());
            
            return ResponseEntity.ok(response);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Get user's orders
     */
    @GetMapping("/orders")
    public ResponseEntity<?> getUserOrders(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestParam(required = false) String status) {
        try {
            if (userId == null) {
                userId = 1L;
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
     * Cancel order
     */
    @PostMapping("/orders/{orderId}/cancel")
    public ResponseEntity<?> cancelOrder(
            @PathVariable Long orderId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            OrderResponse order = orderService.cancelOrder(orderId, userId);
            return ResponseEntity.ok(order);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Change password
     */
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody ChangePasswordRequest request) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            userService.changePassword(userId, request.getOldPassword(), request.getNewPassword());
            return ResponseEntity.ok(Map.of("message", "Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Inner classes
    static class ProfileResponse {
        private Long id;
        private String fullName;
        private String email;
        private String phone;
        private String address;
        private String role;
        
        // Getters and Setters
        public Long getId() { return id; }
        public void setId(Long id) { this.id = id; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
        public String getPhone() { return phone; }
        public void setPhone(String phone) { this.phone = phone; }
        public String getAddress() { return address; }
        public void setAddress(String address) { this.address = address; }
        public String getRole() { return role; }
        public void setRole(String role) { this.role = role; }
    }
    
    static class UpdateProfileRequest {
        private String fullName;
        private String phone;
        private String address;
        
        public String getFullName() { return fullName; }
        public String getPhone() { return phone; }
        public String getAddress() { return address; }
    }
    
    static class ChangePasswordRequest {
        private String oldPassword;
        private String newPassword;
        
        public String getOldPassword() { return oldPassword; }
        public String getNewPassword() { return newPassword; }
    }
}