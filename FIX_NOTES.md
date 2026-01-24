# 🔧 FIX NOTES - Backend Admin Updates

## ✅ Các Fixes Đã Thực Hiện

### 1. **FIX LỖI "Bad Request" Khi Thêm Phương Tiện**

**Vấn đề**: Regex validation biển số xe quá chặt
- Regex cũ: `\\d{2}[A-Z]-\\d+\\.\\d+` (yêu cầu format như `29-G1 123.45`)
- Regex mới: `^[0-9]{2,3}[A-Z]-\\d{4,6}$` (chấp nhận format như `79A-27289`)

**File thay đổi**: [backend/src/main/java/com/example/backend/dto/VehicleRequest.java](backend/src/main/java/com/example/backend/dto/VehicleRequest.java)

**Test**: Thêm phương tiện với biển số `79A-27289` ✓

---

### 2. **FIX FILE EXPORT QUẢN LÝ KHÁCH HÀNG**

**Vấn đề**: Khi export file CSV, các cột bị gộp lại thành một dòng
- Cũ: Các cột không được tách riêng

**Giải pháp**: 
- Thêm proper CSV escaping (escape quotes, wrap fields)
- Đảm bảo mỗi row trên một dòng riêng
- Escapehtml để xử lý special characters

**File thay đổi**: [frontend/js/admin-users.js](frontend/js/admin-users.js)

**Test**: Export file quản lý khách hàng → Mở trong Excel → Kiểm tra các cột ✓

---

## 📊 API ENDPOINTS - QUY TRÌNH TEST

### 1. **Thêm Phương Tiện Mới**
```bash
POST http://localhost:8081/api/admin/vehicles
Content-Type: application/json

{
  "name": "Honda Vision 2023",
  "type": "XEMAY",
  "brand": "Honda",
  "model": "Vision",
  "year": 2023,
  "color": "Đen",
  "licensePlate": "79A-27289",
  "pricePerDay": 150000,
  "seats": 2,
  "fuelType": "Xăng",
  "transmission": "Automatic",
  "description": "Xe máy Honda mới",
  "imageUrl": "/img/oto/xe_may/Vision.png"
}
```
✓ Kỳ vọng: HTTP 201, trả về vehicle object

---

### 2. **Lấy Danh Sách Phương Tiện (Phân Trang)**
```bash
GET http://localhost:8081/api/admin/vehicles?page=0&size=12&q=&category=all
```
✓ Kỳ vọng: HTTP 200, danh sách 12 vehicles/trang

---

### 3. **Export Phương Tiện**
```bash
GET http://localhost:8081/api/admin/vehicles/export
```
✓ Kỳ vọng: HTTP 200, file Excel `danh-sach-xe-*.xlsx`

---

### 4. **Export Quản Lý Khách Hàng (Frontend)**
- Vào trang: http://localhost/admin/quan_ly_khach_hang.html
- Click nút "Xuất Excel" 
- ✓ Kiểm tra file CSV: các cột phải riêng biệt (ID | Họ tên | Email | Số điện thoại | Vai trò)

---

### 5. **Export Đơn Hàng**
```bash
GET http://localhost:8081/api/admin/orders/export
```
✓ Kỳ vọng: HTTP 200, file Excel `don-dat-xe-*.xlsx`

---

### 6. **Export Thống Kê**
```bash
GET http://localhost:8081/api/admin/stats/export
```
✓ Kỳ vọng: HTTP 200, file Excel `thong-ke-bao-cao-*.xlsx`

---

## 🌐 URLS TRUY CẬP

| Trang | URL |
|-------|-----|
| 🏠 Trang chủ | http://localhost/home/index.html |
| 👥 Quản lý khách hàng | http://localhost/admin/quan_ly_khach_hang.html |
| 🚗 Quản lý phương tiện | http://localhost/admin/quan_ly_xe.html |
| 📦 Quản lý đơn hàng | http://localhost/admin/quan_ly_don_hang.html |
| 📊 Thống kê | http://localhost/admin/thong_ke.html |
| 🛠️ PHPMyAdmin | http://localhost:8080 |

---

## ✨ VALIDATION RULES - PHƯƠNG TIỆN

| Field | Rule |
|-------|------|
| Tên xe | 3-100 ký tự |
| Loại xe | OTO \| XEMAY \| XEDAP |
| Hãng xe | 2-50 ký tự |
| Model | 1-50 ký tự |
| Năm | 2000-2030 |
| Biển số | Regex: `^[0-9]{2,3}[A-Z]-\d{4,6}$` (e.g: 79A-27289) |
| Giá thuê/ngày | 1.000 - 1.000.000 VNĐ |
| Chỗ ngồi | 1-50 |
| Mô tả | ≤500 ký tự |
| URL hình | ≤255 ký tự |

---

## 🐛 TROUBLESHOOTING

**Nếu "Bad Request" khi thêm phương tiện:**
- Kiểm tra biển số format: `79A-27289` hoặc `29A-12345`
- Kiểm tra giá: từ 1.000 đến 1.000.000
- Kiểm tra năm: 2000-2030

**Nếu file export không đúng:**
- Excel: Các cột phải riêng riêng
- CSV: Dùng delimiter là dấu phẩy `,`
- Encoding: UTF-8 with BOM (`\ufeff`)

---

## 🚀 BUILD & DEPLOY

```bash
# Build backend
mvn clean package -DskipTests

# Start Docker
cd docker
docker-compose up -d --build

# Check logs
docker logs vehiclerental-backend
```

---

**Ngày update**: 24/01/2026  
**Status**: ✅ Ready for Testing
