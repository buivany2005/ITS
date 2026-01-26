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
    
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {
        try {
            // Tìm user theo email
            Optional<User> userOpt = userRepository.findByEmail(request.getEmail());
            
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Email không tồn tại"));
            }
            
            User user = userOpt.get();
            
            // Kiểm tra password (chưa mã hóa - development only)
            if (!user.getPassword().equals(request.getPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Mật khẩu không đúng"));
            }
            
            // Đăng nhập thành công
            LoginResponse response = new LoginResponse(
                user.getId(),
                user.getEmail(),
                user.getFullName(),
                user.getRole().toString(),
                "Đăng nhập thành công"
            );
            
            return ResponseEntity.ok(response);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Lỗi server: " + e.getMessage()));
        }
    }
    
    @GetMapping("/check")
    public ResponseEntity<?> checkAuth() {
        return ResponseEntity.ok(new MessageResponse("API auth hoạt động"));
    }
    
    @PostMapping("/change-password")
    public ResponseEntity<?> changePassword(@RequestBody ChangePasswordRequest request) {
        try {
            // Validate input
            if (request.getCurrentPassword() == null || request.getCurrentPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Mật khẩu hiện tại không được để trống"));
            }
            
            if (request.getNewPassword() == null || request.getNewPassword().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Mật khẩu mới không được để trống"));
            }
            
            // Get user by ID
            Optional<User> userOpt = userRepository.findById(request.getUserId());
            if (userOpt.isEmpty()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Không tìm thấy người dùng"));
            }
            
            User user = userOpt.get();
            
            // Verify current password
            if (!user.getPassword().equals(request.getCurrentPassword())) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(new ErrorResponse("Mật khẩu hiện tại không đúng"));
            }
            
            // Update password
            user.setPassword(request.getNewPassword());
            userRepository.save(user);
            
            return ResponseEntity.ok(new MessageResponse("Đổi mật khẩu thành công"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Lỗi server: " + e.getMessage()));
        }
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest request) {
        try {
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
                return ResponseEntity.badRequest()
                    .body(new ErrorResponse("Email đã được đăng ký"));
            }
            
            // Create new user
            User newUser = new User();
            newUser.setFullName(request.getFullName());
            newUser.setEmail(request.getEmail());
            newUser.setPassword(request.getPassword()); // Note: plain text - should be hashed in production
            newUser.setPhone(request.getPhone());
            newUser.setAddress(request.getAddress());
            newUser.setRole(UserRole.USER); // Default role for new users
            
            User savedUser = userRepository.save(newUser);
            
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
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse("Lỗi server: " + e.getMessage()));
        }
    }
    
    // Inner classes for responses
    static class ErrorResponse {
        private String error;
        
        public ErrorResponse(String error) {
            this.error = error;
        }
        
        public String getError() {
            return error;
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
    }
    
    // Change Password Request
    static class ChangePasswordRequest {
        private Long userId;
        private String currentPassword;
        private String newPassword;
        
        public Long getUserId() { return userId; }
        public void setUserId(Long userId) { this.userId = userId; }
        
        public String getCurrentPassword() { return currentPassword; }
        public void setCurrentPassword(String currentPassword) { this.currentPassword = currentPassword; }
        
        public String getNewPassword() { return newPassword; }
        public void setNewPassword(String newPassword) { this.newPassword = newPassword; }
    }
}
