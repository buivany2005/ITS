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
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
        return orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"))
            .stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
     * Get orders with pagination
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getOrdersWithPagination(int page, int size, String query, String status, 
                                                       LocalDate dateFrom, LocalDate dateTo) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Order> orderPage;
        
        // Apply filters
        if (query != null && !query.isEmpty() && dateFrom != null && dateTo != null) {
            orderPage = orderRepository.searchByVehicleNameAndDateRange(query, dateFrom, dateTo, pageable);
        } else if (query != null && !query.isEmpty()) {
            orderPage = orderRepository.searchByVehicleName(query, pageable);
        } else if (dateFrom != null && dateTo != null) {
            orderPage = orderRepository.findByDateRange(dateFrom, dateTo, pageable);
        } else if (status != null && !status.isEmpty() && !status.equals("all")) {
            OrderStatus orderStatus = OrderStatus.valueOf(status.toUpperCase());
            orderPage = orderRepository.findByStatusOrderByCreatedAtDesc(orderStatus, pageable);
        } else {
            orderPage = orderRepository.findAll(pageable);
        }
        
        Map<String, Object> response = new HashMap<>();
        response.put("orders", orderPage.getContent().stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList()));
        response.put("currentPage", orderPage.getNumber());
        response.put("totalItems", orderPage.getTotalElements());
        response.put("totalPages", orderPage.getTotalPages());
        
        return response;
    }
    
    /**
     * Search orders (admin)
     */
    @Transactional(readOnly = true)
    public List<OrderResponse> searchOrders(String query, LocalDate dateFrom, LocalDate dateTo) {
        List<Order> orders;
        
        if (query != null && !query.isEmpty() && dateFrom != null && dateTo != null) {
            orders = orderRepository.searchByVehicleNameAndDateRange(query, dateFrom, dateTo);
        } else if (query != null && !query.isEmpty()) {
            orders = orderRepository.searchByVehicleName(query);
        } else if (dateFrom != null && dateTo != null) {
            orders = orderRepository.findByDateRange(dateFrom, dateTo);
        } else {
            orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        }
        
        return orders.stream()
            .map(OrderResponse::fromEntity)
            .collect(Collectors.toList());
    }
    
    /**
=======
>>>>>>> fafb6f636f639debf1e987ffba5915058ddf4722
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
    /**
     * Get statistics report
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getStatisticsReport() {
        List<Order> allOrders = orderRepository.findAll();
        OrderStats stats = getOrderStats();
        
        double totalRevenue = allOrders.stream()
            .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
            .mapToDouble(o -> o.getTotalPrice().doubleValue())
            .sum();
        
        String mostRentedVehicle = allOrders.stream()
            .collect(Collectors.groupingBy(Order::getVehicle, Collectors.counting()))
            .entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(e -> e.getKey().getName())
            .orElse("N/A");
        
        double averageRating = vehicleRepository.findAll().stream()
            .filter(v -> v.getRating() != null)
            .mapToDouble(v -> v.getRating().doubleValue())
            .average()
            .orElse(0.0);
        
        Map<String, Object> report = new HashMap<>();
        report.put("totalVehicles", vehicleRepository.count());
        report.put("availableVehicles", vehicleRepository.countByStatus(com.example.backend.entity.Vehicle.VehicleStatus.AVAILABLE));
        report.put("rentedVehicles", vehicleRepository.countByStatus(com.example.backend.entity.Vehicle.VehicleStatus.RENTED));
        report.put("maintenanceVehicles", vehicleRepository.countByStatus(com.example.backend.entity.Vehicle.VehicleStatus.MAINTENANCE));
        report.put("totalOrders", stats.getTotalOrders());
        report.put("completedOrders", stats.getCompletedOrders());
        report.put("pendingOrders", stats.getPendingOrders());
        report.put("totalRevenue", String.format("%,.0f", totalRevenue));
        report.put("mostRentedVehicle", mostRentedVehicle);
        report.put("averageRating", String.format("%.2f", averageRating));
        
        return report;
    }
    
    /**
     * Export orders to Excel
     */
    public byte[] exportOrdersToExcel() throws Exception {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Đơn đặt xe");
            
            // Create header style
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 12);
            headerStyle.setFont(headerFont);
            headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            headerStyle.setBorderBottom(BorderStyle.THIN);
            headerStyle.setBorderTop(BorderStyle.THIN);
            headerStyle.setBorderLeft(BorderStyle.THIN);
            headerStyle.setBorderRight(BorderStyle.THIN);
            
            // Create data style
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            // Create header row
            Row headerRow = sheet.createRow(0);
            String[] columns = {"ID", "Khách hàng", "Email", "Số điện thoại", "Phương tiện", 
                               "Ngày thuê", "Ngày trả", "Số ngày", "Giá/ngày", "Tổng tiền", "Trạng thái"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(headerStyle);
            }
            
            // Fill data
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int rowNum = 1;
            for (Order order : orders) {
                Row row = sheet.createRow(rowNum++);
                
                Cell cell0 = row.createCell(0);
                cell0.setCellValue(order.getId());
                cell0.setCellStyle(dataStyle);
                
                Cell cell1 = row.createCell(1);
                cell1.setCellValue(order.getCustomerName() != null ? order.getCustomerName() : order.getUser().getFullName());
                cell1.setCellStyle(dataStyle);
                
                Cell cell2 = row.createCell(2);
                cell2.setCellValue(order.getCustomerEmail() != null ? order.getCustomerEmail() : order.getUser().getEmail());
                cell2.setCellStyle(dataStyle);
                
                Cell cell3 = row.createCell(3);
                cell3.setCellValue(order.getCustomerPhone() != null ? order.getCustomerPhone() : order.getUser().getPhone());
                cell3.setCellStyle(dataStyle);
                
                Cell cell4 = row.createCell(4);
                cell4.setCellValue(order.getVehicle().getName());
                cell4.setCellStyle(dataStyle);
                
                Cell cell5 = row.createCell(5);
                cell5.setCellValue(order.getDateFrom().format(dateFormatter));
                cell5.setCellStyle(dataStyle);
                
                Cell cell6 = row.createCell(6);
                cell6.setCellValue(order.getDateTo().format(dateFormatter));
                cell6.setCellStyle(dataStyle);
                
                Cell cell7 = row.createCell(7);
                cell7.setCellValue(order.getTotalDays());
                cell7.setCellStyle(dataStyle);
                
                Cell cell8 = row.createCell(8);
                cell8.setCellValue(order.getPricePerDay().doubleValue());
                cell8.setCellStyle(dataStyle);
                
                Cell cell9 = row.createCell(9);
                cell9.setCellValue(order.getTotalPrice().doubleValue());
                cell9.setCellStyle(dataStyle);
                
                Cell cell10 = row.createCell(10);
                cell10.setCellValue(getStatusInVietnamese(order.getStatus()));
                cell10.setCellStyle(dataStyle);
            }
            
            // Auto-size columns
            for (int i = 0; i < columns.length; i++) {
                sheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
    }
    
    private String getStatusInVietnamese(OrderStatus status) {
        switch (status) {
            case PENDING: return "Chờ xác nhận";
            case CONFIRMED: return "Đã xác nhận";
            case IN_PROGRESS: return "Đang thuê";
            case COMPLETED: return "Hoàn thành";
            case CANCELLED: return "Đã hủy";
            default: return status.name();
        }
    }
    
    /**
     * Export statistics to Excel
     */
    public byte[] exportStatisticsToExcel() throws Exception {
        List<Order> orders = orderRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
        OrderStats orderStats = getOrderStats();
        
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            
            // Sheet 1: Tổng quan
            Sheet summarySheet = workbook.createSheet("Tổng quan");
            
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerFont.setFontHeightInPoints((short) 14);
            headerStyle.setFont(headerFont);
            
            CellStyle labelStyle = workbook.createCellStyle();
            Font labelFont = workbook.createFont();
            labelFont.setBold(true);
            labelStyle.setFont(labelFont);
            
            // Title
            Row titleRow = summarySheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellValue("BÁO CÁO THỐNG KÊ HỆ THỐNG THUÊ XE");
            titleCell.setCellStyle(headerStyle);
            
            // Date
            Row dateRow = summarySheet.createRow(1);
            dateRow.createCell(0).setCellValue("Ngày xuất báo cáo:");
            dateRow.createCell(1).setCellValue(java.time.LocalDateTime.now().format(
                java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss")));
            
            // Statistics
            int rowNum = 3;
            Row row1 = summarySheet.createRow(rowNum++);
            Cell cell1 = row1.createCell(0);
            cell1.setCellValue("Tổng số đơn đặt xe:");
            cell1.setCellStyle(labelStyle);
            row1.createCell(1).setCellValue(orderStats.getTotalOrders());
            
            Row row2 = summarySheet.createRow(rowNum++);
            Cell cell2 = row2.createCell(0);
            cell2.setCellValue("Đơn chờ xác nhận:");
            cell2.setCellStyle(labelStyle);
            row2.createCell(1).setCellValue(orderStats.getPendingOrders());
            
            Row row3 = summarySheet.createRow(rowNum++);
            Cell cell3 = row3.createCell(0);
            cell3.setCellValue("Đơn đã xác nhận:");
            cell3.setCellStyle(labelStyle);
            row3.createCell(1).setCellValue(orderStats.getConfirmedOrders());
            
            Row row4 = summarySheet.createRow(rowNum++);
            Cell cell4 = row4.createCell(0);
            cell4.setCellValue("Đơn đang thuê:");
            cell4.setCellStyle(labelStyle);
            row4.createCell(1).setCellValue(orderStats.getInProgressOrders());
            
            Row row5 = summarySheet.createRow(rowNum++);
            Cell cell5 = row5.createCell(0);
            cell5.setCellValue("Đơn hoàn thành:");
            cell5.setCellStyle(labelStyle);
            row5.createCell(1).setCellValue(orderStats.getCompletedOrders());
            
            Row row6 = summarySheet.createRow(rowNum++);
            Cell cell6 = row6.createCell(0);
            cell6.setCellValue("Đơn đã hủy:");
            cell6.setCellStyle(labelStyle);
            row6.createCell(1).setCellValue(orderStats.getCancelledOrders());
            
            // Calculate total revenue
            double totalRevenue = orders.stream()
                .filter(o -> o.getStatus() == OrderStatus.COMPLETED)
                .mapToDouble(o -> o.getTotalPrice().doubleValue())
                .sum();
            
            rowNum++;
            Row revenueRow = summarySheet.createRow(rowNum++);
            Cell revenueCell = revenueRow.createCell(0);
            revenueCell.setCellValue("Tổng doanh thu (đơn hoàn thành):");
            revenueCell.setCellStyle(labelStyle);
            revenueRow.createCell(1).setCellValue(String.format("%,.0f VNĐ", totalRevenue));
            
            summarySheet.autoSizeColumn(0);
            summarySheet.autoSizeColumn(1);
            
            // Sheet 2: Chi tiết đơn hàng
            Sheet detailSheet = workbook.createSheet("Chi tiết đơn hàng");
            
            CellStyle tableHeaderStyle = workbook.createCellStyle();
            Font tableHeaderFont = workbook.createFont();
            tableHeaderFont.setBold(true);
            tableHeaderStyle.setFont(tableHeaderFont);
            tableHeaderStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
            tableHeaderStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
            tableHeaderStyle.setBorderBottom(BorderStyle.THIN);
            tableHeaderStyle.setBorderTop(BorderStyle.THIN);
            tableHeaderStyle.setBorderLeft(BorderStyle.THIN);
            tableHeaderStyle.setBorderRight(BorderStyle.THIN);
            
            CellStyle dataStyle = workbook.createCellStyle();
            dataStyle.setBorderBottom(BorderStyle.THIN);
            dataStyle.setBorderTop(BorderStyle.THIN);
            dataStyle.setBorderLeft(BorderStyle.THIN);
            dataStyle.setBorderRight(BorderStyle.THIN);
            
            Row headerRow = detailSheet.createRow(0);
            String[] columns = {"ID", "Khách hàng", "Email", "Số điện thoại", "Phương tiện", 
                               "Ngày thuê", "Ngày trả", "Số ngày", "Giá/ngày", "Tổng tiền", "Trạng thái"};
            
            for (int i = 0; i < columns.length; i++) {
                Cell cell = headerRow.createCell(i);
                cell.setCellValue(columns[i]);
                cell.setCellStyle(tableHeaderStyle);
            }
            
            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            int detailRowNum = 1;
            for (Order order : orders) {
                Row row = detailSheet.createRow(detailRowNum++);
                
                Cell c0 = row.createCell(0);
                c0.setCellValue(order.getId());
                c0.setCellStyle(dataStyle);
                
                Cell c1 = row.createCell(1);
                c1.setCellValue(order.getCustomerName() != null ? order.getCustomerName() : order.getUser().getFullName());
                c1.setCellStyle(dataStyle);
                
                Cell c2 = row.createCell(2);
                c2.setCellValue(order.getCustomerEmail() != null ? order.getCustomerEmail() : order.getUser().getEmail());
                c2.setCellStyle(dataStyle);
                
                Cell c3 = row.createCell(3);
                c3.setCellValue(order.getCustomerPhone() != null ? order.getCustomerPhone() : order.getUser().getPhone());
                c3.setCellStyle(dataStyle);
                
                Cell c4 = row.createCell(4);
                c4.setCellValue(order.getVehicle().getName());
                c4.setCellStyle(dataStyle);
                
                Cell c5 = row.createCell(5);
                c5.setCellValue(order.getDateFrom().format(dateFormatter));
                c5.setCellStyle(dataStyle);
                
                Cell c6 = row.createCell(6);
                c6.setCellValue(order.getDateTo().format(dateFormatter));
                c6.setCellStyle(dataStyle);
                
                Cell c7 = row.createCell(7);
                c7.setCellValue(order.getTotalDays());
                c7.setCellStyle(dataStyle);
                
                Cell c8 = row.createCell(8);
                c8.setCellValue(order.getPricePerDay().doubleValue());
                c8.setCellStyle(dataStyle);
                
                Cell c9 = row.createCell(9);
                c9.setCellValue(order.getTotalPrice().doubleValue());
                c9.setCellStyle(dataStyle);
                
                Cell c10 = row.createCell(10);
                c10.setCellValue(getStatusInVietnamese(order.getStatus()));
                c10.setCellStyle(dataStyle);
            }
            
            for (int i = 0; i < columns.length; i++) {
                detailSheet.autoSizeColumn(i);
            }
            
            workbook.write(out);
            return out.toByteArray();
        }
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
