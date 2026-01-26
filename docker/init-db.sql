-- MySQL Database Initialization Script for Vehicle Rental System

-- Tạo database nếu chưa tồn tại
CREATE DATABASE IF NOT EXISTS vehiclerental CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE vehiclerental;

-- Table: users
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    full_name VARCHAR(255) NOT NULL,
    email VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL,
    phone VARCHAR(20),
    address TEXT,
    role ENUM('ADMIN', 'USER') NOT NULL DEFAULT 'USER',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: vehicles
CREATE TABLE IF NOT EXISTS vehicles (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255) NOT NULL,
    vehicle_type ENUM('OTO', 'XEMAY', 'XEDAP') NOT NULL,
    brand VARCHAR(100),
    model VARCHAR(100),
    year INT,
    license_plate VARCHAR(20),
    color VARCHAR(50),
    seats INT,
    transmission VARCHAR(50),
    fuel_type VARCHAR(50),
    price_per_day DECIMAL(10,2) NOT NULL,
    description TEXT,
    location VARCHAR(255),
    image_url TEXT,
    status ENUM('AVAILABLE', 'RENTED', 'MAINTENANCE') NOT NULL DEFAULT 'AVAILABLE',
    rating DECIMAL(3,2) DEFAULT 0.00,
    total_rentals INT DEFAULT 0,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Table: orders
CREATE TABLE IF NOT EXISTS orders (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    date_from DATE NOT NULL,
    date_to DATE NOT NULL,
    total_days INT,
    price_per_day DECIMAL(10,2) NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'IN_PROGRESS', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    pickup_location VARCHAR(255),
    return_location VARCHAR(255),
    customer_name VARCHAR(255),
    customer_phone VARCHAR(20),
    customer_email VARCHAR(255),
    notes TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Insert sample users (passwords are plain text 'admin' for development only)
INSERT INTO users (full_name, email, password, phone, role) VALUES
('Admin User', 'admin@vehiclerental.com', 'admin', '0123456789', 'ADMIN'),
('Nguyễn Văn A', 'user1@gmail.com', 'admin', '0987654321', 'USER'),
('Trần Thị B', 'user2@gmail.com', 'admin', '0912345678', 'USER');

-- Insert sample vehicles - Ô TÔ
INSERT INTO vehicles (name, vehicle_type, brand, model, year, seats, transmission, fuel_type, price_per_day, description, location, image_url, status, rating) VALUES
('Toyota Granvia 2021', 'OTO', 'Toyota', 'Granvia', 2021, 7, 'Tự động', 'Xăng', 1200000.00, 'MPV cao cấp, sang trọng và hiện đại', 'Quận 1, TP. Hồ Chí Minh', '/img/oto/Toyota_Granvia_2021.png', 'AVAILABLE', 4.8),
('Toyota Avanza Premio 2022', 'OTO', 'Toyota', 'Avanza Premio', 2022, 7, 'Tự động', 'Xăng', 1000000.00, 'MPV 7 chỗ rộng rãi, tiết kiệm nhiên liệu', 'Cầu Giấy, Hà Nội', '/img/oto/Toyota_Avanza_Premio_2022.png', 'AVAILABLE', 4.6),
('Suzuki Ertiga 2022', 'OTO', 'Suzuki', 'Ertiga', 2022, 7, 'Tự động', 'Xăng', 800000.00, 'MPV sang trọng, tiện nghi cho gia đình', 'Quận 3, TP. Hồ Chí Minh', '/img/oto/Suzuki_Ertiga_2022.png', 'AVAILABLE', 4.7),
('Mazda BT-50 2021', 'OTO', 'Mazda', 'BT-50', 2021, 5, 'Tự động', 'Dầu', 1100000.00, 'Bán tải mạnh mẽ, an toàn', 'Hai Bà Trưng, Hà Nội', '/img/oto/Mazda_BT-50_2021%20.png', 'AVAILABLE', 4.9),
('KIA Seltos 2023', 'OTO', 'KIA', 'Seltos', 2023, 5, 'Tự động', 'Xăng', 900000.00, 'SUV đô thị thời trang', 'Tân Bình, TP. Hồ Chí Minh', '/img/oto/KIA_Seltos.png', 'AVAILABLE', 4.5),
('Ford Ranger 2022', 'OTO', 'Ford', 'Ranger', 2022, 5, 'Tự động', 'Dầu', 1300000.00, 'Bán tải mạnh mẽ, phù hợp địa hình phức tạp', 'Hải Châu, Đà Nẵng', '/img/oto/Ford_Ranger_2022%20.png', 'AVAILABLE', 4.8);

-- Insert sample vehicles - XE MÁY
INSERT INTO vehicles (name, vehicle_type, brand, model, year, seats, transmission, price_per_day, description, location, image_url, status, rating) VALUES
('Honda Vision 2023', 'XEMAY', 'Honda', 'Vision', 2023, 2, 'Tay ga', 150000.00, 'Xe ga tiết kiệm, phù hợp đi trong thành phố', 'Ba Đình, Hà Nội', '/img/oto/xe_may/Vision.png', 'AVAILABLE', 4.9),
('Yamaha Exciter 150', 'XEMAY', 'Yamaha', 'Exciter 150', 2023, 2, 'Số sàn', 200000.00, 'Xe côn tay thể thao, mạnh mẽ', 'Sơn Trà, Đà Nẵng', '/img/oto/xe_may/Exciter_150.png', 'AVAILABLE', 4.7),
('Yamaha Exciter 135', 'XEMAY', 'Yamaha', 'Exciter 135', 2022, 2, 'Số sàn', 180000.00, 'Xe côn tay cổ điển, bền bỉ', 'Quận 7, TP. Hồ Chí Minh', '/img/oto/xe_may/Exciter_135.png', 'AVAILABLE', 4.8),
('Honda SH Move', 'XEMAY', 'Honda', 'SH Move', 2023, 2, 'Tay ga', 250000.00, 'Xe ga cao cấp, thời trang', 'Hoàn Kiếm, Hà Nội', '/img/oto/xe_may/SH_move.png', 'AVAILABLE', 4.5),
('Yamaha Sirius', 'XEMAY', 'Yamaha', 'Sirius', 2022, 2, 'Số sàn', 120000.00, 'Xe số tiết kiệm, phổ biến', 'Tân Phú, TP. Hồ Chí Minh', '/img/oto/xe_may/Sirius.png', 'AVAILABLE', 4.9),
('Honda Wave', 'XEMAY', 'Honda', 'Wave', 2022, 2, 'Số sàn', 100000.00, 'Xe số quốc dân, bền bỉ', 'Cầu Giấy, Hà Nội', '/img/oto/xe_may/Wave.png', 'AVAILABLE', 4.6),
('Honda Air Blade 150', 'XEMAY', 'Honda', 'Air Blade 150', 2023, 2, 'Tay ga', 180000.00, 'Xe ga thể thao, vận hành êm ái', 'Quận 1, TP. Hồ Chí Minh', '/img/oto/xe_may/Ải_Blade_150.png', 'AVAILABLE', 4.8);

-- Insert sample vehicles - XE ĐẠP
INSERT INTO vehicles (name, vehicle_type, brand, model, year, seats, price_per_day, description, location, image_url, status, rating) VALUES
('Road Fascino', 'XEDAP', 'Fascino', 'Road', 2023, 1, 80000.00, 'Xe đạp đường phố thời trang', 'Hải Châu, Đà Nẵng', '/img/oto/xe_dap/Road_Fascino.png', 'AVAILABLE', 4.7),
('Touring Merida Explorer', 'XEDAP', 'Merida', 'Explorer', 2023, 1, 120000.00, 'Xe đạp touring chuyên nghiệp', 'TP. Thủ Đức', '/img/oto/xe_dap/Touring_Merida_Explorer.png', 'AVAILABLE', 4.5),
('Touring Mocos', 'XEDAP', 'Mocos', 'Touring', 2022, 1, 90000.00, 'Xe đạp đa năng', 'Quận 1, TP. Hồ Chí Minh', '/img/oto/xe_dap/Touring_Mocos.png', 'AVAILABLE', 4.8);
