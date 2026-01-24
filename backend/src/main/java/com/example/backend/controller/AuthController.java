package com.example.backend.controller;

import com.example.backend.dto.LoginRequest;
import com.example.backend.dto.LoginResponse;
import com.example.backend.dto.RegisterRequest;
import com.example.backend.entity.User;
import com.example.backend.entity.User.UserRole;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*")
public class AuthController {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Login endpoint
     */
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            System.out.println("🔐 Login attempt: " + request.getEmail());
            
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            
            if (userOpt.isEmpty()) {
                System.out.println("❌ User not found: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email không tồn tại"));
            }
            
            User user = userOpt.get();
            
            if (!user.getPassword().equals(request.getPassword())) {
                System.out.println("❌ Wrong password for: " + request.getEmail());
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Mật khẩu không đúng"));
            }
            
            System.out.println("✅ Login successful: " + user.getFullName());
            LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                "Đăng nhập thành công"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            System.out.println("❌ Login error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Lỗi server: " + e.getMessage()));
        }
    }
    
    /**
     * Register endpoint
     */
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
            System.out.println("📝 Register attempt: " + request.getEmail());
            
            // Validate input
            if (request.getFullName() == null || request.getFullName().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Họ tên không được để trống"));
            }
            
            if (request.getEmail() == null || request.getEmail().trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email không được để trống"));
            }
            
            if (request.getPassword() == null || request.getPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Mật khẩu không được để trống"));
            }
            
            if (!request.getPassword().equals(request.getConfirmPassword())) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Mật khẩu xác nhận không khớp"));
            }
            
            // Check if email already exists
            if (userRepository.existsByEmail(request.getEmail())) {
                System.out.println("❌ Email already exists: " + request.getEmail());
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email đã được đăng ký"));
            }
            
            // Create new user
            User newUser = new User();
            newUser.setFullName(request.getFullName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword());
            newUser.setPhone(request.getPhone());
            newUser.setAddress(request.getAddress());
            newUser.setRole(UserRole.USER);
            
            User savedUser = userRepository.save(newUser);
            System.out.println("✅ User registered: " + savedUser.getFullName());
            
            // Return success response
            LoginResponse response = new LoginResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                savedUser.getFullName(),
                savedUser.getRole().toString(),
                "Đăng ký thành công"
            );
            
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
            
        } catch (Exception e) {
            System.out.println("❌ Register error: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Lỗi server: " + e.getMessage()));
        }
    }
    
    /**
     * Check auth endpoint
     */
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        System.out.println("✅ Auth check endpoint hit");
        return ResponseEntity.ok(new MessageResponse("API auth hoạt động"));
    }
    
    /**
     * Logout endpoint
     */
    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        System.out.println("🚪 Logout endpoint hit");
        return ResponseEntity.ok(new MessageResponse("Đăng xuất thành công"));
    }
    
    // ===================================================================
    // INNER CLASSES
    // ===================================================================
    
    static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
        }
        
        public void setError(String error) {
            this.error = error;
        }
    }
    
    static class MessageResponse {
        private String message;
        
        public MessageResponse(String message) {
            this.message = message;
        }
        
        public String getMessage() {
            return message;
        }
        
        public void setMessage(String message) {
            this.message = message;
        }
    }
}