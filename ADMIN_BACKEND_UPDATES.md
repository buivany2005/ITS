# 🔄 CẬP NHẬT BACKEND ADMIN

## ✅ Các API đã bổ sung:

### 1. **GET /api/admin/vehicles/{id}** - Lấy chi tiết xe
**Mục đích:** Sử dụng cho chức năng Edit xe

**Request:**
```bash
GET /api/admin/vehicles/1
```

**Response:**
```json
{
  "id": 1,
  "name": "Honda Vision 2023",
  "category": "XEMAY",
  "brand": "Honda",
  "model": "Vision",
  "year": 2023,
  "licensePlate": "29-G1 123.45",
  "color": "Black",
  "seats": 2,
  "transmission": "Automatic",
  "fuelType": "Gasoline",
  "pricePerDay": 150000,
  "description": "Xe máy Honda Vision 2023 mới",
  "location": "Hà Nội",
  "thumbnail": "/img/oto/xe_may/Vision.png",
  "status": "AVAILABLE",
  "rating": 4.5,
  "totalRentals": 120,
  "createdAt": "2023-10-01T10:00:00"
}
```

---

### 2. **GET /api/admin/orders/{id}** - Lấy chi tiết đơn hàng
**Mục đích:** Sử dụng cho chức năng View chi tiết đơn hàng

**Request:**
```bash
GET /api/admin/orders/1
```

**Response:**
```json
{
  "id": 1,
  "userId": 2,
  "userName": "Nguyễn Văn A",
  "vehicleId": 5,
  "vehicleName": "Honda Vision 2023",
  "vehicleType": "XEMAY",
  "vehicleImageUrl": "/img/oto/xe_may/Vision.png",
  "dateFrom": "2023-10-15",
  "dateTo": "2023-10-18",
  "totalDays": 3,
  "pricePerDay": 150000,
  "totalPrice": 450000,
  "status": "PENDING",
  "pickupLocation": "123 Nguyễn Huệ, Hà Nội",
  "returnLocation": "123 Nguyễn Huệ, Hà Nội",
  "customerName": "Nguyễn Văn A",
  "customerPhone": "0901234567",
  "customerEmail": "nguyenvana@gmail.com",
  "notes": "Ghi chú từ khách hàng",
  "createdAt": "2023-10-10T14:30:00"
}
```

---

## 📋 DANH SÁCH ĐẦY ĐỦ API ADMIN:

### **🚗 Quản lý xe (Vehicles)**

| Method | Endpoint | Chức năng | Status |
|--------|----------|-----------|--------|
| GET | `/api/admin/vehicles` | Lấy danh sách xe (có filter & search) | ✅ |
| GET | `/api/admin/vehicles/{id}` | Lấy chi tiết 1 xe | ✅ NEW |
| POST | `/api/admin/vehicles` | Tạo xe mới | ✅ |
| PUT | `/api/admin/vehicles/{id}` | Cập nhật xe | ✅ |
| DELETE | `/api/admin/vehicles/{id}` | Xóa xe | ✅ |
| PATCH | `/api/admin/vehicles/{id}/status` | Cập nhật trạng thái xe | ✅ |
| GET | `/api/admin/vehicles/stats` | Thống kê xe | ✅ |

### **📦 Quản lý đơn hàng (Orders)**

| Method | Endpoint | Chức năng | Status |
|--------|----------|-----------|--------|
| GET | `/api/admin/orders` | Lấy danh sách đơn hàng (có filter) | ✅ |
| GET | `/api/admin/orders/{id}` | Lấy chi tiết 1 đơn hàng | ✅ NEW |
| PATCH | `/api/admin/orders/{id}/status` | Cập nhật trạng thái đơn | ✅ |
| GET | `/api/admin/orders/stats` | Thống kê đơn hàng | ✅ |
| GET | `/api/admin/orders/export` | Xuất báo cáo Excel | ✅ |

### **👥 Quản lý người dùng (Users)**

| Method | Endpoint | Chức năng | Status |
|--------|----------|-----------|--------|
| GET | `/api/admin/users` | Lấy danh sách users | ✅ |
| PATCH | `/api/admin/users/{id}/role` | Cập nhật role user | ✅ |
| DELETE | `/api/admin/users/{id}` | Xóa user | ✅ |

### **📊 Dashboard**

| Method | Endpoint | Chức năng | Status |
|--------|----------|-----------|--------|
| GET | `/api/admin/dashboard` | Tổng hợp thống kê | ✅ |

---

## 🧪 HƯỚNG DẪN TEST CÁC API MỚI:

### Test API lấy chi tiết xe:
```bash
# Windows CMD
curl http://localhost:8080/api/admin/vehicles/1

# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/vehicles/1" -Method GET
```

### Test API lấy chi tiết đơn hàng:
```bash
# Windows CMD
curl http://localhost:8080/api/admin/orders/1

# PowerShell
Invoke-RestMethod -Uri "http://localhost:8080/api/admin/orders/1" -Method GET
```

---

## 🔧 CÁC FILE ĐÃ CHỈNH SỬA:

### 1. **backend/src/main/java/com/example/backend/controller/AdminController.java**
- ✅ Thêm endpoint `GET /api/admin/vehicles/{id}`
- ✅ Thêm endpoint `GET /api/admin/orders/{id}`

---

## 🚀 CÁCH KHỞI ĐỘNG LẠI:

### Nếu đang chạy Docker:
```bash
cd docker
docker-compose restart backend
```

### Nếu chạy Local (Maven):
```bash
# Dừng backend đang chạy (Ctrl + C)
# Chạy lại
mvn spring-boot:run
```

---

## ✅ CHECKLIST CÁC CHỨC NĂNG ADMIN:

### Trang Quản lý Đơn hàng:
- ✅ Xem danh sách đơn hàng
- ✅ Lọc theo trạng thái (Chờ duyệt, Đã duyệt, Đang thuê, Hoàn thành, Đã hủy)
- ✅ Tìm kiếm đơn hàng
- ✅ Cập nhật trạng thái đơn hàng (dropdown)
- ✅ **Xem chi tiết đơn hàng (button "Xem")** - API mới
- ✅ Xuất báo cáo Excel

### Trang Quản lý Xe:
- ✅ Xem danh sách xe
- ✅ Lọc theo loại (Tất cả, Xe máy, Ô tô, Xe đạp)
- ✅ Tìm kiếm xe
- ✅ **Xem/Sửa chi tiết xe (button "Edit")** - API mới
- ✅ Xóa xe (button "Delete")

---

## 📝 GHI CHÚ:

### Chức năng "Edit" xe:
Frontend hiện tại chỉ alert "Chưa được triển khai". Để kích hoạt:
1. API backend đã sẵn sàng: `GET /api/admin/vehicles/{id}`
2. Cần tạo trang edit vehicle HTML hoặc modal edit
3. Sử dụng API `PUT /api/admin/vehicles/{id}` để update

### Chức năng "View" đơn hàng:
Frontend hiện navigate đến `chi_tiet_xe.html?orderId={id}`. Có thể:
1. Sử dụng API: `GET /api/admin/orders/{id}`
2. Hiển thị thông tin chi tiết trong modal hoặc trang riêng

---

**Cập nhật: $(date)**
**Tất cả API admin đã hoàn thiện! 🎉**
