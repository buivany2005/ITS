package com.example.backend.service;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order;
import com.example.backend.entity.User;
import com.example.backend.entity.Vehicle;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VehicleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final VehicleRepository vehicleRepository;

    @Transactional
    public Order createOrder(OrderRequest request, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Vehicle vehicle = vehicleRepository.findById(request.getVehicleId())
                .orElseThrow(() -> new RuntimeException("Vehicle not found"));

        // Check vehicle availability
        if (vehicle.getStatus() != Vehicle.VehicleStatus.AVAILABLE) {
            throw new RuntimeException("Vehicle is not available");
        }

        // Check for conflicting orders
        Long conflictingOrders = orderRepository.countConflictingOrders(
                vehicle.getId(),
                request.getStartDate(),
                request.getEndDate()
        );

        if (conflictingOrders > 0) {
            throw new RuntimeException("Vehicle is already booked for these dates");
        }

        // Calculate total price
        long days = ChronoUnit.DAYS.between(request.getStartDate(), request.getEndDate());
        if (days <= 0) {
            throw new RuntimeException("End date must be after start date");
        }
        BigDecimal totalPrice = vehicle.getRentalPricePerDay()
                .multiply(BigDecimal.valueOf(days));

        // Create order
        Order order = Order.builder()
                .user(user)
                .vehicle(vehicle)
                .startDate(request.getStartDate())
                .endDate(request.getEndDate())
                .totalPrice(totalPrice)
                .status(Order.OrderStatus.PENDING)
                .notes(request.getNotes())
                .build();

        return orderRepository.save(order);
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, Order.OrderStatus status) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        // Update vehicle status based on order status
        Vehicle vehicle = order.getVehicle();
        
        if (status == Order.OrderStatus.ACTIVE && oldStatus != Order.OrderStatus.ACTIVE) {
            vehicle.setStatus(Vehicle.VehicleStatus.RENTED);
        } else if (status == Order.OrderStatus.COMPLETED || status == Order.OrderStatus.CANCELLED) {
            // Check if there are other active orders for this vehicle
            List<Order> activeOrders = orderRepository.findByStatusOrderByCreatedAtDesc(Order.OrderStatus.ACTIVE)
                    .stream()
                    .filter(o -> o.getVehicle().getId().equals(vehicle.getId()) && !o.getId().equals(orderId))
                    .collect(Collectors.toList());
            
            if (activeOrders.isEmpty()) {
                vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            }
        }

        vehicleRepository.save(vehicle);
        return orderRepository.save(order);
    }

    public List<OrderResponse> getUserOrders(String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserIdOrderByCreatedAtDesc(user.getId());
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public List<OrderResponse> getUserOrdersByStatus(String userEmail, Order.OrderStatus status) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        List<Order> orders = orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(user.getId(), status);
        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    public OrderResponse getOrderById(Long orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        // Check if order belongs to user (unless user is admin)
        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized access to order");
        }

        return convertToResponse(order);
    }

    public Page<OrderResponse> searchOrders(String search, LocalDate startDate, LocalDate endDate, 
                                           String status, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());

        Page<Order> orders;

        if (search != null && !search.trim().isEmpty() && startDate != null && endDate != null) {
            orders = orderRepository.searchByVehicleNameAndDateRange(search, startDate, endDate, pageable);
        } else if (search != null && !search.trim().isEmpty()) {
            orders = orderRepository.searchByVehicleName(search, pageable);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByDateRange(startDate, endDate, pageable);
        } else if (status != null && !status.trim().isEmpty()) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus, pageable);
        } else {
            orders = orderRepository.findAll(pageable);
        }

        return orders.map(this::convertToResponse);
    }

    public List<OrderResponse> searchOrdersForExport(String search, LocalDate startDate, 
                                                     LocalDate endDate, String status) {
        List<Order> orders;

        if (search != null && !search.trim().isEmpty() && startDate != null && endDate != null) {
            orders = orderRepository.searchByVehicleNameAndDateRange(search, startDate, endDate);
        } else if (search != null && !search.trim().isEmpty()) {
            orders = orderRepository.searchByVehicleName(search);
        } else if (startDate != null && endDate != null) {
            orders = orderRepository.findByDateRange(startDate, endDate);
        } else if (status != null && !status.trim().isEmpty()) {
            Order.OrderStatus orderStatus = Order.OrderStatus.valueOf(status.toUpperCase());
            orders = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus);
        } else {
            orders = orderRepository.findAll(Sort.by("createdAt").descending());
        }

        return orders.stream()
                .map(this::convertToResponse)
                .collect(Collectors.toList());
    }

    @Transactional
    public void cancelOrder(Long orderId, String userEmail) {
        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new RuntimeException("User not found"));

        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));

        if (!order.getUser().getId().equals(user.getId()) && user.getRole() != User.Role.ADMIN) {
            throw new RuntimeException("Unauthorized to cancel this order");
        }

        if (order.getStatus() == Order.OrderStatus.COMPLETED) {
            throw new RuntimeException("Cannot cancel completed order");
        }

        if (order.getStatus() == Order.OrderStatus.CANCELLED) {
            throw new RuntimeException("Order is already cancelled");
        }

        order.setStatus(Order.OrderStatus.CANCELLED);
        
        // Make vehicle available again
        Vehicle vehicle = order.getVehicle();
        if (vehicle.getStatus() == Vehicle.VehicleStatus.RENTED) {
            vehicle.setStatus(Vehicle.VehicleStatus.AVAILABLE);
            vehicleRepository.save(vehicle);
        }

        orderRepository.save(order);
    }

    public boolean isVehicleAvailableForDates(Long vehicleId, LocalDate startDate, LocalDate endDate) {
        Long conflictingOrders = orderRepository.countConflictingOrders(vehicleId, startDate, endDate);
        return conflictingOrders == 0;
    }

    public Map<String, Object> getDashboardStats() {
        Map<String, Object> stats = new HashMap<>();
        
        stats.put("totalOrders", orderRepository.count());
        stats.put("pendingOrders", orderRepository.countByStatus(Order.OrderStatus.PENDING));
        stats.put("confirmedOrders", orderRepository.countByStatus(Order.OrderStatus.CONFIRMED));
        stats.put("activeOrders", orderRepository.countByStatus(Order.OrderStatus.ACTIVE));
        stats.put("completedOrders", orderRepository.countByStatus(Order.OrderStatus.COMPLETED));
        stats.put("cancelledOrders", orderRepository.countByStatus(Order.OrderStatus.CANCELLED));

        return stats;
    }

    private OrderResponse convertToResponse(Order order) {
        long days = ChronoUnit.DAYS.between(order.getStartDate(), order.getEndDate());
        BigDecimal pricePerDay = order.getTotalPrice().divide(BigDecimal.valueOf(days > 0 ? days : 1), 2, BigDecimal.ROUND_HALF_UP);
        
        return OrderResponse.builder()
                .id(order.getId())
                .vehicleId(order.getVehicle().getId())
                .vehicleName(order.getVehicle().getName())
                .vehicleBrand(order.getVehicle().getBrand())
                .vehicleModel(order.getVehicle().getModel())
                .vehicleImageUrl(order.getVehicle().getImageUrl())
                .customerId(order.getUser().getId())
                .customerName(order.getUser().getFullName())
                .customerEmail(order.getUser().getUsername())
                .customerPhone(order.getUser().getPhoneNumber())
                .startDate(order.getStartDate())
                .endDate(order.getEndDate())
                .dateFrom(order.getStartDate())
                .dateTo(order.getEndDate())
                .totalDays((int) days)
                .totalPrice(order.getTotalPrice())
                .pricePerDay(pricePerDay)
                .status(order.getStatus().name())
                .notes(order.getNotes())
                .createdAt(order.getCreatedAt())
                .updatedAt(order.getUpdatedAt())
                .build();
    }
}