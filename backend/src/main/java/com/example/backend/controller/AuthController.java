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
    // --- BẮT ĐẦU ĐOẠN CODE THÊM MỚI ---

    // 1. API Lấy thông tin chi tiết User (để hiển thị Avatar)
    @GetMapping("/{id}")
    public ResponseEntity<?> getUserById(@PathVariable Long id) {
        Optional<User> userOpt = userRepository.findById(id);
        if (userOpt.isPresent()) {
            return ResponseEntity.ok(userOpt.get());
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Không tìm thấy người dùng"));
        }
    }

    // 2. API Cập nhật thông tin User (Avatar, Tên, SĐT...)
    @PutMapping("/update/{id}")
    public ResponseEntity<?> updateUser(@PathVariable Long id, @RequestBody User updateInfo) {
        Optional<User> userOpt = userRepository.findById(id);
        
        if (userOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse("Người dùng không tồn tại"));
        }

        User existingUser = userOpt.get();
        
        // Chỉ cập nhật những trường được gửi lên
        if (updateInfo.getFullName() != null) existingUser.setFullName(updateInfo.getFullName());
        if (updateInfo.getPhone() != null) existingUser.setPhone(updateInfo.getPhone());
        if (updateInfo.getAddress() != null) existingUser.setAddress(updateInfo.getAddress());
        
        // Cập nhật Avatar
        if (updateInfo.getAvatarUrl() != null) {
            existingUser.setAvatarUrl(updateInfo.getAvatarUrl());
        }

        User savedUser = userRepository.save(existingUser);
        return ResponseEntity.ok(savedUser);
    }
    
    // --- KẾT THÚC ĐOẠN CODE THÊM MỚI ---


    
}
