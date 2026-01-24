package com.example.backend.util;

import com.example.backend.dto.VehicleResponse;
import com.example.backend.dto.OrderResponse;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

public class ExcelExporter {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd/MM/yyyy");
    
    /**
     * Export vehicles to Excel
     */
    public static byte[] exportVehiclesToExcel(List<VehicleResponse> vehicles) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Phương tiện");
        
        // Header style
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "STT", "Tên xe", "Loại xe", "Hãng", "Model", "Năm", "Biển số", 
            "Màu sắc", "Chỗ ngồi", "Loại nhiên liệu", "Truyền động", 
            "Giá thuê/ngày (VNĐ)", "Trạng thái", "Tổng lần thuê", "Đánh giá", "Ngày tạo"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data rows
        int rowNum = 1;
        CellStyle dataStyle = createDataStyle(workbook);
        
        for (VehicleResponse vehicle : vehicles) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(vehicle.getName() != null ? vehicle.getName() : "");
            row.createCell(2).setCellValue(vehicle.getCategory() != null ? vehicle.getCategory() : "");
            row.createCell(3).setCellValue(vehicle.getBrand() != null ? vehicle.getBrand() : "");
            row.createCell(4).setCellValue(vehicle.getModel() != null ? vehicle.getModel() : "");
            row.createCell(5).setCellValue(vehicle.getYear() != null ? vehicle.getYear() : 0);
            row.createCell(6).setCellValue(vehicle.getLicensePlate() != null ? vehicle.getLicensePlate() : "");
            row.createCell(7).setCellValue(vehicle.getColor() != null ? vehicle.getColor() : "");
            row.createCell(8).setCellValue(vehicle.getSeats() != null ? vehicle.getSeats() : 0);
            row.createCell(9).setCellValue(vehicle.getFuelType() != null ? vehicle.getFuelType() : "");
            row.createCell(10).setCellValue(vehicle.getTransmission() != null ? vehicle.getTransmission() : "");
            
            Cell priceCell = row.createCell(11);
            priceCell.setCellValue(vehicle.getPricePerDay() != null ? vehicle.getPricePerDay().doubleValue() : 0);
            priceCell.setCellStyle(createNumberStyle(workbook));
            
            row.createCell(12).setCellValue(vehicle.getStatus() != null ? vehicle.getStatus() : "");
            row.createCell(13).setCellValue(vehicle.getTotalRentals() != null ? vehicle.getTotalRentals() : 0);
            row.createCell(14).setCellValue(vehicle.getRating() != null ? vehicle.getRating().doubleValue() : 0);
            row.createCell(15).setCellValue(vehicle.getCreatedAt() != null ? 
                vehicle.getCreatedAt().format(DATE_FORMATTER) : "");
            
            // Apply data style
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    /**
     * Export orders to Excel
     */
    public static byte[] exportOrdersToExcel(List<OrderResponse> orders) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("Đơn hàng");
        
        // Header style
        CellStyle headerStyle = createHeaderStyle(workbook);
        
        // Create header row
        Row headerRow = sheet.createRow(0);
        String[] headers = {
            "STT", "Mã đơn", "Tên khách hàng", "Email", "SĐT", "Xe", "Ngày bắt đầu", 
            "Ngày kết thúc", "Số ngày", "Giá/ngày (VNĐ)", "Tổng tiền (VNĐ)", "Trạng thái", "Ghi chú"
        };
        
        for (int i = 0; i < headers.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(headers[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // Data rows
        int rowNum = 1;
        CellStyle dataStyle = createDataStyle(workbook);
        
        for (OrderResponse order : orders) {
            Row row = sheet.createRow(rowNum++);
            
            row.createCell(0).setCellValue(rowNum - 1);
            row.createCell(1).setCellValue(order.getId() != null ? "DH" + order.getId() : "");
            row.createCell(2).setCellValue(order.getCustomerName() != null ? order.getCustomerName() : "");
            row.createCell(3).setCellValue(order.getCustomerEmail() != null ? order.getCustomerEmail() : "");
            row.createCell(4).setCellValue(order.getCustomerPhone() != null ? order.getCustomerPhone() : "");
            row.createCell(5).setCellValue(order.getVehicleName() != null ? order.getVehicleName() : "");
            row.createCell(6).setCellValue(order.getDateFrom() != null ? 
                order.getDateFrom().format(DATE_FORMATTER) : "");
            row.createCell(7).setCellValue(order.getDateTo() != null ? 
                order.getDateTo().format(DATE_FORMATTER) : "");
            row.createCell(8).setCellValue(order.getTotalDays() != null ? order.getTotalDays() : 0);
            
            Cell priceCell = row.createCell(9);
            priceCell.setCellValue(order.getPricePerDay() != null ? order.getPricePerDay().doubleValue() : 0);
            priceCell.setCellStyle(createNumberStyle(workbook));
            
            Cell totalCell = row.createCell(10);
            totalCell.setCellValue(order.getTotalPrice() != null ? order.getTotalPrice().doubleValue() : 0);
            totalCell.setCellStyle(createNumberStyle(workbook));
            
            row.createCell(11).setCellValue(order.getStatus() != null ? order.getStatus() : "");
            row.createCell(12).setCellValue(order.getNotes() != null ? order.getNotes() : "");
            
            // Apply data style
            for (int i = 0; i < headers.length; i++) {
                row.getCell(i).setCellStyle(dataStyle);
            }
        }
        
        // Auto-size columns
        for (int i = 0; i < headers.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    /**
     * Export statistics report
     */
    public static byte[] exportStatisticsReport(Map<String, Object> statistics) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        
        // Summary sheet
        Sheet summarySheet = workbook.createSheet("Thống kê");
        CellStyle headerStyle = createHeaderStyle(workbook);
        CellStyle dataStyle = createDataStyle(workbook);
        
        int rowNum = 0;
        
        // Title
        Row titleRow = summarySheet.createRow(rowNum++);
        Cell titleCell = titleRow.createCell(0);
        titleCell.setCellValue("BÁO CÁO THỐNG KÊ HỆ THỐNG QUẢN LÝ XE");
        CellStyle titleStyle = workbook.createCellStyle();
        titleStyle.setFont(createBoldFont(workbook, 14));
        titleCell.setCellStyle(titleStyle);
        
        // Skip a row
        rowNum++;
        
        // Statistics data
        if (statistics != null) {
            String[] labels = {
                "Tổng số phương tiện",
                "Phương tiện sẵn sàng",
                "Phương tiện đang thuê",
                "Phương tiện bảo trì",
                "Tổng số đơn hàng",
                "Đơn hàng hoàn tất",
                "Đơn hàng đang chờ",
                "Tổng doanh thu (VNĐ)",
                "Xe được thuê nhiều nhất",
                "Tỷ lệ hài lòng TB"
            };
            
            Object[] values = {
                statistics.getOrDefault("totalVehicles", 0),
                statistics.getOrDefault("availableVehicles", 0),
                statistics.getOrDefault("rentedVehicles", 0),
                statistics.getOrDefault("maintenanceVehicles", 0),
                statistics.getOrDefault("totalOrders", 0),
                statistics.getOrDefault("completedOrders", 0),
                statistics.getOrDefault("pendingOrders", 0),
                statistics.getOrDefault("totalRevenue", 0),
                statistics.getOrDefault("mostRentedVehicle", "N/A"),
                statistics.getOrDefault("averageRating", 0)
            };
            
            for (int i = 0; i < labels.length; i++) {
                Row row = summarySheet.createRow(rowNum++);
                
                Cell labelCell = row.createCell(0);
                labelCell.setCellValue(labels[i]);
                labelCell.setCellStyle(headerStyle);
                
                Cell valueCell = row.createCell(1);
                if (values[i] instanceof Number) {
                    valueCell.setCellValue(((Number) values[i]).doubleValue());
                    valueCell.setCellStyle(createNumberStyle(workbook));
                } else {
                    valueCell.setCellValue(values[i].toString());
                    valueCell.setCellStyle(dataStyle);
                }
            }
        }
        
        // Auto-size columns
        summarySheet.autoSizeColumn(0);
        summarySheet.autoSizeColumn(1);
        
        // Convert to byte array
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    // ========== HELPER METHODS ==========
    
    private static CellStyle createHeaderStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        Font font = createBoldFont(workbook, 11);
        style.setFont(font);
        style.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        style.setAlignment(HorizontalAlignment.CENTER);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle createDataStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.LEFT);
        style.setVerticalAlignment(VerticalAlignment.CENTER);
        style.setBorderBottom(BorderStyle.THIN);
        style.setBorderTop(BorderStyle.THIN);
        style.setBorderLeft(BorderStyle.THIN);
        style.setBorderRight(BorderStyle.THIN);
        return style;
    }
    
    private static CellStyle createNumberStyle(Workbook workbook) {
        CellStyle style = workbook.createCellStyle();
        style.setAlignment(HorizontalAlignment.RIGHT);
        style.setDataFormat(workbook.createDataFormat().getFormat("#,##0.00"));
        return style;
    }
    
    private static Font createBoldFont(Workbook workbook, int size) {
        Font font = workbook.createFont();
        font.setBold(true);
        font.setFontHeightInPoints((short) size);
        return font;
    }
}
