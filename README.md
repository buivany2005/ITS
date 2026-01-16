# 🚗 Vehicle Rental System - Hệ thống cho thuê phương tiện

Hệ thống quản lý và cho thuê phương tiện (ô tô, xe máy, xe đạp) với giao diện web hiện đại.

## 📋 Công nghệ sử dụng

### Backend

- **Java 17** - Spring Boot
- **MySQL 8.0** - Database
- **Maven** - Build tool
- **Docker** - Containerization

### Frontend

- **HTML5, CSS3, JavaScript**
- **TailwindCSS** - UI Framework
- **Nginx** - Web Server

### Database Management

- **phpMyAdmin** - Web interface for MySQL

## 🎯 Tính năng

- ✅ Xem danh sách phương tiện
- ✅ Lọc theo loại xe (Ô tô, Xe máy, Xe đạp)
- ✅ Tìm kiếm phương tiện
- ✅ Xem chi tiết xe
- ✅ Đặt thuê xe
- ✅ Quản lý đơn hàng (Admin)
- ✅ Quản lý phương tiện (Admin)

## 🚀 Hướng dẫn cài đặt và chạy

### Yêu cầu hệ thống

- **Docker Desktop** 20.10+
- **Docker Compose** 2.0+
- 4GB RAM khả dụng
- 10GB dung lượng ổ cứng

### Cách 1: Chạy với Docker (Khuyến nghị)

#### Bước 1: Clone project

```bash
git clone <repository-url>
cd Vehicle_rental
```

#### Bước 2: Khởi động Docker Desktop

Đảm bảo Docker Desktop đang chạy trên máy của bạn.

#### Bước 3: Build và chạy containers

```bash
cd docker
docker-compose up -d --build
```

Quá trình build lần đầu sẽ mất 3-5 phút.

#### Bước 4: Kiểm tra trạng thái

```bash
docker-compose ps
```

Tất cả services phải có status **Up** hoặc **Healthy**:

- ✅ `vehiclerental-db` - Database (MySQL 8.0)
- ✅ `vehiclerental-backend` - API Server (Spring Boot)
- ✅ `vehiclerental-frontend` - Web Server (Nginx)
- ✅ `vehiclerental-phpmyadmin` - phpMyAdmin (Database Management)

#### Bước 5: Truy cập ứng dụng

Mở trình duyệt và truy cập:

- **Trang chủ**: http://localhost/home/index.html
- **Danh sách xe**: http://localhost/use/danh_sach_xe.html
- **Backend API**: http://localhost:8080/api/vehicles
- **Admin**: http://localhost/admin/quan_ly_xe.html
- **phpMyAdmin**: http://localhost:5050 (user: `root`, pass: `rootpass`)

### Cách 2: Chạy không dùng Docker

#### Yêu cầu:

- **Java 17** JDK
- **Maven** 3.6+
- **MySQL** 8.0+
- **Web Server** (Live Server, Python SimpleHTTPServer, hoặc Nginx)

**⚠️ Lưu ý:** Nếu bạn chỉ muốn chạy backend để phát triển, bạn có thể dùng Docker cho database và chỉ chạy backend locally. Xem **Cách 2b** bên dưới.

#### Bước 1: Cài đặt và cấu hình MySQL

**Windows:**

1. Tải và cài [MySQL Community Server 8.0](https://dev.mysql.com/downloads/mysql/)
2. Trong quá trình cài đặt, đặt password cho user `root` là `rootpass`
3. Mở Command Prompt/PowerShell và chạy:

```bash
mysql -u root -prootpass < docker\init-db.sql
```

**Linux/Mac:**

```bash
mysql -u root -p < docker/init-db.sql
# Nhập password khi được yêu cầu
```

#### Bước 2: Cấu hình Backend

Đảm bảo file `backend/src/main/resources/application.properties` có cấu hình đúng:

```properties
spring.datasource.url=jdbc:mysql://localhost:3306/vehiclerental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=rootpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8080
```

#### Bước 3: Build và chạy Backend

```bash
# Từ thư mục gốc dự án
mvn clean install
mvn spring-boot:run
```

Backend sẽ chạy tại: **http://localhost:8080**

#### Bước 4: Chạy Frontend

**Cách 4.1: Dùng Live Server (VS Code)**

1. Cài extension "Live Server" trong VS Code
2. Mở thư mục `frontend/`
3. Click phải vào `home/index.html` → "Open with Live Server"
4. Truy cập: **http://127.0.0.1:5500/home/index.html**

**Cách 4.2: Dùng Python SimpleHTTPServer**

```bash
cd frontend
python -m http.server 8000
```

Truy cập: **http://localhost:8000/home/index.html**

**Cách 4.3: Dùng Node.js http-server**

```bash
npm install -g http-server
cd frontend
http-server -p 8000
```

Truy cập: **http://localhost:8000/home/index.html**

**Lưu ý:** Khi chạy frontend không dùng Docker, cần cập nhật API URL trong `frontend/js/api.js` nếu backend chạy trên port khác 8080.

### Cách 2b: Chạy Backend Locally + Database từ Docker (Khuyến nghị)

Đây là cách tốt nhất để phát triển backend: Database từ Docker (không cần cài MySQL), Backend chạy locally để dễ debug.

#### Bước 1: Khởi động chỉ database container

```bash
cd docker
docker-compose up -d db
```

Chờ database sẵn sàng (~10 giây)

#### Bước 2: Import dữ liệu

```bash
# Windows
docker-compose exec db mysql -u root -prootpass vehiclerental < ..\docker\init-db.sql

# Linux/Mac
docker-compose exec db mysql -u root -prootpass vehiclerental < ../docker/init-db.sql
```

#### Bước 3: Cấu hình Backend

File `backend/src/main/resources/application.properties`:

```properties
spring.datasource.url=jdbc:mysql://localhost:3307/vehiclerental?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
spring.datasource.username=root
spring.datasource.password=rootpass
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver

spring.jpa.database-platform=org.hibernate.dialect.MySQL8Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true

server.port=8081
```

**Lưu ý:** Port 8080 được sử dụng bởi Docker backend, nên local backend sử dụng port 8081.

#### Bước 4: Chạy Backend trong VS Code

**Cách 4.1: Chạy bằng Terminal**

```bash
cd C:\project-tuhoc\ITS\Vehicle_rental
mvn clean compile spring-boot:run
```

Backend sẽ chạy tại: **http://localhost:8081**

**Cách 4.2: Chạy với Debug trong VS Code (Khuyến nghị)**

1. **Cài đặt Extensions (nếu chưa có):**

   - "Extension Pack for Java" (Microsoft)
   - "Spring Boot Extension Pack" (VMware)

2. **Mở Folder Project trong VS Code:**

   ```
   File → Open Folder → C:\project-tuhoc\ITS\Vehicle_rental
   ```

3. **Mở file `VehicleRentalApplication.java`:**

   - Đường dẫn: `backend/src/main/java/com/example/backend/VehicleRentalApplication.java`

4. **Nhấn F5 hoặc vào Run → Start Debugging**

   - VS Code sẽ tự động compile và chạy backend
   - Debug Console sẽ hiển thị các logs

5. **Đặt Breakpoint để debug:**
   - Click vào số hàng trái để đặt breakpoint
   - Khi code chạy đến breakpoint, sẽ dừng lại cho inspect

**Cách 4.3: Chạy Maven tasks từ VS Code**

1. Mở Command Palette: `Ctrl+Shift+P`
2. Gõ: `Maven: Execute commands`
3. Chọn: `spring-boot:run`
4. VS Code sẽ chạy backend với hiển thị logs

**Kiểm tra API:**

```bash
curl http://localhost:8081/api/vehicles
```

#### Bước 5: Chạy Frontend

Dùng Live Server hoặc Python SimpleHTTPServer (xem Cách 2 bước 4)

**Cấu hình API URL cho Frontend:**

Mở file `frontend/js/api.js` và sửa:

```javascript
// Khi backend chạy trên port 8081 (local)
const API_BASE_URL = "http://localhost:8081/api";
```

## Cách Dừng Services

```bash
# Dừng all containers (giữ lại dữ liệu)
docker-compose stop

# Hoặc khởi động lại
docker-compose start
```

## 📊 Database

### Dữ liệu mẫu

Database tự động được khởi tạo với:

- **3 users**: 1 admin, 2 user thường
- **24 vehicles**:
  - 8 ô tô (VinFast VF8, Toyota Veloz, Honda City, Mazda CX-5, ...)
  - 8 xe máy (Honda Vision, Vespa Primavera, Yamaha Exciter, ...)
  - 8 xe đạp (Giant ATX 720, Trek FX 3, Specialized Sirrus, ...)

### Tài khoản mặc định

**Admin:**

- Email: `admin@vehiclerental.com`
- Password: `admin`

**User:**

- Email: `user1@gmail.com`
- Pass3307 (Docker) hoặc 3306 (Local)
  Database: vehiclerental
  Username: root
  Password: rootpass (Docker) hoặc root password của bạn (Local)

```
Host: localhost
Port: 5432
Database: vehiclerental
Username: user
Password: pass
```

## 🛠️ Các lệnh Docker hữu ích

### Xem logs

```bash
# Xem tất cả logs
docker-compose logs -f

# Xem logs một service
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

### Dừng containers

```bash
docker-compose stop
```

### Khởi động lại

```bash
docker-compose start
```

### Restart một service

```bash
docker-compose restart backend
```

### Xóa containers (giữ lại data)

```bash
docker-compose down
```

### Xóa containers và data

```bash
docker-compose down -v
```

### Rebuild từ đầu

```bash
docker-compose down -v
docker-compose up -d --build
```

### Truy c (MySQL)

docker-compose exec db mysql -u root -prootpass vehiclerental

# Frontend

docker-compose exec frontend sh

````

### Truy cập phpMyAdmin

```bash
# URL: http://localhost:5050
# Username: root
# Password: rootpass

# Database
docker-compose exec db psql -U user -d vehiclerental

# Frontend
docker-compose exec frontend sh
````

## 🔍 Kiểm tra hoạt động

### Test API

```bash
# Lấy danh sách tất cả xe
curl http://localhost:8080/api/vehicles

# Lọc theo loại xe
curl http://localhost:8080/api/vehicles?vehicleType=OTO
curl http://localhost:8080/api/vehicles?vehicleType=XEMAY
curl http://localhost:8080/api/vehicles?vehicleType=XEDAP
```

### Test bộ lọc Frontend

1. Truy cập: http://localhost/use/danh_sach_xe.html
2. Tick checkbox "Ô tô" → Chỉ hiển thị 8 xe ô tô
3. Tick checkbox "Xe máy" → Hiển thị thêm 8 xe máy
4. Tick checkbox "Xe đạp" → Hiển thị thêm 8 xe đạp
5. Click "Đặt lại bộ lọc" → Hiển thị tất cả 24 xe

## 📁 Cấu trúc dự án

```
Vehicle_rental/
├── backend/                    # Spring Boot Backend
│   ├── src/
│   │   └── main/
│   │       ├── java/          # Java source code
│   │       └── resources/     # Config files
│   ├── Dockerfile             # Backend container
│   └── .dockerignore
├── frontend/                  # Frontend static files
│   ├── home/                  # Trang chủ
│   ├── use/                   # Trang người dùng
│   │   ├── danh_sach_xe.html # Danh sách xe với bộ lọc
│   │   ├── chi_tiet_xe.html  # Chi tiết xe
│   │   └── dat_xe.html       # Đặt thuê xe
│   ├── admin/                 # Trang quản trị
│   ├── login/                 # Đăng nhập/Đăng ký
│   ├── js/                    # JavaScript files
│   │   ├── api.js            # API helpers
│   │   ├── danh-sach-xe.js   # Logic bộ lọc
│   │   └── ...
│   ├── Dockerfile             # Frontend container
│   ├── nginx.conf             # Nginx config
│   └── .dockerignore
├── docker/                    # Docker configuration
│   ├── docker-compose.yml     # Main compose file
│   ├── init-db.sql           # Database init script
│   └── README.md             # Docker docs
├── pom.xml                    # Maven config
└── README.md                  # This file
```

## 🐛 Troubleshooting

### Backend không khởi động

```bash
# Xem logs chi tiết
docker-compose logs backend

# Kiểm tra database đã sẵn sàng
docker-compose exec db pg_isready -U user

# Restart backend
docker-compose restart backend
```

### Frontend không hiển thị xe

```bash
# Kiểm tra API có hoạt động
curl http://localhost:8080/api/vehicles

# Mở DevTools trong browser (F12)
# Kiểm tra Console và Network tab

# Kiểm tra logs frontend
docker-compose logs frontend
```

### Port đã được sử dụng

````bash
# Kiểm tra port đang chạy
netstat -ano | findstr :80
netstat -ano | findstr :8080
netstat -ano | findstr :5432

# Thay đổi port trong docker-compose.yml
# Ví dụ: "8081:8080" thay vì "8080:8080"
```MySQL shell
docker-compose exec db mysql -u root -prootpass vehiclerental

# Kiểm tra tables
SHOW TABLES;bash
# Xem logs database
docker-compose logs db

# Truy cập database shell
docker-compose exec db psql -U user -d vehiclerental

# Kiểm tra tables
\dt

# Kiểm tra dữ liệu
SELECT COUNT(*) FROM vehicles;
````

### Clear cache và rebuild

```bash
docker-compose down -v
docker system prune -a
docker-compose up -d --mysqldump -u root -prootpass vehiclerental > backup_$(date +%Y%m%d).sql
```

### Restore database

````bash
docker-compose exec -T db mysql -u root -prootpass
```bash
docker-compose exec db pg_dump -U user vehiclerental > backup_$(date +%Y%m%d).sql
````

### Restore database

```bash
docker-compose exec -T db psql -U user vehiclerental < backup_20260116.sql
```

## 🔐 Production Deployment

Khi deploy lên production:

1. ✅ Thay đổi passwords mạnh hơn
2. ✅ Sử dụng HTTPS với SSL certificates
3. ✅ Cấu hình CORS đúng domain
4. ✅ Enable firewall
5. ✅ Setup backup tự động
6. ✅ Sử dụng environment variables
7. ✅ Giới hạn resource cho containers
8. ✅ Enable monitoring và logging

## 📞 Hỗ trợ

Nếu gặp vấn đề:

1. Kiểm tra logs: `docker-compose logs -f`
2. Kiểm tra status: `docker-compose ps`
3. Restart services: `docker-compose restart`
4. Xem file [docker/README.md](docker/README.md) để biết thêm chi tiết

## 📄 License

Copyright © 2026 Vehicle Rental Team

## � API Endpoints

### Authentication

**Login:**

```bash
POST /api/auth/login
Content-Type: application/json

{
  "email": "admin@vehiclerental.com",
  "password": "admin"
}
```

**Register:**

```bash
POST /api/auth/register
Content-Type: application/json

{
  "fullName": "User Name",
  "email": "user@example.com",
  "password": "password123",
  "confirmPassword": "password123",
  "phone": "0123456789",
  "address": "Your Address"
}
```

### Vehicles

**Get all vehicles:**

```bash
GET /api/vehicles
```

**Filter by type:**

```bash
GET /api/vehicles?vehicleType=OTO
GET /api/vehicles?vehicleType=XEMAY
GET /api/vehicles?vehicleType=XEDAP
```

## 👥 Team

- **Backend Developer**: Spring Boot + MySQL
- **Frontend Developer**: HTML/CSS/JS + TailwindCSS
- **DevOps**: Docker + Nginx

---

**Happy Coding! 🚀**
