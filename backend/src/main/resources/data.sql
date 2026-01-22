-- ===================================================================
-- SAMPLE DATA FOR VEHICLE RENTAL SYSTEM
-- ===================================================================

-- Insert users
INSERT INTO users (full_name, email, password, phone, address, role, created_at, updated_at) VALUES
('Admin User', 'admin@example.com', 'admin123', '0901234567', 'Hà Nội', 'ADMIN', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Nguyễn Văn A', 'user@example.com', 'user123', '0907654321', 'Hồ Chí Minh', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Nguyễn Thái Sơn', 'thaison77@gmail.com', '12345678', '0909999999', 'Hà Nội', 'USER', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert vehicles - Ô TÔ
INSERT INTO vehicles (name, vehicle_type, brand, model, year, license_plate, color, seats, transmission, fuel_type, price_per_day, description, location, image_url, status, rating, total_rentals, created_at, updated_at) VALUES
('Toyota Vios 2023', 'OTO', 'Toyota', 'Vios', 2023, '30A-12345', 'Trắng', 5, 'Tự động', 'Xăng', 500000, 'Xe sedan tiết kiệm nhiên liệu, phù hợp gia đình', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Toyota+Vios', 'AVAILABLE', 4.5, 25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Honda City 2022', 'OTO', 'Honda', 'City', 2022, '29B-54321', 'Đen', 5, 'Tự động', 'Xăng', 480000, 'Xe sedan sang trọng, êm ái', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Honda+City', 'AVAILABLE', 4.7, 30, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Mazda 3 Sport', 'OTO', 'Mazda', '3', 2023, '30C-99999', 'Đỏ', 5, 'Tự động', 'Xăng', 600000, 'Xe sedan thể thao, thiết kế hiện đại', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Mazda+3', 'AVAILABLE', 4.8, 20, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Hyundai Accent', 'OTO', 'Hyundai', 'Accent', 2022, '51F-77777', 'Bạc', 5, 'Tự động', 'Xăng', 450000, 'Xe sedan giá rẻ, tiết kiệm', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Hyundai+Accent', 'AVAILABLE', 4.3, 18, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Toyota Camry', 'OTO', 'Toyota', 'Camry', 2023, '30D-11111', 'Xám', 5, 'Tự động', 'Xăng', 800000, 'Xe sedan hạng sang, sang trọng', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Toyota+Camry', 'AVAILABLE', 4.9, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert vehicles - XE MÁY
INSERT INTO vehicles (name, vehicle_type, brand, model, year, license_plate, color, seats, transmission, fuel_type, price_per_day, description, location, image_url, status, rating, total_rentals, created_at, updated_at) VALUES
('Honda Winner X', 'XEMAY', 'Honda', 'Winner X', 2023, '29-A1 12345', 'Đỏ đen', 2, 'Số sàn', 'Xăng', 150000, 'Xe số thể thao, mạnh mẽ', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Honda+Winner+X', 'AVAILABLE', 4.6, 45, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Yamaha Exciter 155', 'XEMAY', 'Yamaha', 'Exciter 155', 2023, '30-B2 54321', 'Xanh', 2, 'Số sàn', 'Xăng', 140000, 'Xe số phổ biến, bền bỉ', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Yamaha+Exciter', 'AVAILABLE', 4.5, 50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Honda SH Mode', 'XEMAY', 'Honda', 'SH Mode', 2022, '51-C1 99999', 'Trắng', 2, 'Tự động', 'Xăng', 120000, 'Xe tay ga tiện lợi, sang trọng', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Honda+SH+Mode', 'AVAILABLE', 4.4, 35, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Yamaha Janus', 'XEMAY', 'Yamaha', 'Janus', 2022, '51-D2 77777', 'Xanh mint', 2, 'Tự động', 'Xăng', 100000, 'Xe tay ga nữ tính, nhẹ nhàng', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Yamaha+Janus', 'AVAILABLE', 4.2, 40, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Honda Air Blade', 'XEMAY', 'Honda', 'Air Blade', 2023, '29-E3 88888', 'Đen', 2, 'Tự động', 'Xăng', 110000, 'Xe tay ga phổ thông, tiết kiệm', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Honda+Air+Blade', 'AVAILABLE', 4.3, 60, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert vehicles - XE ĐẠP
INSERT INTO vehicles (name, vehicle_type, brand, model, year, license_plate, color, seats, transmission, fuel_type, price_per_day, description, location, image_url, status, rating, total_rentals, created_at, updated_at) VALUES
('Giant Escape 3', 'XEDAP', 'Giant', 'Escape 3', 2023, '', 'Xanh dương', 1, '', '', 50000, 'Xe đạp thể thao, phù hợp đi phố', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Giant+Escape+3', 'AVAILABLE', 4.5, 15, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Trek FX 2', 'XEDAP', 'Trek', 'FX 2', 2023, '', 'Đen', 1, '', '', 55000, 'Xe đạp hybrid chất lượng cao', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Trek+FX+2', 'AVAILABLE', 4.6, 12, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Merida Speeder 100', 'XEDAP', 'Merida', 'Speeder 100', 2022, '', 'Đỏ', 1, '', '', 45000, 'Xe đạp đường trường giá rẻ', 'Hồ Chí Minh', 'https://via.placeholder.com/400x300?text=Merida+Speeder', 'AVAILABLE', 4.3, 10, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('Touring Bike Pro', 'XEDAP', 'Generic', 'Touring Pro', 2023, '', 'Xám', 1, '', '', 60000, 'Xe đạp touring chuyên nghiệp', 'Hà Nội', 'https://via.placeholder.com/400x300?text=Touring+Bike', 'AVAILABLE', 4.7, 8, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- Insert sample orders
INSERT INTO orders (user_id, vehicle_id, date_from, date_to, total_days, price_per_day, total_price, status, pickup_location, return_location, customer_name, customer_phone, customer_email, notes, created_at, updated_at) VALUES
(2, 1, '2026-01-15', '2026-01-18', 3, 500000, 1500000, 'CONFIRMED', 'Hà Nội', 'Hà Nội', 'Nguyễn Văn A', '0907654321', 'user@example.com', 'Cần xe sạch sẽ', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
(2, 6, '2026-01-20', '2026-01-22', 2, 150000, 300000, 'PENDING', 'Hà Nội', 'Hà Nội', 'Nguyễn Văn A', '0907654321', 'user@example.com', '', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);