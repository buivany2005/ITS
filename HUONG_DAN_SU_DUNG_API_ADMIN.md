# Hướng dẫn sử dụng API Admin - Hệ thống Thuê Xe

## Địa chỉ Server
- **Base URL**: `http://localhost:8081/api`
- **Port**: 8081
- **Context Path**: /api

## 1. QUẢN LÝ PHƯƠNG TIỆN

### 1.1. Lấy danh sách phương tiện (có phân trang)

**Endpoint:** `GET /api/admin/vehicles`

**Parameters:**
- `page` (optional): Số trang, bắt đầu từ 0. Mặc định: 0
- `size` (optional): Số phương tiện mỗi trang. Mặc định: 12
- `q` (optional): Từ khóa tìm kiếm theo tên xe
- `category` (optional): Loại xe - `OTO`, `XEMAY`, `XEDAP`, `all`

**Ví dụ:**
```
GET /api/admin/vehicles?page=0&size=12
GET /api/admin/vehicles?page=1&size=12&q=vision
GET /api/admin/vehicles?category=XEMAY
```

**Response:**
```json
{
  "vehicles": [
    {
      "id": 1,
      "name": "Honda Vision 2024",
      "category": "XEMAY",
      "brand": "Honda",
      "model": "Vision",
      "year": 2024,
      "licensePlate": "29-G1 123.45",
      "color": "Đen",
      "seats": 2,
      "transmission": "Automatic",
      "fuelType": "Xăng",
      "pricePerDay": 150000,
      "description": "Xe máy Honda Vision mới",
      "thumbnail": "/img/oto/xe_may/Vision.png",
      "status": "AVAILABLE",
      "rating": null,
      "totalRentals": 0,
      "createdAt": "2024-01-15T10:30:00"
    }
  ],
  "currentPage": 0,
  "totalItems": 45,
  "totalPages": 4
}
```

### 1.2. Thêm phương tiện mới

**Endpoint:** `POST /api/admin/vehicles`

**Body:**
```json
{
  "name": "Honda Vision 2024",
  "type": "XEMAY",
  "brand": "Honda",
  "model": "Vision",
  "year": 2024,
  "color": "Đen",
  "licensePlate": "29-G1 123.45",
  "pricePerDay": 150000,
  "description": "Xe máy Honda Vision mới",
  "imageUrl": "/img/oto/xe_may/Vision.png",
  "seats": 2,
  "fuelType": "Xăng",
  "transmission": "Automatic"
}
```

**Response:** Trả về thông tin phương tiện vừa tạo

### 1.3. Cập nhật phương tiện

**Endpoint:** `PUT /api/admin/vehicles/{id}`

**Body:** Giống như thêm mới, có thể thêm trường `status`

### 1.4. Xóa phương tiện

**Endpoint:** `DELETE /api/admin/vehicles/{id}`

### 1.5. Cập nhật trạng thái xe

**Endpoint:** `PATCH /api/admin/vehicles/{id}/status`

**Body:**
```json
{
  "status": "AVAILABLE"
}
```

**Các trạng thái:**
- `AVAILABLE`: Sẵn sàng
- `RENTED`: Đang thuê
- `MAINTENANCE`: Bảo trì
- `UNAVAILABLE`: Không khả dụng

## 2. QUẢN LÝ ĐỐN ĐẶT XE

### 2.1. Lấy danh sách đơn hàng (có phân trang và tìm kiếm)

**Endpoint:** `GET /api/admin/orders`

**Parameters:**
- `page` (optional): Số trang, bắt đầu từ 0. Mặc định: 0
- `size` (optional): Số đơn hàng mỗi trang. Mặc định: 12
- `q` (optional): Tìm kiếm theo tên phương tiện
- `status` (optional): Lọc theo trạng thái - `PENDING`, `CONFIRMED`, `IN_PROGRESS`, `COMPLETED`, `CANCELLED`
- `dateFrom` (optional): Từ ngày (format: yyyy-MM-dd)
- `dateTo` (optional): Đến ngày (format: yyyy-MM-dd)

**Ví dụ:**
```
# Lấy trang đầu tiên
GET /api/admin/orders?page=0&size=12

# Tìm kiếm theo tên xe
GET /api/admin/orders?q=vision

# Lọc theo ngày
GET /api/admin/orders?dateFrom=2024-01-01&dateTo=2024-12-31

# Kết hợp tìm kiếm và lọc
GET /api/admin/orders?page=0&size=12&q=honda&dateFrom=2024-01-01&dateTo=2024-12-31
```

**Response:**
```json
{
  "orders": [
    {
      "id": 1,
      "userName": "Nguyễn Văn A",
      "userEmail": "nguyenvana@email.com",
      "userPhone": "0123456789",
      "vehicleName": "Honda Vision 2024",
      "dateFrom": "2024-01-20",
      "dateTo": "2024-01-25",
      "totalDays": 5,
      "pricePerDay": 150000,
      "totalPrice": 750000,
      "status": "CONFIRMED",
      "customerName": "Nguyễn Văn A",
      "customerPhone": "0123456789",
      "customerEmail": "nguyenvana@email.com",
      "pickupLocation": "Hà Nội",
      "returnLocation": "Hà Nội",
      "notes": "Ghi chú đơn hàng",
      "createdAt": "2024-01-15T10:00:00"
    }
  ],
  "currentPage": 0,
  "totalItems": 89,
  "totalPages": 8
}
```

### 2.2. Lấy thông tin đơn hàng theo ID

**Endpoint:** `GET /api/admin/orders/{id}`

### 2.3. Cập nhật trạng thái đơn hàng

**Endpoint:** `PATCH /api/admin/orders/{id}/status`

**Body:**
```json
{
  "status": "CONFIRMED"
}
```

**Các trạng thái:**
- `PENDING`: Chờ xác nhận
- `CONFIRMED`: Đã xác nhận
- `IN_PROGRESS`: Đang thuê
- `COMPLETED`: Hoàn thành
- `CANCELLED`: Đã hủy

### 2.4. Xuất Excel đơn hàng

**Endpoint:** `GET /api/admin/orders/export`

**Response:** File Excel (.xlsx) chứa danh sách tất cả đơn hàng

**Cách sử dụng trong JavaScript:**
```javascript
async function exportOrders() {
  try {
    const res = await fetch('/api/admin/orders/export');
    if (!res.ok) throw new Error('Export failed');
    
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'don-dat-xe.xlsx';
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  } catch (err) {
    alert('Không thể xuất báo cáo: ' + err.message);
  }
}
```

## 3. THỐNG KÊ & BÁO CÁO

### 3.1. Lấy thống kê tổng quan

**Endpoint:** `GET /api/admin/dashboard`

**Response:**
```json
{
  "vehicles": {
    "totalVehicles": 45,
    "carCount": 20,
    "motorcycleCount": 20,
    "bicycleCount": 5
  },
  "orders": {
    "totalOrders": 150,
    "pendingOrders": 10,
    "confirmedOrders": 15,
    "inProgressOrders": 8,
    "completedOrders": 100,
    "cancelledOrders": 17
  },
  "totalUsers": 200
}
```

### 3.2. Thống kê phương tiện

**Endpoint:** `GET /api/admin/vehicles/stats`

**Response:**
```json
{
  "totalVehicles": 45,
  "carCount": 20,
  "motorcycleCount": 20,
  "bicycleCount": 5
}
```

### 3.3. Thống kê đơn hàng

**Endpoint:** `GET /api/admin/orders/stats`

**Response:**
```json
{
  "totalOrders": 150,
  "pendingOrders": 10,
  "confirmedOrders": 15,
  "inProgressOrders": 8,
  "completedOrders": 100,
  "cancelledOrders": 17
}
```

### 3.4. Xuất Excel báo cáo thống kê

**Endpoint:** `GET /api/admin/stats/export`

**Response:** File Excel (.xlsx) chứa 2 sheet:
1. **Tổng quan**: Thống kê tổng hợp
2. **Chi tiết đơn hàng**: Danh sách tất cả đơn hàng

**Nội dung sheet Tổng quan:**
- Tổng số đơn đặt xe
- Đơn chờ xác nhận
- Đơn đã xác nhận
- Đơn đang thuê
- Đơn hoàn thành
- Đơn đã hủy
- Tổng doanh thu (từ đơn hoàn thành)

**Cách sử dụng:**
```javascript
async function exportStatistics() {
  try {
    const res = await fetch('/api/admin/stats/export');
    if (!res.ok) throw new Error('Export failed');
    
    const blob = await res.blob();
    const url = URL.createObjectURL(blob);
    const a = document.createElement('a');
    a.href = url;
    a.download = 'thong-ke-bao-cao.xlsx';
    document.body.appendChild(a);
    a.click();
    a.remove();
    URL.revokeObjectURL(url);
  } catch (err) {
    alert('Không thể xuất báo cáo: ' + err.message);
  }
}
```

## 4. QUẢN LÝ NGƯỜI DÙNG

### 4.1. Lấy danh sách người dùng

**Endpoint:** `GET /api/admin/users`

### 4.2. Cập nhật role người dùng

**Endpoint:** `PATCH /api/admin/users/{id}/role`

**Body:**
```json
{
  "role": "ADMIN"
}
```

### 4.3. Xóa người dùng

**Endpoint:** `DELETE /api/admin/users/{id}`

## LƯU Ý QUAN TRỌNG

1. **CORS**: Tất cả endpoints đều hỗ trợ CORS
2. **Phân trang mặc định**: page=0, size=12
3. **Sắp xếp**: Tất cả danh sách được sắp xếp theo ngày tạo mới nhất
4. **Format ngày**: yyyy-MM-dd (ví dụ: 2024-01-15)
5. **Encoding**: UTF-8 cho tất cả responses
6. **Excel files**: Tự động đặt tên với timestamp

## XỬ LÝ LỖI

Tất cả endpoints đều trả về lỗi dưới dạng:
```json
{
  "error": "Mô tả lỗi"
}
```

Status codes:
- `200`: Thành công
- `201`: Tạo mới thành công
- `400`: Bad Request (lỗi validation)
- `404`: Không tìm thấy
- `500`: Lỗi server

## VÍ DỤ TÍCH HỢP FRONTEND

### Ví dụ 1: Lấy danh sách xe có phân trang

```javascript
let currentPage = 0;
let totalPages = 0;

async function loadVehicles() {
  const params = new URLSearchParams({
    page: currentPage,
    size: 12
  });
  
  const searchQuery = document.getElementById('search').value;
  if (searchQuery) {
    params.set('q', searchQuery);
  }
  
  const category = document.getElementById('category').value;
  if (category && category !== 'all') {
    params.set('category', category);
  }
  
  try {
    const response = await fetch(`/api/admin/vehicles?${params}`);
    const data = await response.json();
    
    displayVehicles(data.vehicles);
    updatePagination(data.currentPage, data.totalPages);
  } catch (error) {
    console.error('Error loading vehicles:', error);
  }
}

function nextPage() {
  if (currentPage < totalPages - 1) {
    currentPage++;
    loadVehicles();
  }
}

function previousPage() {
  if (currentPage > 0) {
    currentPage--;
    loadVehicles();
  }
}
```

### Ví dụ 2: Tìm kiếm và lọc đơn hàng

```javascript
async function searchOrders() {
  const searchQuery = document.getElementById('search').value;
  const dateFrom = document.getElementById('dateFrom').value;
  const dateTo = document.getElementById('dateTo').value;
  
  const params = new URLSearchParams({
    page: 0,
    size: 12
  });
  
  if (searchQuery) params.set('q', searchQuery);
  if (dateFrom) params.set('dateFrom', dateFrom);
  if (dateTo) params.set('dateTo', dateTo);
  
  try {
    const response = await fetch(`/api/admin/orders?${params}`);
    const data = await response.json();
    
    displayOrders(data.orders);
    updatePagination(data.currentPage, data.totalPages);
  } catch (error) {
    console.error('Error searching orders:', error);
  }
}
```

### Ví dụ 3: Xuất Excel

```javascript
async function exportData(endpoint, filename) {
  try {
    const response = await fetch(`/api/admin/${endpoint}`);
    if (!response.ok) throw new Error('Export failed');
    
    const blob = await response.blob();
    const url = URL.createObjectURL(blob);
    const link = document.createElement('a');
    link.href = url;
    link.download = filename;
    document.body.appendChild(link);
    link.click();
    document.body.removeChild(link);
    URL.revokeObjectURL(url);
    
    alert('Xuất file thành công!');
  } catch (error) {
    alert('Lỗi khi xuất file: ' + error.message);
  }
}

// Sử dụng
exportData('orders/export', 'don-hang.xlsx');
exportData('stats/export', 'thong-ke.xlsx');
```

## HỖ TRỢ

Nếu gặp vấn đề, vui lòng kiểm tra:
1. Server đang chạy trên port 8081
2. Database MySQL đang chạy trên port 3307
3. CORS được bật
4. Request headers đúng format
5. Parameters được encode đúng

Mọi thắc mắc xin liên hệ team phát triển.
