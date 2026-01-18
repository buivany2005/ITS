package com.example.backend.service;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order;
import com.example.backend.entity.Order.OrderStatus;
import com.example.backend.entity.User;
import com.example.backend.entity.Vehicle;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class OrderService {
    
    @Autowired
    private OrderRepository orderRepository;
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    /**
     * Create a new order
     */
    public OrderResponse createOrder(Long userId, OrderRequest request) {
        // Validate user
        User user = userRepository.findById(userId)
            .orElseThrow(() -> new RuntimeException("User không tồn tại"));
        
        // Validate vehicle
        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
            .orElseThrow(() -> new RuntimeException("Phương tiện không tồn tại"));
        
        // Check vehicle availability
        if (!isVehicleAvailable(request.getVehicleId(), request.getDateFrom(), request.getDateTo())) {
            throw new RuntimeException("Phương tiện không khả dụng trong khoảng thời gian này");
        }
        
        // Create order
        Order order = new Order();
        order.setUser(user);
        order.setVehicle(vehicle);
        order.setDateFrom(request.getDateFrom());
        order.setDateTo(request.getDateTo());
        order.setPricePerDay(vehicle.getPricePerDay());
        order.setPickupLocation(request.getPickupLocation());
        order.setReturnLocation(request.getReturnLocation());
        order.setCustomerName(request.getCustomerName() != null ? request.getCustomerName() : user.getFullName());
        order.setCustomerPhone(request.getCustomerPhone() != null ? request.getCustomerPhone() : user.getPhone());
        order.setCustomerEmail(request.getCustomerEmail() != null ? request.getCustomerEmail() : user.getEmail());
        order.setNotes(request.getNotes());
        order.setStatus(OrderStatus.PENDING);
        
        Order savedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(savedOrder);
    }
    
    /**
     * Get order by ID
     */
    @Transactional(readOnly = true)
    public OrderResponse getOrderById(Long orderId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        return OrderResponse.fromEntity(order);
    }
    
    /**
     * Get all orders for a user
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrders(Long userId) {
        return orderRepository.findByUserIdOrderByCreatedAtDesc(userId)
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Get user orders by status
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getUserOrdersByStatus(Long userId, OrderStatus status) {
        return orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status)
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Get all orders (admin)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Get orders by status (admin)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getOrdersByStatus(OrderStatus status) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status)
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Update order status
     */
    public OrderResponse updateOrderStatus(Long orderId, OrderStatus newStatus) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        order.setStatus(newStatus);
        Order updatedOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(updatedOrder);
    }
    
    /**
     * Cancel order (user)
     */
    public OrderResponse cancelOrder(Long orderId, Long userId) {
        Order order = orderRepository.findById(orderId)
            .orElseThrow(() -> new RuntimeException("Đơn hàng không tồn tại"));
        
        // Check ownership
        if (!order.getUser().getId().equals(userId)) {
            throw new RuntimeException("Bạn không có quyền hủy đơn hàng này");
        }
        
        // Check if cancellable
        if (order.getStatus() != OrderStatus.PENDING) {
            throw new RuntimeException("Chỉ có thể hủy đơn hàng đang chờ xác nhận");
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        Order cancelledOrder = orderRepository.save(order);
        return OrderResponse.fromEntity(cancelledOrder);
    }
    
    /**
     * Check if vehicle is available for date range
     */
    @Transactional(readOnly = true)
    public boolean isVehicleAvailable(Long vehicleId, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) {
        long conflictCount = orderRepository.countConflictingOrders(vehicleId, dateFrom, dateTo);
        return conflictCount == 0;
    }
    
    /**
     * Get order statistics
     */
    @Transactional(readOnly = true)
    public OrderStats getOrderStats() {
        OrderStats stats = new OrderStats();
        stats.setTotalOrders(orderRepository.count());
        stats.setPendingOrders(orderRepository.countByStatus(OrderStatus.PENDING));
        stats.setConfirmedOrders(orderRepository.countByStatus(OrderStatus.CONFIRMED));
        stats.setInProgressOrders(orderRepository.countByStatus(OrderStatus.IN_PROGRESS));
        stats.setCompletedOrders(orderRepository.countByStatus(OrderStatus.COMPLETED));
        stats.setCancelledOrders(orderRepository.countByStatus(OrderStatus.CANCELLED));
        return stats;
    }
    
    // Inner class for stats
    public static class OrderStats {
        private long totalOrders;
        private long pendingOrders;
        private long confirmedOrders;
        private long inProgressOrders;
        private long completedOrders;
        private long cancelledOrders;
        
        // Getters and Setters
        public long getTotalOrders() { return totalOrders; }
        public void setTotalOrders(long totalOrders) { this.totalOrders = totalOrders; }
        public long getPendingOrders() { return pendingOrders; }
        public void setPendingOrders(long pendingOrders) { this.pendingOrders = pendingOrders; }
        public long getConfirmedOrders() { return confirmedOrders; }
        public void setConfirmedOrders(long confirmedOrders) { this.confirmedOrders = confirmedOrders; }
        public long getInProgressOrders() { return inProgressOrders; }
        public void setInProgressOrders(long inProgressOrders) { this.inProgressOrders = inProgressOrders; }
        public long getCompletedOrders() { return completedOrders; }
        public void setCompletedOrders(long completedOrders) { this.completedOrders = completedOrders; }
        public long getCancelledOrders() { return cancelledOrders; }
        public void setCancelledOrders(long cancelledOrders) { this.cancelledOrders = cancelledOrders; }
    }
}
