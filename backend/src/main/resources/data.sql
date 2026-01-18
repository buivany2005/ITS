-- Insert sample users
INSERT INTO users (id, full_name, email, password, phone, address, role, created_at, updated_at) VALUES
(1, 'Nguyễn Văn A', 'admin@example.com', 'admin123', '0901234567', 'Hà Nội', 'ADMIN', NOW(), NOW()),
(2, 'Trần Thị B', 'user@example.com', 'user123', '0907654321', 'Hồ Chí Minh', 'USER', NOW(), NOW());

-- Insert sample vehicles
INSERT INTO vehicles (name, vehicle_type, brand, model, year, license_plate, color, seats, transmission, fuel_type, price_per_day, description, location, image_url, status, rating, total_rentals, created_at, updated_at) VALUES
-- Ô tô
('Toyota Vios', 'OTO', 'Toyota', 'Vios', 2023, '30A-12345', 'Trắng', 5, 'Số tự động', 'Xăng', 500000, 'Xe sedan tiết kiệm nhiên liệu, phù hợp cho gia đình', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Toyota+Vios', 'AVAILABLE', 4.5, 25, NOW(), NOW()),
('Honda City', 'OTO', 'Honda', 'City', 2022, '29B-54321', 'Đen', 5, 'Số tự động', 'Xăng', 480000, 'Xe sedan sang trọng, êm ái', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Honda+City', 'AVAILABLE', 4.7, 30, NOW(), NOW()),
('Mazda 3', 'OTO', 'Mazda', '3', 2023, '30C-99999', 'Đỏ', 5, 'Số tự động', 'Xăng', 600000, 'Xe sedan thể thao, thiết kế hiện đại', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Mazda+3', 'AVAILABLE', 4.8, 20, NOW(), NOW()),
('Hyundai Accent', 'OTO', 'Hyundai', 'Accent', 2022, '51F-77777', 'Bạc', 5, 'Số tự động', 'Xăng', 450000, 'Xe sedan giá rẻ, tiết kiệm', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Hyundai+Accent', 'AVAILABLE', 4.3, 18, NOW(), NOW()),

-- Xe máy
('Honda Winner X', 'XEMAY', 'Honda', 'Winner X', 2023, '29-A1 12345', 'Đỏ đen', 2, 'Số sàn', 'Xăng', 150000, 'Xe số thể thao, mạnh mẽ', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Honda+Winner+X', 'AVAILABLE', 4.6, 45, NOW(), NOW()),
('Yamaha Exciter', 'XEMAY', 'Yamaha', 'Exciter 155', 2023, '30-B2 54321', 'Xanh', 2, 'Số sàn', 'Xăng', 140000, 'Xe số phổ biến, bền bỉ', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Yamaha+Exciter', 'AVAILABLE', 4.5, 50, NOW(), NOW()),
('Honda SH Mode', 'XEMAY', 'Honda', 'SH Mode', 2022, '51-C1 99999', 'Trắng', 2, 'Tự động', 'Xăng', 120000, 'Xe tay ga tiện lợi, sang trọng', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Honda+SH+Mode', 'AVAILABLE', 4.4, 35, NOW(), NOW()),
('Yamaha Janus', 'XEMAY', 'Yamaha', 'Janus', 2022, '51-D2 77777', 'Xanh mint', 2, 'Tự động', 'Xăng', 100000, 'Xe tay ga nữ tính, nhẹ nhàng', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Yamaha+Janus', 'AVAILABLE', 4.2, 40, NOW(), NOW()),

-- Xe đạp
('Giant Escape 3', 'XEDAP', 'Giant', 'Escape 3', 2023, '', 'Xanh dương', 1, '', '', 50000, 'Xe đạp thể thao, phù hợp đi phố', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Giant+Escape+3', 'AVAILABLE', 4.5, 15, NOW(), NOW()),
('Trek FX 2', 'XEDAP', 'Trek', 'FX 2', 2023, '', 'Đen', 1, '', '', 55000, 'Xe đạp hybrid chất lượng cao', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Trek+FX+2', 'AVAILABLE', 4.6, 12, NOW(), NOW()),
('Merida Speeder 100', 'XEDAP', 'Merida', 'Speeder 100', 2022, '', 'Đỏ', 1, '', '', 45000, 'Xe đạp đường trường giá rẻ', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Merida+Speeder', 'AVAILABLE', 4.3, 10, NOW(), NOW());

-- Insert sample orders
INSERT INTO orders (user_id, vehicle_id, date_from, date_to, total_days, price_per_day, total_price, status, pickup_location, return_location, customer_name, customer_phone, customer_email, notes, created_at, updated_at) VALUES
(2, 1, '2026-01-15', '2026-01-18', 3, 500000, 1500000, 'CONFIRMED', 'Hà Nội', 'Hà Nội', 'Trần Thị B', '0907654321', 'user@example.com', 'Cần xe sạch sẽ', NOW(), NOW()),
(2, 5, '2026-01-20', '2026-01-22', 2, 150000, 300000, 'PENDING', 'Hà Nội', 'Hà Nội', 'Trần Thị B', '0907654321', 'user@example.com', '', NOW(), NOW());