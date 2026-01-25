# 🚀 Hướng dẫn Chạy Backend trong VS Code (Với Database từ Docker)

## Yêu cầu

- ✅ Java 17 JDK cài sẵn
- ✅ Maven cài sẵn
- ✅ VS Code cài Extension Pack for Java
- ✅ Docker Desktop đang chạy

## Bước 1: Khởi động Database từ Docker

Chạy một trong các lệnh sau:

### Option A: Dùng VS Code Tasks (Dễ nhất)

1. Nhấn `Ctrl+Shift+P` → gõ `Tasks: Run Task`
2. Chọn: `Database: Start Docker DB only`
3. Chờ ~ 10 giây cho Database sẵn sàng

### Option B: Dùng Terminal

```bash
cd docker
docker-compose up -d db
```

## Bước 2: Chạy Backend

### Option A: Chạy với Debug (Khuyến nghị)

1. Nhấn `F5` hoặc `Ctrl+Shift+D` → chọn "Spring Boot Backend - Run"
2. VS Code sẽ tự động compile và khởi động backend
3. Khi thấy logs: `Started VehicleRentalApplication`, backend đã sẵn sàng
4. Truy cập API: http://localhost:8081/api/vehicles

### Option B: Chạy bằng Terminal

```bash
mvn clean compile spring-boot:run
```

### Option C: Chạy bằng VS Code Tasks

1. Nhấn `Ctrl+Shift+P` → gõ `Tasks: Run Task`
2. Chọn: `Backend: Run`

## Bước 3: Debug Code (Tùy chọn)

Khi backend chạy ở trạng thái Debug:

1. **Đặt Breakpoint:** Click vào số hàng trái trong code Java
2. **Trigger endpoint:** Gọi API từ Frontend hoặc Postman
3. **Inspect variables:** Xem giá trị variables, step qua code
4. **Xem logs:** Debug Console sẽ hiển thị tất cả logs

### Breakpoint hữu ích

- [VehicleController.java](../backend/src/main/java/com/example/backend/controller/VehicleController.java) - API endpoints
- [AuthController.java](../backend/src/main/java/com/example/backend/controller/AuthController.java) - Login/Register
- [UserRepository.java](../backend/src/main/java/com/example/backend/repository/UserRepository.java) - Database queries

## Bước 4: Chạy Frontend

Mở terminal mới và chạy:

```bash
cd frontend
python -m http.server 8000
```

Hoặc dùng Live Server extension và click `Open with Live Server` trên `index.html`

## Cấu hình API URL

File: `frontend/js/api.js`

Khi backend chạy local port 8081:

```javascript
const API_BASE_URL = "http://localhost:8081/api";
```

## 🔍 Kiểm tra Kết nối

**Terminal:**

```bash
# Kiểm tra backend
curl http://localhost:8081/api/vehicles

# Kiểm tra database
docker exec vehiclerental-db mysql -u root -prootpass vehiclerental -e "SELECT COUNT(*) FROM vehicles;"
```

**VS Code Debug Console:**

- Khi backend chạy ở debug mode, mở Debug Console (Ctrl+Shift+Y)
- Gõ expressions để inspect: `Vehicle.class`, `userRepository.findAll()`, etc.

## 🛑 Dừng Services

```bash
# Dừng backend: Nhấn Ctrl+C trong terminal hoặc Red Stop button
# Dừng database:
docker-compose -f docker/docker-compose.yml stop db

# Hoặc dùng VS Code Task: Database: Stop Docker DB
```

## 💡 Tips

1. **Hot Reload:** Spring Boot DevTools tự động reload khi bạn sửa code
2. **Xem SQL Queries:** Logs sẽ hiển thị tất cả SQL queries (nếu `show-sql=true`)
3. **Breakpoint Conditions:** Click chuột phải breakpoint → "Breakpoint Properties" → thêm condition
4. **Watch Variables:** Trong Debug panel, thêm variables để theo dõi real-time

## ❌ Troubleshooting

**Port 8081 đã được sử dụng:**

```bash
# Windows
netstat -ano | findstr :8081
taskkill /PID <PID> /F

# Linux/Mac
lsof -i :8081
kill -9 <PID>
```

**Database connection refused:**

```bash
# Kiểm tra Docker container
docker-compose ps

# Xem logs database
docker-compose logs db
```

**Maven build failed:**

```bash
# Clean cache
mvn clean
mvn compile
```

---

✅ Bây giờ bạn có thể phát triển backend dễ dàng trong VS Code!
