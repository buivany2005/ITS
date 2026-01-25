@echo off
REM Script khởi động backend + database cho development trên Windows
REM Sử dụng: Chạy dev-startup.bat trong CMD hoặc PowerShell

echo 🚀 Vehicle Rental - Development Environment Startup
echo ==================================================

REM Kiểm tra Docker
docker --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Docker không được cài đặt. Vui lòng cài Docker Desktop
    exit /b 1
)

REM Kiểm tra Maven
mvn --version >nul 2>&1
if errorlevel 1 (
    echo ❌ Maven không được cài đặt
    exit /b 1
)

REM Kiểm tra Java
java -version >nul 2>&1
if errorlevel 1 (
    echo ❌ Java không được cài đặt
    exit /b 1
)

echo ✅ Tất cả dependencies đã được cài đặt
echo.

REM Bước 1: Khởi động Database
echo 📦 Bước 1: Khởi động Database từ Docker...
cd docker
docker-compose up -d db

echo ⏳ Chờ database sẵn sàng...
timeout /t 15 /nobreak

REM Kiểm tra database
docker-compose exec db mysql -u root -prootpass vehiclerental -e "SELECT 1" >nul 2>&1
if errorlevel 1 (
    echo ❌ Database không kết nối được
    exit /b 1
)

echo ✅ Database sẵn sàng
cd ..

REM Bước 2: Chạy Backend
echo.
echo 🎯 Bước 2: Chạy Backend Spring Boot (Port 8081)...
echo ==================================================
echo Khi thấy 'Started VehicleRentalApplication', backend đã sẵn sàng
echo.

mvn clean compile spring-boot:run

REM Sau khi backend dừng
echo.
echo 🛑 Backend đã dừng
set /p STOP_DB="Bạn có muốn dừng Database không? (y/N): "
if /i "%STOP_DB%"=="y" (
    cd docker
    docker-compose stop db
    echo ✅ Database đã dừng
    cd ..
)

echo 👋 Tạm biệt!
pause
