#!/bin/bash

# Script khởi động backend + database cho development
# Sử dụng: chmod +x dev-startup.sh && ./dev-startup.sh

echo "🚀 Vehicle Rental - Development Environment Startup"
echo "=================================================="

# Kiểm tra Docker
if ! command -v docker &> /dev/null; then
    echo "❌ Docker không được cài đặt. Vui lòng cài Docker Desktop"
    exit 1
fi

# Kiểm tra Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven không được cài đặt. Vui lòng cài Maven"
    exit 1
fi

# Kiểm tra Java
if ! command -v java &> /dev/null; then
    echo "❌ Java không được cài đặt. Vui lòng cài Java 17 JDK"
    exit 1
fi

echo "✅ Tất cả dependencies đã được cài đặt"
echo ""

# Bước 1: Khởi động Database
echo "📦 Bước 1: Khởi động Database từ Docker..."
cd docker
docker-compose up -d db

# Chờ database sẵn sàng
echo "⏳ Chờ database sẵn sàng..."
sleep 15

# Kiểm tra database
if docker-compose exec db mysql -u root -prootpass vehiclerental -e "SELECT 1" &> /dev/null; then
    echo "✅ Database sẵn sàng"
else
    echo "❌ Database không kết nối được. Vui lòng kiểm tra Docker"
    exit 1
fi

cd ..

# Bước 2: Chạy Backend
echo ""
echo "🎯 Bước 2: Chạy Backend Spring Boot (Port 8081)..."
echo "=================================================="
echo "Khi thấy 'Started VehicleRentalApplication', backend đã sẵn sàng"
echo ""

mvn clean compile spring-boot:run

# Sau khi backend dừng
echo ""
echo "🛑 Backend đã dừng"
read -p "Bạn có muốn dừng Database không? (y/N): " -n 1 -r
echo
if [[ $REPLY =~ ^[Yy]$ ]]; then
    cd docker
    docker-compose stop db
    echo "✅ Database đã dừng"
    cd ..
fi

echo "👋 Tạm biệt!"
