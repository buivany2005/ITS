package com.example.backend.service;

import com.example.backend.dto.OrderRequest;
import com.example.backend.dto.OrderResponse;
import com.example.backend.entity.Order;
import com.example.backend.entity.User;
import com.example.backend.entity.Vehicle;
import com.example.backend.repository.OrderRepository;
import com.example.backend.repository.UserRepository;
import com.example.backend.repository.VehicleRepository;
<<<<<<< HEAD
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
=======
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
>>>>>>> c7b20e3812e5add1651baa4d639b573578a1b157
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
<<<<<<< HEAD
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
     * Get all orders (admin) - paginated
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable) {
        return orderRepository.findAll(pageable)
            .map(OrderResponse::fromEntity);
    }

    /**
     * Get all orders (admin) - paginated with optional date range filter
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrders(Pageable pageable, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) {
        if (dateFrom != null && dateTo != null) {
            return orderRepository
                .findByDateFromGreaterThanEqualAndDateToLessThanEqualOrderByCreatedAtDesc(dateFrom, dateTo, pageable)
                .map(OrderResponse::fromEntity);
        }
        return getAllOrders(pageable);
    }    
    /**
     * Get all orders (admin) - non-paginated for backward compatibility
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrders() {
        return orderRepository.findAll()
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }    
    /**
     * Get all orders by status - paginated
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable) {
        return orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
            .map(OrderResponse::fromEntity);
    }
    
    /**
     * Get all orders by status (admin) - paginated with optional date range filter
     */
    @Transactional(readOnly = true)
    public Page<OrderResponse> getAllOrdersByStatus(OrderStatus status, Pageable pageable, java.time.LocalDate dateFrom, java.time.LocalDate dateTo) {
        if (dateFrom != null && dateTo != null) {
            return orderRepository
                .findByStatusAndDateFromGreaterThanEqualAndDateToLessThanEqualOrderByCreatedAtDesc(status, dateFrom, dateTo, pageable)
                .map(OrderResponse::fromEntity);
        }
        return getAllOrdersByStatus(status, pageable);
    }
    /**
     * Get all orders by status - non-paginated for backward compatibility
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> getAllOrdersByStatus(OrderStatus status) {
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
=======
                .orElseThrow(() -> new RuntimeException("Order not found"));

        Order.OrderStatus oldStatus = order.getStatus();
        order.setStatus(status);

        // Update vehicle status based on order status
        Vehicle vehicle = order.getVehicle();
>>>>>>> c7b20e3812e5add1651baa4d639b573578a1b157
        
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
<<<<<<< HEAD
    
    /**
     * Export orders to real XLSX Excel file using Apache POI
     */
    public byte[] exportOrdersToExcel() throws Exception {
        List<Order> orders = orderRepository.findAll();

        org.apache.poi.xssf.usermodel.XSSFWorkbook workbook = new org.apache.poi.xssf.usermodel.XSSFWorkbook();
        org.apache.poi.ss.usermodel.Sheet sheet = workbook.createSheet("Orders");

        // Header
        org.apache.poi.ss.usermodel.Row header = sheet.createRow(0);
        String[] headers = new String[] {"ID", "Khách hàng", "Phương tiện", "Ngày thuê", "Ngày trả", "Trạng thái", "Tổng tiền"};
        for (int i = 0; i < headers.length; i++) {
            org.apache.poi.ss.usermodel.Cell c = header.createCell(i);
            c.setCellValue(headers[i]);
        }

        // Create a cell style for currency
        org.apache.poi.ss.usermodel.CellStyle currencyStyle = workbook.createCellStyle();
        org.apache.poi.ss.usermodel.DataFormat df = workbook.createDataFormat();
        currencyStyle.setDataFormat(df.getFormat("#,##0.00"));

        int rowIdx = 1;
        java.time.format.DateTimeFormatter dtf = java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd");
        for (Order order : orders) {
            org.apache.poi.ss.usermodel.Row row = sheet.createRow(rowIdx++);
            row.createCell(0).setCellValue(order.getId().toString());
            row.createCell(1).setCellValue(order.getUser() != null ? safeString(order.getUser().getFullName()) : "");
            row.createCell(2).setCellValue(order.getVehicle() != null ? safeString(order.getVehicle().getName()) : "");
            row.createCell(3).setCellValue(order.getDateFrom() != null ? order.getDateFrom().format(dtf) : "");
            row.createCell(4).setCellValue(order.getDateTo() != null ? order.getDateTo().format(dtf) : "");
            row.createCell(5).setCellValue(order.getStatus() != null ? order.getStatus().name() : "");
            org.apache.poi.ss.usermodel.Cell priceCell = row.createCell(6);
            if (order.getTotalPrice() != null) {
                try {
                    priceCell.setCellValue(order.getTotalPrice().doubleValue());
                    priceCell.setCellStyle(currencyStyle);
                } catch (Exception ex) {
                    priceCell.setCellValue(order.getTotalPrice().toString());
                }
            } else {
                priceCell.setCellValue(0);
            }
        }

        // Autosize columns (reasonable for small exports)
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }

        java.io.ByteArrayOutputStream baos = new java.io.ByteArrayOutputStream();
        try {
            workbook.write(baos);
        } finally {
            workbook.close();
        }
        return baos.toByteArray();
    }

    private String safeString(String s) {
        return s == null ? "" : s;
=======

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
>>>>>>> c7b20e3812e5add1651baa4d639b573578a1b157
    }
}