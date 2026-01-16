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

-- Table: rentals
CREATE TABLE IF NOT EXISTS rentals (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    start_date TIMESTAMP NOT NULL,
    end_date TIMESTAMP NOT NULL,
    total_price DECIMAL(10,2) NOT NULL,
    status ENUM('PENDING', 'CONFIRMED', 'COMPLETED', 'CANCELLED') NOT NULL DEFAULT 'PENDING',
    payment_status ENUM('UNPAID', 'PAID', 'REFUNDED') NOT NULL DEFAULT 'UNPAID',
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Table: reviews
CREATE TABLE IF NOT EXISTS reviews (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    rental_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    vehicle_id BIGINT NOT NULL,
    rating INT NOT NULL CHECK (rating >= 1 AND rating <= 5),
    comment TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (rental_id) REFERENCES rentals(id) ON DELETE CASCADE,
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
('VinFast VF8 2023', 'OTO', 'VinFast', 'VF8', 2023, 5, 'Tự động', 'Điện', 1200000.00, 'SUV điện cao cấp, sang trọng và hiện đại', 'Quận 1, TP. Hồ Chí Minh', 'https://lh3.googleusercontent.com/aida-public/AB6AXuBWSymljZ8TVOZYjVADw1AdXOuk032_3qOc0YquXHzGRrY9zv18dzCQjgLRqY45rx1nsazNlkVpEf7Mm5UgQdnTVtpW8QlxvelqDfycpwEV2erJ3AJ1jS1JuMM0RxkCk6NfmN7-LJr3H2eRHWhE6Sb3XSyMDX07hbsmcqb9lhkboYqOyeCZk1NPb0Vz01y5pSYXPtIBcsQ6QzDLqcMKGG22SWvAvR1mLUJE6g51DNbL0fRBTa2ziTzVRFgwIlKJA0cuRmyLiTKWp7aJ', 'AVAILABLE', 4.8),
('Toyota Veloz Cross', 'OTO', 'Toyota', 'Veloz Cross', 2023, 7, 'Tự động', 'Xăng', 1000000.00, 'MPV 7 chỗ rộng rãi, tiết kiệm nhiên liệu', 'Cầu Giấy, Hà Nội', 'https://lh3.googleusercontent.com/aida-public/AB6AXuCXh1xCIJUJY_KilO1v8tx-IB8VQ-D1SvWKEhvw0ouMXTsRUVQ8FzqxVWVxJoRQbEuwKsT3dlrbm98hYzy6PCbrouMdCzf2uXx4FXROqmPSOoo1J4nBIgBrhDmdQqVcR4EyI1JoK_vMdGH3YJ1zfDnXncosM6089zEUnjvIf6DJEHuNQBxyj1uf2g5Yin4YHZzW3q7m930o4LhLTARGX0qaOjZWar1ehuK7wZKPzlsSsDGBKqH9_TOmloSV4TUBFERiuuaUK0izrJsD', 'AVAILABLE', 4.6),
('Honda City 2022', 'OTO', 'Honda', 'City', 2022, 5, 'Tự động', 'Xăng', 800000.00, 'Sedan sang trọng, tiện nghi cho gia đình', 'Quận 3, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Honda+City', 'AVAILABLE', 4.7),
('Mazda CX-5 2023', 'OTO', 'Mazda', 'CX-5', 2023, 5, 'Tự động', 'Xăng', 1100000.00, 'SUV thể thao, an toàn và mạnh mẽ', 'Hai Bà Trưng, Hà Nội', 'https://via.placeholder.com/400x300?text=Mazda+CX5', 'AVAILABLE', 4.9),
('Hyundai Accent 2023', 'OTO', 'Hyundai', 'Accent', 2023, 5, 'Tự động', 'Xăng', 700000.00, 'Sedan tiết kiệm, phù hợp đi phố', 'Tân Bình, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Hyundai+Accent', 'AVAILABLE', 4.5),
('Ford Ranger 2022', 'OTO', 'Ford', 'Ranger', 2022, 5, 'Tự động', 'Dầu', 1300000.00, 'Bán tải mạnh mẽ, phù hợp địa hình phức tạp', 'Hải Châu, Đà Nẵng', 'https://via.placeholder.com/400x300?text=Ford+Ranger', 'AVAILABLE', 4.8),
('Mercedes-Benz C-Class', 'OTO', 'Mercedes-Benz', 'C-Class', 2022, 5, 'Tự động', 'Xăng', 2000000.00, 'Sedan hạng sang, đẳng cấp thượng lưu', 'Quận 1, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Mercedes+C-Class', 'AVAILABLE', 5.0),
('Kia Seltos 2023', 'OTO', 'Kia', 'Seltos', 2023, 5, 'Tự động', 'Xăng', 900000.00, 'SUV đô thị thời trang', 'Đống Đa, Hà Nội', 'https://via.placeholder.com/400x300?text=Kia+Seltos', 'AVAILABLE', 4.6);

-- Insert sample vehicles - XE MÁY
INSERT INTO vehicles (name, vehicle_type, brand, model, year, seats, transmission, price_per_day, description, location, image_url, status, rating) VALUES
('Honda Vision 2023', 'XEMAY', 'Honda', 'Vision', 2023, 2, 'Tay ga', 150000.00, 'Xe ga tiết kiệm, phù hợp đi trong thành phố', 'Ba Đình, Hà Nội', 'https://lh3.googleusercontent.com/aida-public/AB6AXuAnk96OdOLrKbhDYa1FZtVeZpFrIR2_E9dkxZDI6oAlLDCG1GlM6nne9IMosEkP6Xj8e-x1n8fFSnVJ5MPV4-nnK5w_mwFWuwZfBbTBgMi54-nXESggrsAbi5AuTTwrE8nkU4Vx0zjZRTBXFF_vt844D5JsAvbZM-McVIXTYX70WmJUHbzY8kTGs1tvdmpd0VQhgdRJV9nYjjBWZPYEiOd-4TJ9IvDnZhd4iljkDvgxGam54nRXojlKbq6E5e00O5jPaezWhaaSPqHH', 'AVAILABLE', 4.9),
('Vespa Primavera 2022', 'XEMAY', 'Vespa', 'Primavera', 2022, 2, 'Tay ga', 350000.00, 'Xe tay ga cao cấp, phong cách Ý', 'Quận 3, TP. Hồ Chí Minh', 'https://lh3.googleusercontent.com/aida-public/AB6AXuAXBY7x1bXcdtatB-qW1uUUxSKyaDBfWFE7Bd5De3Rlvsq4-uIi9ZvIuwcEA3v-uy_gGFg_-Z2CTRkXQXm_qySYNPfP9ahH2YX1yM7sVcpUbD1xtssITfPmJzFF72OrjcwiJgADk6SPqJO1SkT2eUgXJmhKKXgiUk4tstZ_7gL6RRUMCYn9S02O6qyswNK_s1soPkb53u9nchMdTnEh2iRjTVhpbv44x7E3jXEd8jD-6rotY-f2bqJ7DDz2R3f5riD3SzIyMMerBcy3', 'AVAILABLE', 5.0),
('Yamaha Exciter 155', 'XEMAY', 'Yamaha', 'Exciter 155', 2023, 2, 'Số sàn', 200000.00, 'Xe côn tay thể thao, mạnh mẽ', 'Sơn Trà, Đà Nẵng', 'https://via.placeholder.com/400x300?text=Yamaha+Exciter', 'AVAILABLE', 4.7),
('Honda Air Blade 2023', 'XEMAY', 'Honda', 'Air Blade', 2023, 2, 'Tay ga', 180000.00, 'Xe ga thể thao, vận hành êm ái', 'Quận 7, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Honda+AirBlade', 'AVAILABLE', 4.8),
('SYM Symphony 2023', 'XEMAY', 'SYM', 'Symphony', 2023, 2, 'Tay ga', 140000.00, 'Xe ga nhỏ gọn, tiết kiệm', 'Hoàn Kiếm, Hà Nội', 'https://via.placeholder.com/400x300?text=SYM+Symphony', 'AVAILABLE', 4.5),
('Honda Winner X', 'XEMAY', 'Honda', 'Winner X', 2022, 2, 'Số sàn', 220000.00, 'Xe côn tay cao cấp, đầy sức mạnh', 'Tân Phú, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Honda+WinnerX', 'AVAILABLE', 4.9),
('Yamaha Grande 2023', 'XEMAY', 'Yamaha', 'Grande', 2023, 2, 'Tay ga', 160000.00, 'Xe ga thanh lịch, dành cho phụ nữ', 'Cầu Giấy, Hà Nội', 'https://via.placeholder.com/400x300?text=Yamaha+Grande', 'AVAILABLE', 4.6),
('Piaggio Liberty', 'XEMAY', 'Piaggio', 'Liberty', 2022, 2, 'Tay ga', 300000.00, 'Xe tay ga Ý cao cấp', 'Quận 1, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Piaggio+Liberty', 'AVAILABLE', 4.8);

-- Insert sample vehicles - XE ĐẠP
INSERT INTO vehicles (name, vehicle_type, brand, model, year, seats, price_per_day, description, location, image_url, status, rating) VALUES
('Giant ATX 720', 'XEDAP', 'Giant', 'ATX 720', 2023, 1, 80000.00, 'Xe đạp địa hình chuyên nghiệp', 'Hải Châu, Đà Nẵng', 'https://lh3.googleusercontent.com/aida-public/AB6AXuC7VKrJaczKrbY2RtCROCbOE-mwk8lQzy_eDnZu_fJQUNLasQYL0VlcGg1L5t5Up7PK96tlEwxf7Fbaj_qasoDEPxeW7fquxVDGnSPoJW9G0b2N5aZouVW10IcmPdQFF_2EEj8W4VNJBsCsosN2GPixA3iR7WnwEFfqqsNcUbQjUrf71oYw2M7F08-nuOcKcpsPKjSYkq6pYKcnogCiUpYC36ZL8rPRHg-Di7pXyahb-7BzN-N0DDrzT_upOwxmPIfKy5wkrUSXuAtR', 'AVAILABLE', 4.7),
('Xe đạp điện Klara S', 'XEDAP', 'VinFast', 'Klara S', 2023, 1, 120000.00, 'Xe đạp điện tiện lợi cho đô thị', 'TP. Thủ Đức', 'https://lh3.googleusercontent.com/aida-public/AB6AXuABBS3kTx1q85Fk9S9NbbkErLVk2aVWydDqH-oF1Ed-iQbxPrhOjEVcrE2zwbouiZ5GFqOJ44NUwCg4iWaVBXD-hJUaUxmJ9ciPlu2IXuKnMXYDHvjc8wSmaPOXyofczqoivrLaKTWzyI4uyVMNE6z2zOrI4gdbugve5KgYphpEiKB-hxgXk72WL8DtBm5fvujSCxvJ2w-iq3xLeUtfvwrNS3ulxcfprI_wpQDYojzJeN1d8Lw8hTeEpLgQDSQP2y2D3T874uVPZ1hl', 'AVAILABLE', 4.5),
('Trek FX 3', 'XEDAP', 'Trek', 'FX 3', 2022, 1, 90000.00, 'Xe đạp thể thao đa năng', 'Quận 1, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Trek+FX3', 'AVAILABLE', 4.8),
('Specialized Sirrus', 'XEDAP', 'Specialized', 'Sirrus', 2023, 1, 100000.00, 'Xe đạp fitness cao cấp', 'Hoàn Kiếm, Hà Nội', 'https://via.placeholder.com/400x300?text=Specialized+Sirrus', 'AVAILABLE', 4.9),
('Cannondale Trail', 'XEDAP', 'Cannondale', 'Trail 5', 2022, 1, 95000.00, 'Xe đạp leo núi chuyên nghiệp', 'Ngũ Hành Sơn, Đà Nẵng', 'https://via.placeholder.com/400x300?text=Cannondale+Trail', 'AVAILABLE', 4.6),
('Merida Scultura', 'XEDAP', 'Merida', 'Scultura', 2023, 1, 110000.00, 'Xe đạp đua cao cấp', 'Quận 3, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Merida+Scultura', 'AVAILABLE', 4.8),
('Polygon Xtrada', 'XEDAP', 'Polygon', 'Xtrada 5', 2022, 1, 85000.00, 'Xe đạp địa hình giá tốt', 'Thanh Xuân, Hà Nội', 'https://via.placeholder.com/400x300?text=Polygon+Xtrada', 'AVAILABLE', 4.5),
('Xe đạp gấp Dahon', 'XEDAP', 'Dahon', 'K3', 2023, 1, 70000.00, 'Xe đạp gấp tiện lợi', 'Quận 7, TP. Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Dahon+Folding', 'AVAILABLE', 4.4);
