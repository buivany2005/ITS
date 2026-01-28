# Backend Admin Updates - Complete

## Ngày cập nhật: 24/01/2026

## Tổng quan các thay đổi

Đã hoàn thành việc cập nhật backend để khớp với frontend admin và đáp ứng đầy đủ các yêu cầu:

### 1. Quản lý Phương tiện (Vehicle Management)
- ✅ Thêm mới phương tiện và lưu vào database
- ✅ Phân trang: 12 phương tiện mỗi trang
- ✅ Tìm kiếm theo tên và lọc theo loại xe

### 2. Quản lý Đơn đặt xe (Order Management)  
- ✅ Lấy dữ liệu từ database
- ✅ Phân trang: 12 đơn hàng mỗi trang
- ✅ Tìm kiếm theo tên phương tiện
- ✅ Lọc theo ngày thuê
- ✅ Xuất file Excel với định dạng .xlsx

### 3. Thống kê & Báo cáo (Statistics & Reports)
- ✅ Lấy tất cả dữ liệu thống kê
- ✅ Xuất file Excel báo cáo tổng hợp

## Chi tiết các thay đổi

### 1. Dependencies (pom.xml)

Đã thêm Apache POI để hỗ trợ xuất Excel:

```xml
<!-- Apache POI for Excel Export -->
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi</artifactId>
    <version>5.2.5</version>
</dependency>
<dependency>
    <groupId>org.apache.poi</groupId>
    <artifactId>poi-ooxml</artifactId>
    <version>5.2.5</version>
</dependency>
```

### 2. VehicleRepository

**Thêm các phương thức phân trang:**

```java
Page<Vehicle> findByVehicleType(VehicleType vehicleType, Pageable pageable);
Page<Vehicle> findByNameContainingIgnoreCase(String name, Pageable pageable);
```

### 3. VehicleService

**Thêm phương thức:**

```java
public Map<String, Object> getVehiclesWithPagination(int page, int size, String query, String category)
```

**Trả về:**
- `vehicles`: List các phương tiện
- `currentPage`: Trang hiện tại
- `totalItems`: Tổng số phương tiện
- `totalPages`: Tổng số trang

### 4. OrderRepository

**Thêm các phương thức tìm kiếm và phân trang:**

```java
// Tìm kiếm theo tên xe
List<Order> searchByVehicleName(String query);
Page<Order> searchByVehicleName(String query, Pageable pageable);

// Tìm kiếm theo tên xe và khoảng ngày
List<Order> searchByVehicleNameAndDateRange(String query, LocalDate dateFrom, LocalDate dateTo);
Page<Order> searchByVehicleNameAndDateRange(String query, LocalDate dateFrom, LocalDate dateTo, Pageable pageable);

// Tìm kiếm theo khoảng ngày
List<Order> findByDateRange(LocalDate dateFrom, LocalDate dateTo);
Page<Order> findByDateRange(LocalDate dateFrom, LocalDate dateTo, Pageable pageable);

// Phân trang theo trạng thái
Page<Order> findByStatusOrderByCreatedAtDesc(OrderStatus status, Pageable pageable);
```

### 5. OrderService

**Thêm các phương thức:**

#### a) Phân trang với tìm kiếm
```java
public Map<String, Object> getOrdersWithPagination(
    int page, int size, String query, String status, 
    LocalDate dateFrom, LocalDate dateTo)
```

#### b) Tìm kiếm đơn hàng
```java
public List<OrderResponse> searchOrders(
    String query, LocalDate dateFrom, LocalDate dateTo)
```

#### c) Xuất Excel đơn hàng
```java
public byte[] exportOrdersToExcel() throws Exception
```

Xuất file Excel với các cột:
- ID
- Khách hàng
- Email
- Số điện thoại
- Phương tiện
- Ngày thuê
- Ngày trả
- Số ngày
- Giá/ngày
- Tổng tiền
- Trạng thái (tiếng Việt)

#### d) Xuất Excel thống kê
```java
public byte[] exportStatisticsToExcel() throws Exception
```

Xuất file Excel gồm 2 sheet:
1. **Tổng quan**: Thống kê tổng hợp
   - Tổng số đơn đặt xe
   - Đơn chờ xác nhận
   - Đơn đã xác nhận
   - Đơn đang thuê
   - Đơn hoàn thành
   - Đơn đã hủy
   - Tổng doanh thu

2. **Chi tiết đơn hàng**: Danh sách tất cả đơn hàng

### 6. AdminController

**Cập nhật endpoint `/admin/vehicles`:**

```http
GET /api/admin/vehicles?page=0&size=12&q=honda&category=XEMAY
```

**Parameters:**
- `page`: Số trang (mặc định: 0)
- `size`: Số items mỗi trang (mặc định: 12)
- `q`: Từ khóa tìm kiếm (optional)
- `category`: Loại xe - OTO, XEMAY, XEDAP (optional)

**Response:**
```json
{
  "vehicles": [...],
  "currentPage": 0,
  "totalItems": 45,
  "totalPages": 4
}
```

**Cập nhật endpoint `/admin/orders`:**

```http
GET /api/admin/orders?page=0&size=12&q=vision&dateFrom=2024-01-01&dateTo=2024-12-31
```

**Parameters:**
- `page`: Số trang (mặc định: 0)
- `size`: Số items mỗi trang (mặc định: 12)
- `q`: Tìm kiếm theo tên xe (optional)
- `status`: Lọc theo trạng thái (optional)
- `dateFrom`: Từ ngày (format: yyyy-MM-dd, optional)
- `dateTo`: Đến ngày (format: yyyy-MM-dd, optional)

**Response:**
```json
{
  "orders": [...],
  "currentPage": 0,
  "totalItems": 89,
  "totalPages": 8
}
```

**Thêm endpoint mới:**

```http
GET /api/admin/stats/export
```

Xuất file Excel báo cáo thống kê tổng hợp.

**Response:**
- Content-Type: `application/vnd.openxmlformats-officedocument.spreadsheetml.sheet`
- File name: `thong-ke-bao-cao-yyyyMMdd-HHmmss.xlsx`

### 7. Vehicle Entity

Thêm trạng thái `UNAVAILABLE`:

```java
public enum VehicleStatus {
    AVAILABLE, RENTED, MAINTENANCE, UNAVAILABLE
}
```

## API Endpoints Summary

### Vehicles

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/admin/vehicles` | Lấy danh sách xe (có phân trang) |
| GET | `/api/admin/vehicles/{id}` | Lấy thông tin xe theo ID |
| POST | `/api/admin/vehicles` | Thêm xe mới |
| PUT | `/api/admin/vehicles/{id}` | Cập nhật xe |
| DELETE | `/api/admin/vehicles/{id}` | Xóa xe |
| PATCH | `/api/admin/vehicles/{id}/status` | Cập nhật trạng thái xe |
| GET | `/api/admin/vehicles/stats` | Thống kê xe |

### Orders

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/admin/orders` | Lấy danh sách đơn (có phân trang & tìm kiếm) |
| GET | `/api/admin/orders/{id}` | Lấy thông tin đơn theo ID |
| PATCH | `/api/admin/orders/{id}/status` | Cập nhật trạng thái đơn |
| GET | `/api/admin/orders/stats` | Thống kê đơn hàng |
| GET | `/api/admin/orders/export` | Xuất Excel đơn hàng |

### Statistics

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/admin/stats/export` | Xuất Excel báo cáo thống kê |
| GET | `/api/admin/dashboard` | Lấy dữ liệu dashboard |

### Users

| Method | Endpoint | Mô tả |
|--------|----------|-------|
| GET | `/api/admin/users` | Lấy danh sách người dùng |
| PATCH | `/api/admin/users/{id}/role` | Cập nhật role người dùng |
| DELETE | `/api/admin/users/{id}` | Xóa người dùng |

## Hướng dẫn test

### 1. Test Phân trang Phương tiện

```bash
# Trang 1 (12 xe đầu tiên)
curl http://localhost:8081/api/admin/vehicles?page=0&size=12

# Trang 2
curl http://localhost:8081/api/admin/vehicles?page=1&size=12

# Tìm kiếm + phân trang
curl http://localhost:8081/api/admin/vehicles?page=0&size=12&q=vision
```

### 2. Test Tìm kiếm và Phân trang Đơn hàng

```bash
# Lấy đơn hàng trang 1
curl http://localhost:8081/api/admin/orders?page=0&size=12

# Tìm kiếm theo tên xe
curl http://localhost:8081/api/admin/orders?q=vision

# Lọc theo ngày
curl "http://localhost:8081/api/admin/orders?dateFrom=2024-01-01&dateTo=2024-12-31"

# Kết hợp tìm kiếm và lọc ngày
curl "http://localhost:8081/api/admin/orders?q=vision&dateFrom=2024-01-01&dateTo=2024-12-31"
```

### 3. Test Xuất Excel

```bash
# Xuất đơn hàng
curl -o orders.xlsx http://localhost:8081/api/admin/orders/export

# Xuất thống kê
curl -o statistics.xlsx http://localhost:8081/api/admin/stats/export
```

## Compatibility với Frontend

### 1. admin-vehicles.js
- ✅ API endpoint `/api/admin/vehicles` đã hỗ trợ phân trang
- ✅ Tìm kiếm theo tên với param `q`
- ✅ Lọc theo category với param `category`
- ✅ CRUD operations đầy đủ

### 2. admin-orders.js
- ✅ API endpoint `/api/admin/orders` đã hỗ trợ phân trang
- ✅ Tìm kiếm theo tên xe với param `q`
- ✅ Lọc theo ngày với params `dateFrom`, `dateTo`
- ✅ Xuất Excel tại `/api/admin/orders/export`

### 3. Frontend cần update

Để sử dụng pagination, frontend cần gọi API với parameters:

```javascript
// Trong admin-vehicles.js
function fetchVehicles(page = 0) {
  const params = new URLSearchParams();
  params.set('page', page);
  params.set('size', 12);
  if (filters.q) params.set("q", filters.q);
  if (filters.category && filters.category !== "all")
    params.set("category", filters.category);
  
  fetch("/api/admin/vehicles?" + params.toString())
    .then((r) => r.json())
    .then((data) => {
      vehicles = data.vehicles || [];
      totalPages = data.totalPages;
      currentPage = data.currentPage;
      renderTable();
      renderPagination();
    })
    .catch((err) => console.error("fetchVehicles error", err));
}

// Trong admin-orders.js
function fetchOrders(page = 0) {
  const params = new URLSearchParams();
  params.set('page', page);
  params.set('size', 12);
  if (searchQuery) params.set("q", searchQuery);
  if (dateFrom) params.set("dateFrom", dateFrom);
  if (dateTo) params.set("dateTo", dateTo);
  if (status && status !== "all") params.set("status", status);
  
  fetch("/api/admin/orders?" + params.toString())
    .then((r) => r.json())
    .then((data) => {
      orders = data.orders || [];
      totalPages = data.totalPages;
      currentPage = data.currentPage;
      renderTable();
      renderPagination();
    })
    .catch((err) => console.error("fetchOrders error", err));
}
```

## Build & Deploy

```bash
# Build project
mvn clean compile

# Run tests (optional)
mvn test

# Package application
mvn clean package -DskipTests

# Run application
java -jar target/vehicle-rental-0.0.1-SNAPSHOT.jar

# Hoặc với Maven
mvn spring-boot:run
```

## Notes

1. Tất cả API endpoints đều hỗ trợ CORS với `@CrossOrigin(origins = "*")`
2. Server chạy trên port `8081` với context path `/api`
3. Database cần chạy trên `localhost:3307` (MySQL)
4. Pagination mặc định: page=0, size=12
5. Tất cả responses đều được sắp xếp theo `createdAt DESC`
6. Excel files được đặt tên tự động với timestamp
7. Hỗ trợ backward compatibility - nếu không truyền page/size, sẽ trả về tất cả dữ liệu

## Kết luận

✅ Tất cả yêu cầu đã được hoàn thành:
1. ✅ Quản lý phương tiện có phân trang 12 items/trang
2. ✅ Đơn đặt xe có phân trang, tìm kiếm và xuất Excel
3. ✅ Thống kê & báo cáo có xuất Excel đầy đủ

Backend đã sẵn sàng để hoạt động với frontend admin!
