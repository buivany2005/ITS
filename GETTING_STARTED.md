# 🚀 HƯỚNG DẪN CHẠY DỰ ÁN VEHICLE RENTAL

> **Tài liệu này hướng dẫn từng bước để chạy dự án Vehicle Rental System**

---

## 📋 YÊU CẦU TRƯỚC KHI BẮT ĐẦU

| Phần mềm           | Phiên bản | Bắt buộc            |
| ------------------ | --------- | ------------------- |
| **Docker Desktop** | 20.10+    | ✅                  |
| **Java JDK**       | 17+       | ✅ (nếu chạy local) |
| **Maven**          | 3.6+      | ✅ (nếu chạy local) |
| **VS Code**        | Latest    | Khuyến nghị         |
| **Live Server**    | Extension | Khuyến nghị cho FE  |

---

## 🔵 CÁCH 1: CHẠY VỚI DOCKER (ĐƠN GIẢN NHẤT)

### Bước 1: Khởi động Docker Desktop

Mở Docker Desktop và đợi cho đến khi nó chạy hoàn toàn (icon Docker ở taskbar phải ổn định).

### Bước 2: Mở terminal và chạy lệnh

```bash
cd /c/project-tuhoc/ITS/Vehicle_rental/docker
docker-compose up -d --build

```

### Bước 3: Đợi build hoàn tất

- Lần đầu tiên: **3-5 phút**
- Các lần sau: **30 giây - 1 phút**

Kiểm tra trạng thái:

```bash
docker-compose ps
```

Tất cả services phải có status **Up** hoặc **Healthy**.

### Bước 4: Truy cập ứng dụng

| Trang           | URL                                    |
| --------------- | -------------------------------------- |
| 🏠 Trang chủ    | http://localhost/home/index.html       |
| 🚗 Danh sách xe | http://localhost/use/danh_sach_xe.html |
| 🔧 API Backend  | http://localhost:8080/api/vehicles     |
| 👤 Admin        | http://localhost/admin/quan_ly_xe.html |
| 🗄️ phpMyAdmin   | http://localhost:5050                  |

**Thông tin đăng nhập phpMyAdmin:**

- User: `root`
- Password: `rootpass`

---

## 🟢 CÁCH 2: CHẠY THỦ CÔNG (CHO DEVELOPMENT)

> Cách này phù hợp khi bạn muốn debug hoặc phát triển code.

### Bước 1️⃣: Khởi động Database (Docker)

Mở terminal và chạy:

```bash
cd c:\project-tuhoc\ITS\Vehicle_rental
docker-compose -f docker/docker-compose.yml up -d db
```

**Đợi khoảng 30 giây** để MySQL khởi động hoàn tất.

Kiểm tra database đã chạy:

```bash
docker ps
```

Phải thấy container `vehiclerental-db` với status **Up**.

---

### Bước 2️⃣: Set JAVA_HOME

#### Windows CMD:

```cmd
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
```

#### Git Bash / VS Code Terminal:

```bash
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-17.0.15.6-hotspot"
```

#### PowerShell:

```powershell
$env:JAVA_HOME = "C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
```

---

### Bước 3️⃣: Chạy Backend Spring Boot

#### Cách A - Chạy từng lệnh:

```bash
cd c:\project-tuhoc\ITS\Vehicle_rental
mvn spring-boot:run -Dspring-boot.run.mainClass=com.example.backend.VehicleRentalApplication
```

#### Cách B - Chạy 1 lệnh duy nhất (Git Bash):

```bash
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-17.0.15.6-hotspot" && \
cd /c/project-tuhoc/ITS/Vehicle_rental && \
mvn spring-boot:run -Dspring-boot.run.mainClass=com.example.backend.VehicleRentalApplication
```

#### Cách C - Sử dụng VS Code Task:

1. Nhấn `Ctrl + Shift + P`
2. Gõ "Tasks: Run Task"
3. Chọn **"Backend: Run"**

> ⚠️ **Lưu ý:** Cách này yêu cầu JAVA_HOME đã được set trong system environment.

**Đợi cho đến khi thấy log:**

```
Started VehicleRentalApplication in X.XXX seconds
```

---

### Bước 4️⃣: Chạy Frontend

#### Cách A - Sử dụng VS Code Live Server (Khuyến nghị):

1. Mở VS Code trong thư mục project
2. Cài extension **Live Server** (nếu chưa có)
3. Mở file `frontend/home/index.html`
4. Click chuột phải → **Open with Live Server**
5. Trình duyệt sẽ tự động mở tại http://127.0.0.1:5500

#### Cách B - Sử dụng Python HTTP Server:

```bash
cd c:\project-tuhoc\ITS\Vehicle_rental\frontend
python -m http.server 3000
```

Truy cập: http://localhost:3000/home/index.html

#### Cách C - Sử dụng Node.js (http-server):

```bash
npx http-server frontend -p 3000
```

---

### Bước 5️⃣: Truy cập ứng dụng

| Trang               | URL (Live Server)                                    |
| ------------------- | ---------------------------------------------------- |
| 🏠 Trang chủ        | http://127.0.0.1:5500/home/index.html                |
| 🚗 Danh sách xe     | http://127.0.0.1:5500/use/danh_sach_xe.html          |
| 📋 Chi tiết xe      | http://127.0.0.1:5500/use/chi_tiet_xe.html?id=1      |
| 🛒 Đặt xe           | http://127.0.0.1:5500/use/dat_xe.html                |
| 💳 Thanh toán       | http://127.0.0.1:5500/pay/pay.html                   |
| 👤 Quản lý đơn hàng | http://127.0.0.1:5500/use/quan_ly_xe.html            |
| 🔧 Admin - Xe       | http://127.0.0.1:5500/admin/quan_ly_phuong_tien.html |
| 🔧 Admin - Đơn hàng | http://127.0.0.1:5500/admin/quan_ly_don_hang.html    |
| 🔐 Đăng nhập        | http://127.0.0.1:5500/login/login.html               |

**API Backend:** http://localhost:8081/api/vehicles

---

## 🔴 DỪNG ỨNG DỤNG

### Dừng Backend:

Nhấn `Ctrl + C` trong terminal đang chạy Spring Boot.

### Dừng Database Docker:

```bash
docker-compose -f docker/docker-compose.yml stop db
```

### Dừng tất cả Docker containers:

```bash
docker-compose -f docker/docker-compose.yml down
```

### Xóa tất cả data (reset database):

```bash
docker-compose -f docker/docker-compose.yml down -v
```

---

## 🐛 XỬ LÝ LỖI THƯỜNG GẶP

### Lỗi 1: JAVA_HOME not defined

```
The JAVA_HOME environment variable is not defined correctly
```

**Cách fix:**

```bash
# Git Bash
export JAVA_HOME="/c/Program Files/Eclipse Adoptium/jdk-17.0.15.6-hotspot"

# CMD
set "JAVA_HOME=C:\Program Files\Eclipse Adoptium\jdk-17.0.15.6-hotspot"
```

---

### Lỗi 2: Port 8081 already in use

```
Web server failed to start. Port 8081 was already in use.
```

**Cách fix:**

```bash
# Tìm process đang chiếm port
netstat -ano | findstr :8081

# Kill process (thay <PID> bằng số PID tìm được)
taskkill //F //PID <PID>
```

---

### Lỗi 3: Connection refused to MySQL

```
Communications link failure
```

**Cách fix:**

1. Kiểm tra Docker container database đang chạy:

```bash
docker ps
```

2. Nếu không thấy `vehiclerental-db`, chạy lại:

```bash
docker-compose -f docker/docker-compose.yml up -d db
```

3. Đợi 30 giây rồi thử lại.

---

### Lỗi 4: Access denied for user

```
Access denied for user 'root'@'localhost'
```

**Cách fix:**

Kiểm tra file `application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/vehiclerental
spring.datasource.username=root
spring.datasource.password=rootpass
```

---

### Lỗi 5: Frontend không gọi được API

**Cách fix:**

1. Kiểm tra backend đang chạy ở port 8081
2. Kiểm tra file `frontend/js/api.js`:

```javascript
const API_BASE_URL = "http://localhost:8081/api";
```

3. Kiểm tra CORS đã được config trong backend.

---

## 📊 THÔNG TIN CẤU HÌNH

| Thành phần               | Giá trị                             |
| ------------------------ | ----------------------------------- |
| **Database Host**        | localhost:3307                      |
| **Database Name**        | vehiclerental                       |
| **DB Username**          | root                                |
| **DB Password**          | rootpass                            |
| **Backend Port**         | 8081                                |
| **Backend Context Path** | /api                                |
| **Frontend Port**        | 5500 (Live Server) hoặc 80 (Docker) |

---

## 📁 CẤU TRÚC THƯ MỤC

```
Vehicle_rental/
├── backend/
│   └── src/main/java/com/example/backend/
│       ├── controller/     # REST Controllers
│       ├── entity/         # JPA Entities
│       ├── repository/     # Data Repositories
│       ├── service/        # Business Logic
│       ├── dto/            # Data Transfer Objects
│       └── config/         # Configuration
├── frontend/
│   ├── home/               # Trang chủ
│   ├── login/              # Đăng nhập
│   ├── use/                # Trang người dùng
│   ├── pay/                # Thanh toán
│   ├── admin/              # Trang admin
│   └── js/                 # JavaScript files
├── docker/
│   ├── docker-compose.yml  # Docker config
│   └── init-db.sql         # Database schema
└── img/                    # Hình ảnh
```

---

## 🔗 API ENDPOINTS

### Authentication

| Method | Endpoint             | Mô tả     |
| ------ | -------------------- | --------- |
| POST   | `/api/auth/login`    | Đăng nhập |
| POST   | `/api/auth/register` | Đăng ký   |

### Vehicles

| Method | Endpoint                    | Mô tả            |
| ------ | --------------------------- | ---------------- |
| GET    | `/api/vehicles`             | Lấy tất cả xe    |
| GET    | `/api/vehicles/{id}`        | Lấy chi tiết xe  |
| GET    | `/api/vehicles/type/{type}` | Lấy xe theo loại |

### Orders (User)

| Method | Endpoint                  | Mô tả                 |
| ------ | ------------------------- | --------------------- |
| GET    | `/api/orders/my-orders`   | Lấy đơn hàng của user |
| POST   | `/api/orders`             | Tạo đơn hàng mới      |
| POST   | `/api/orders/{id}/cancel` | Hủy đơn hàng          |

### Admin

| Method | Endpoint                        | Mô tả                   |
| ------ | ------------------------------- | ----------------------- |
| GET    | `/api/admin/vehicles`           | Lấy tất cả xe (admin)   |
| POST   | `/api/admin/vehicles`           | Thêm xe mới             |
| PUT    | `/api/admin/vehicles/{id}`      | Cập nhật xe             |
| DELETE | `/api/admin/vehicles/{id}`      | Xóa xe                  |
| GET    | `/api/admin/orders`             | Lấy tất cả đơn hàng     |
| PATCH  | `/api/admin/orders/{id}/status` | Cập nhật trạng thái đơn |
| GET    | `/api/admin/dashboard`          | Lấy thống kê dashboard  |

---

## ✅ CHECKLIST KHỞI ĐỘNG NHANH

- [ ] Docker Desktop đang chạy
- [ ] Database container đã start (`docker ps` thấy `vehiclerental-db`)
- [ ] JAVA_HOME đã được set
- [ ] Backend đang chạy (thấy log "Started VehicleRentalApplication")
- [ ] Frontend đang serve (Live Server hoặc http-server)
- [ ] Truy cập http://localhost:8081/api/vehicles trả về JSON

---

_Cập nhật lần cuối: 17/01/2026_
