package com.example.backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.regex.Pattern;

@RestController
@RequestMapping("/newsletter")
@CrossOrigin(origins = "*")
public class NewsletterController {
    
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    
    /**
     * Subscribe to newsletter
     */
    @PostMapping
    public ResponseEntity<?> subscribe(@RequestBody SubscribeRequest request) {
        try {
            String email = request.getEmail();
            
            // Validate email
            if (email == null || email.trim().isEmpty()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email không được để trống"));
            }
            
            if (!EMAIL_PATTERN.matcher(email).matches()) {
                return ResponseEntity.badRequest()
                    .body(Map.of("error", "Email không hợp lệ"));
            }
            
            // TODO: Save to database or send to email service
            // For now, just return success
            
            return ResponseEntity.status(HttpStatus.CREATED)
                .body(Map.of(
                    "message", "Đăng ký nhận tin thành công",
                    "email", email
                ));
                
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of("error", "Lỗi server: " + e.getMessage()));
        }
    }
    
    // Inner class
    static class SubscribeRequest {
        private String email;
        
        public String getEmail() { return email; }
        public void setEmail(String email) { this.email = email; }
    }
}