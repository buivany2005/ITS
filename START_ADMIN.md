# 🚀 HƯỚNG DẪN KHỞI ĐỘNG & TEST TRANG ADMIN

## Bước 1: Khởi động Docker
```bash
cd docker
docker-compose up -d
```

Đợi 30 giây để các services khởi động.

## Bước 2: Kiểm tra trạng thái
```bash
docker-compose ps
```

Đảm bảo tất cả services đang **Up**.

## Bước 3: Truy cập trang Admin

### 🔗 Link Admin Pages:

1. **Quản lý đơn hàng:**
   http://localhost/admin/quan_ly_don_hang.html

2. **Quản lý phương tiện:**
   http://localhost/admin/quan_ly_phuong_tien.html

3. **Dashboard/Thống kê:**
   http://localhost/admin/quan_ly_xe.html

### 🔌 API Endpoints để test:

```bash
# Lấy danh sách tất cả đơn hàng
curl http://localhost:8080/api/admin/orders

# Lấy đơn hàng theo status
curl http://localhost:8080/api/admin/orders?status=PENDING

# Lấy danh sách xe
curl http://localhost:8080/api/admin/vehicles

# Lấy xe theo category
curl http://localhost:8080/api/admin/vehicles?category=xemay
```

## 🧪 Test các chức năng:

### 1. Test Quản lý đơn hàng:
- ✅ Xem danh sách đơn hàng
- ✅ Lọc theo trạng thái (Chờ duyệt, Đang thuê, Hoàn thành, Đã hủy)
- ✅ Cập nhật trạng thái đơn hàng
- ✅ Xuất báo cáo Excel
- ✅ Tìm kiếm đơn hàng

### 2. Test Quản lý xe:
- ✅ Xem danh sách xe
- ✅ Lọc theo loại xe (Tất cả, Xe máy, Ô tô, Xe đạp)
- ✅ Tìm kiếm xe
- ✅ Xóa xe

## 🐛 Debug:

### Nếu không thấy dữ liệu:
1. Mở DevTools (F12)
2. Vào tab Console xem lỗi
3. Vào tab Network xem API calls
4. Kiểm tra backend logs:
   ```bash
   docker-compose logs backend
   ```

### Nếu API trả về 404:
- Kiểm tra backend đã chạy chưa
- Kiểm tra port đúng chưa (8080 cho Docker, 8081 cho local)

## ⚙️ Tài khoản test:

**Admin:**
- Email: admin@vehiclerental.com
- Password: admin

**User thường:**
- Email: user1@gmail.com  
- Password: user1

## 📊 Database Management:

**phpMyAdmin:**
- URL: http://localhost:5050
- Username: root
- Password: rootpass

---

**Chúc bạn test thành công! 🎉**
