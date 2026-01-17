package com.example.backend.service;

import com.example.backend.entity.User;
import com.example.backend.entity.User.UserRole;
import com.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    /**
     * Get all users (admin)
     */
    @Transactional(readOnly = true)
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }
    
    /**
     * Get user by ID
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserById(Long id) {
        return userRepository.findById(id);
    }
    
    /**
     * Get user by email
     */
    @Transactional(readOnly = true)
    public Optional<User> getUserByEmail(String email) {
        return userRepository.findByEmail(email);
    }
    
    /**
     * Update user profile
     */
    public User updateProfile(Long userId, User userData) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        if (userData.getFullName() != null) user.setFullName(userData.getFullName());
        if (userData.getPhone() != null) user.setPhone(userData.getPhone());
        if (userData.getAddress() != null) user.setAddress(userData.getAddress());
        
        return userRepository.save(user);
    }
    
    /**
     * Change password
     */
    public void changePassword(Long userId, String oldPassword, String newPassword) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Verify old password (plain text comparison - should use BCrypt in production)
        if (!user.getPassword().equals(oldPassword)) {
            throw new RuntimeException("Mật khẩu cũ không đúng");
        }
        
        user.setPassword(newPassword);
        userRepository.save(user);
    }
    
    /**
     * Delete user (admin)
     */
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new RuntimeException("User không tồn tại");
        }
        userRepository.deleteById(id);
    }
    
    /**
     * Update user role (admin)
     */
    public User updateUserRole(Long userId, UserRole newRole) {
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        user.setRole(newRole);
        return userRepository.save(user);
    }
    
    /**
     * Get user count
     */
    @Transactional(readOnly = true)
    public long getUserCount() {
        return userRepository.count();
    }
}
