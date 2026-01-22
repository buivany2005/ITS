package com.example.backend.controller;

import com.example.backend.dto.CartResponse;
import com.example.backend.entity.Cart;
import com.example.backend.entity.User;
import com.example.backend.entity.Vehicle;
import com.example.backend.repository.CartRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/cart")  // ✅ FIXED: Thêm /api prefix
@CrossOrigin(origins = "*")
public class CartController {
    
    @Autowired
    private CartRepository cartRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    /**
     * Get user's cart
     */
    @GetMapping
    public ResponseEntity<?> getCart(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }
            
            List<CartResponse> cartItems = cartRepository.findByUserId(userId)
                .stream()
                .map(CartResponse::fromEntity)
                .collect(Collectors.toList());
            
            return ResponseEntity.ok(cartItems);
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Add item to cart
     */
    @PostMapping
    public ResponseEntity<?> addToCart(
            @RequestHeader(value = "X-User-Id", required = false) Long userId,
            @RequestBody AddToCartRequest request) {
        try {
            if (userId == null) {
                userId = 1L; // Default for testing
            }
            
            // Validate user
            User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User không tồn tại"));
            
            // Validate vehicle
            Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Phương tiện không tồn tại"));
            
            // Check if already in cart
            if (cartRepository.existsByUserIdAndVehicleId(userId, request.getVehicleId())) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Phương tiện đã có trong giỏ hàng"));
            }
            
            // Create cart item
            Cart cart = new Cart();
            cart.setUser(user);
            cart.setVehicle(vehicle);
            cart.setDateFrom(request.getDateFrom());
            cart.setDateTo(request.getDateTo());
            
            Cart saved = cartRepository.save(cart);
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(CartResponse.fromEntity(saved));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Remove item from cart
     */
    @DeleteMapping("/{itemId}")
    public ResponseEntity<?> removeFromCart(
            @PathVariable Long itemId,
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            Cart cart = cartRepository.findById(itemId)
                .orElseThrow(() -> new RuntimeException("Cart item không tồn tại"));
            
            // Verify ownership
            if (!cart.getUser().getId().equals(userId)) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body(Map.of("error", "Không có quyền xóa item này"));
            }
            
            cartRepository.delete(cart);
            return ResponseEntity.ok(Map.of("message", "Đã xóa khỏi giỏ hàng"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    /**
     * Clear cart
     */
    @DeleteMapping
    public ResponseEntity<?> clearCart(
            @RequestHeader(value = "X-User-Id", required = false) Long userId) {
        try {
            if (userId == null) {
                userId = 1L;
            }
            
            cartRepository.deleteByUserId(userId);
            return ResponseEntity.ok(Map.of("message", "Đã xóa toàn bộ giỏ hàng"));
            
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                .body(Map.of("error", e.getMessage()));
        }
    }
    
    // Inner class for request
    static class AddToCartRequest {
        public Long vehicleId;
        public LocalDate dateFrom;
        public LocalDate dateTo;
        
        public Long getVehicleId() { return vehicleId; }
        public LocalDate getDateFrom() { return dateFrom; }
        public LocalDate getDateTo() { return dateTo; }
    }
}