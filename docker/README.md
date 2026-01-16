# Vehicle Rental System - Docker Setup

Hệ thống cho thuê phương tiện được containerized với Docker.

## 📋 Yêu cầu

- Docker Engine 20.10+
- Docker Compose 2.0+
- 4GB RAM khả dụng
- 10GB dung lượng ổ cứng

## 🏗️ Kiến trúc

Dự án bao gồm các services:

1. **PostgreSQL** (port 5432) - Database
2. **Spring Boot Backend** (port 8080) - REST API
3. **Nginx Frontend** (port 80/443) - Web UI
4. **PgAdmin** (port 5050) - Database Management (Optional)

## 🚀 Khởi chạy

### 1. Clone project và di chuyển vào thư mục docker

```bash
cd docker
```

### 2. Tạo file .env (tùy chọn)

```bash
cp .env.example .env
# Chỉnh sửa .env nếu cần
```

### 3. Khởi động tất cả services

```bash
docker-compose up -d
```

Hoặc build lại từ đầu:

```bash
docker-compose up -d --build
```

### 4. Kiểm tra logs

```bash
# Xem logs tất cả services
docker-compose logs -f

# Xem logs một service cụ thể
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f db
```

### 5. Kiểm tra trạng thái services

```bash
docker-compose ps
```

## 🌐 Truy cập ứng dụng

- **Frontend**: http://localhost
- **Backend API**: http://localhost:8080
- **PgAdmin**: http://localhost:5050
  - Email: admin@vehiclerental.com
  - Password: admin

## 📊 Database

### Kết nối từ ứng dụng local

```
Host: localhost
Port: 5432
Database: vehiclerental
Username: user
Password: pass
```

### Kết nối từ PgAdmin

1. Truy cập http://localhost:5050
2. Đăng nhập với email/password trong .env
3. Add New Server:
   - Name: VehicleRental
   - Host: db
   - Port: 5432
   - Database: vehiclerental
   - Username: user
   - Password: pass

## 🛠️ Các lệnh hữu ích

### Dừng tất cả services

```bash
docker-compose stop
```

### Khởi động lại services

```bash
docker-compose restart
```

### Xóa containers (giữ lại data)

```bash
docker-compose down
```

### Xóa containers và volumes (xóa toàn bộ data)

```bash
docker-compose down -v
```

### Rebuild một service cụ thể

```bash
docker-compose up -d --build backend
docker-compose up -d --build frontend
```

### Truy cập shell của container

```bash
# Backend
docker-compose exec backend sh

# Database
docker-compose exec db psql -U user -d vehiclerental

# Frontend
docker-compose exec frontend sh
```

### Xem resource usage

```bash
docker stats
```

## 🔧 Troubleshooting

### Backend không kết nối được database

```bash
# Kiểm tra database đã sẵn sàng
docker-compose logs db

# Restart backend
docker-compose restart backend
```

### Port bị chiếm

```bash
# Thay đổi port trong docker-compose.yml
# Ví dụ: "8081:8080" thay vì "8080:8080"
```

### Clear cache và rebuild

```bash
docker-compose down
docker system prune -a
docker-compose up -d --build
```

## 📝 Dữ liệu mẫu

Database sẽ tự động được khởi tạo với dữ liệu mẫu từ file `init-db.sql`:

- 3 users (1 admin, 2 user thường)
- 24 vehicles (8 ô tô, 8 xe máy, 8 xe đạp)
- Một số rental và review mẫu

**Default admin account:**

- Email: admin@vehiclerental.com
- Password: admin (hash: $2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy)

## 🔐 Production Deployment

Khi deploy production:

1. Thay đổi passwords trong .env
2. Sử dụng HTTPS với SSL certificates
3. Cấu hình firewall
4. Enable backup tự động cho database
5. Sử dụng Docker secrets thay vì environment variables
6. Cấu hình resource limits trong docker-compose.yml

## 📦 Backup & Restore

### Backup database

```bash
docker-compose exec db pg_dump -U user vehiclerental > backup.sql
```

### Restore database

```bash
docker-compose exec -T db psql -U user vehiclerental < backup.sql
```

## 📞 Hỗ trợ

Nếu gặp vấn đề, vui lòng:

1. Kiểm tra logs: `docker-compose logs -f`
2. Kiểm tra health check: `docker ps`
3. Restart services: `docker-compose restart`
