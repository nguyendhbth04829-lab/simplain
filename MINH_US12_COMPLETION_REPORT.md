# BÁO CÁO NGHIỆM THU HOÀN THÀNH USER STORY 12 (US12)
**Tính năng: Hồ sơ khách hàng (Cập nhật thông tin cá nhân & Đổi mật khẩu) — Tiếng Việt 100%**  
**Người thực hiện: Minh | Module 3 (Storefront / Khách hàng)**  
*Thời điểm hoàn thành: 21/10/2026*

---

## 1. Nghiệp vụ và luồng hoạt động của US12

Căn cứ vào tài liệu đặc tả nghiệp vụ [`simplai_n product buclog.pdf`](file:///e:/simplain/simplai_n%20product%20buclog.pdf) (Trang 1):
- **User Story**: *"Là Khách hàng, tôi muốn cập nhật thông tin cá nhân"*
- **Tiêu chí chấp nhận cốt lõi (Acceptance Criteria)**:
  1. Cập nhật Tên, SĐT, Email, Giới tính, Ngày sinh.
  2. Cho phép Đổi mật khẩu.

Dự án đã được cấu trúc và đồng bộ bằng **tiếng Việt 100%** (từ tên lớp, dịch vụ, DTO, bảng mã ngoại lệ, thông điệp, đường dẫn API đến giao diện Thymeleaf HTML):

### 1.1. Luồng xem thông tin hồ sơ cá nhân
- **REST API**: `GET /api/khach-hang/ho-so`
- **Giao diện Web**: `GET /khach-hang/ho-so?id={id}`
- **Quy tắc**:
  - Nhận diện khách hàng qua header `X-Customer-Id` hoặc tham số `id`.
  - Thiếu định danh hoặc ID <= 0: Trả về HTTP `401 Unauthorized` (`ChuaDangNhapException`).
  - Không tìm thấy tài khoản: Trả về HTTP `404 Not Found` (`KhongTimThayException`).
  - Tài khoản bị khóa (`trang_thai == 0`): Trả về HTTP `403 Forbidden` (`TaiKhoanBiKhoaException`).
  - Trả về thông tin hồ sơ `HoSoKhachHangResponse` (loại bỏ trường nhạy cảm `mat_khau_hash`).

### 1.2. Luồng cập nhật thông tin cá nhân
- **REST API**: `PUT /api/khach-hang/cap-nhat`
- **Giao diện Web**: `POST /khach-hang/cap-nhat?id={id}`
- **Quy tắc kiểm tra**:
  - `hoTen`: Bắt buộc, không để trống (`@NotBlank`), tối đa 150 ký tự (`@Size`).
  - `soDienThoai`: Định dạng 10 chữ số chuẩn Việt Nam (`@Pattern`).
  - `email`: Đúng định dạng email (`@Email`), tối đa 150 ký tự.
  - `gioiTinh`: Tối đa 10 ký tự (`Nam`, `Nữ`, `Khác`).
  - `ngaySinh`: Phải là ngày trong quá khứ (`@Past`).
  - `anhDaiDien`: URL ảnh đại diện, tối đa 500 ký tự.
- **Quy tắc chống trùng lặp dữ liệu**:
  - Nếu email mới đã được sử dụng bởi khách hàng khác: Ném `XungDotDuLieuException` (HTTP `409 Conflict`).
  - Nếu SĐT mới đã được sử dụng bởi khách hàng khác: Ném `XungDotDuLieuException` (HTTP `409 Conflict`).
- Ghi nhận thông tin mới vào bảng `khach_hang` và trả về hồ sơ đã cập nhật.

### 1.3. Luồng đổi mật khẩu tài khoản
- **REST API**: `PUT /api/khach-hang/doi-mat-khau`
- **Giao diện Web**: `POST /khach-hang/doi-mat-khau?id={id}`
- **Quy tắc kiểm tra**:
  - `matKhauCu`, `matKhauMoi`, `xacNhanMatKhauMoi`: Bắt buộc không được để trống (`@NotBlank`).
  - `matKhauMoi`: Tối thiểu từ 6 đến 50 ký tự (`@Size(min = 6, max = 50)`).
  - Mật khẩu mới và xác nhận mật khẩu mới phải khớp nhau 100%. Nếu sai: Ném `DuLieuKhongHopLeException` (HTTP `400 Bad Request`).
  - Mật khẩu cũ phải trùng khớp với chuỗi băm trong CSDL. Nếu sai: Ném `DuLieuKhongHopLeException` (HTTP `400 Bad Request`).
  - Mật khẩu mới không được trùng với mật khẩu cũ. Nếu trùng: Ném `DuLieuKhongHopLeException` (HTTP `400 Bad Request`).
  - Băm mật khẩu mới bằng thuật toán chuẩn SHA-256 (64 ký tự hex), cập nhật cột `mat_khau_hash`.

---

## 2. Danh sách file đã tạo (Toàn bộ bằng Tiếng Việt)

| STT | Đường dẫn tệp tin | Mục đích |
|:---:|:---|:---|
| 1 | [`src/main/java/vn/fpoly/simplain/entity/KhachHang.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/entity/KhachHang.java) | Thực thể JPA ánh xạ bảng `khach_hang` |
| 2 | [`src/main/java/vn/fpoly/simplain/repository/KhachHangRepository.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/repository/KhachHangRepository.java) | Repository truy vấn CSDL khách hàng |
| 3 | [`src/main/java/vn/fpoly/simplain/util/MaHoaMatKhau.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/util/MaHoaMatKhau.java) | Tiện ích băm và xác thực mật khẩu SHA-256 |
| 4 | [`src/main/java/vn/fpoly/simplain/service/KhachHangService.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/service/KhachHangService.java) | Lớp dịch vụ xử lý nghiệp vụ US12 tiếng Việt |
| 5 | [`src/main/java/vn/fpoly/simplain/controller/KhachHangApiController.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/controller/KhachHangApiController.java) | Controller REST API với tài liệu Swagger tiếng Việt |
| 6 | [`src/main/java/vn/fpoly/simplain/controller/KhachHangController.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/controller/KhachHangController.java) | Controller phục vụ giao diện Web Thymeleaf |
| 7 | [`src/main/java/vn/fpoly/simplain/dto/HoSoKhachHangResponse.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/dto/HoSoKhachHangResponse.java) | DTO trả về thông tin chi tiết hồ sơ |
| 8 | [`src/main/java/vn/fpoly/simplain/dto/CapNhatHoSoRequest.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/dto/CapNhatHoSoRequest.java) | DTO yêu cầu cập nhật thông tin cá nhân |
| 9 | [`src/main/java/vn/fpoly/simplain/dto/DoiMatKhauRequest.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/dto/DoiMatKhauRequest.java) | DTO yêu cầu đổi mật khẩu tài khoản |
| 10 | [`src/main/java/vn/fpoly/simplain/dto/ThongBaoResponse.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/dto/ThongBaoResponse.java) | DTO trả về thông điệp thành công |
| 11 | [`src/main/java/vn/fpoly/simplain/dto/LoiPhanHoi.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/dto/LoiPhanHoi.java) | DTO thông điệp lỗi tiếng Việt chuẩn hóa |
| 12 | [`src/main/java/vn/fpoly/simplain/exception/KhongTimThayException.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/KhongTimThayException.java) | Ngoại lệ HTTP 404 tiếng Việt |
| 13 | [`src/main/java/vn/fpoly/simplain/exception/DuLieuKhongHopLeException.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/DuLieuKhongHopLeException.java) | Ngoại lệ HTTP 400 tiếng Việt |
| 14 | [`src/main/java/vn/fpoly/simplain/exception/ChuaDangNhapException.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/ChuaDangNhapException.java) | Ngoại lệ HTTP 401 tiếng Việt |
| 15 | [`src/main/java/vn/fpoly/simplain/exception/TaiKhoanBiKhoaException.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/TaiKhoanBiKhoaException.java) | Ngoại lệ HTTP 403 tiếng Việt |
| 16 | [`src/main/java/vn/fpoly/simplain/exception/XungDotDuLieuException.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/XungDotDuLieuException.java) | Ngoại lệ HTTP 409 tiếng Việt |
| 17 | [`src/main/java/vn/fpoly/simplain/exception/XuLyLoiHeThong.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/XuLyLoiHeThong.java) | Bộ xử lý lỗi tập trung `@RestControllerAdvice` |
| 18 | [`src/main/resources/templates/khach-hang/ho-so.html`](file:///e:/simplain/src/main/resources/templates/khach-hang/ho-so.html) | Giao diện Web xem và cập nhật hồ sơ khách hàng |
| 19 | [`src/main/resources/templates/khach-hang/doi-mat-khau.html`](file:///e:/simplain/src/main/resources/templates/khach-hang/doi-mat-khau.html) | Giao diện Web đổi mật khẩu tài khoản |
| 20 | [`src/test/java/vn/fpoly/simplain/util/MaHoaMatKhauTest.java`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/util/MaHoaMatKhauTest.java) | 4 Unit tests kiểm thử băm mật khẩu |
| 21 | [`src/test/java/vn/fpoly/simplain/service/KhachHangServiceTest.java`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/service/KhachHangServiceTest.java) | 11 Unit tests kiểm thử nghiệp vụ tầng Service |
| 22 | [`src/test/java/vn/fpoly/simplain/controller/KhachHangApiControllerTest.java`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/controller/KhachHangApiControllerTest.java) | 10 MockMvc tests kiểm thử REST API |
| 23 | [`src/test/java/vn/fpoly/simplain/controller/KhachHangControllerTest.java`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/controller/KhachHangControllerTest.java) | 4 MockMvc tests kiểm thử giao diện Web Thymeleaf |

---

## 3. Chi tiết API và Request / Response tiếng Việt

### 3.1. `GET /api/khach-hang/ho-so`
- **Tác dụng**: Lấy thông tin tài khoản khách hàng.
- **Header**: `X-Customer-Id: Long` (bắt buộc).
- **Phản hồi thành công (`200 OK`)**:
  ```json
  {
    "id": 1,
    "maKhachHang": "KH0001",
    "hoTen": "Nguyễn Văn A",
    "soDienThoai": "0987654321",
    "email": "nguyenvana@gmail.com",
    "anhDaiDien": "https://example.com/avatar.jpg",
    "gioiTinh": "Nam",
    "ngaySinh": "1995-08-15",
    "trangThai": 1,
    "ngayTao": "2026-10-21T09:00:00"
  }
  ```

### 3.2. `PUT /api/khach-hang/cap-nhat`
- **Tác dụng**: Cập nhật thông tin cá nhân.
- **Header**: `X-Customer-Id: Long` (bắt buộc).
- **Request Body**:
  ```json
  {
    "hoTen": "Nguyễn Văn A",
    "soDienThoai": "0912345678",
    "email": "nguyenvana.moi@gmail.com",
    "gioiTinh": "Nam",
    "ngaySinh": "1996-05-20",
    "anhDaiDien": "https://example.com/avatar_moi.jpg"
  }
  ```

### 3.3. `PUT /api/khach-hang/doi-mat-khau`
- **Tác dụng**: Đổi mật khẩu tài khoản.
- **Header**: `X-Customer-Id: Long` (bắt buộc).
- **Request Body**:
  ```json
  {
    "matKhauCu": "MatKhauCu123@",
    "matKhauMoi": "MatKhauMoi456@",
    "xacNhanMatKhauMoi": "MatKhauMoi456@"
  }
  ```
- **Phản hồi thành công (`200 OK`)**:
  ```json
  {
    "thongDiep": "Đổi mật khẩu thành công",
    "thoiGian": "2026-10-21T10:00:00"
  }
  ```

---

## 4. Các bảng Database sử dụng

US12 thao tác trực tiếp trên bảng **`khach_hang`** (bảo toàn 100% cấu trúc DDL có sẵn trong `Simpl4_database_full_copy.sql`):
- `id`: Khóa chính định danh.
- `ma_khach_hang`: Mã hiển thị (KHxxxx).
- `ho_ten`: Họ tên khách hàng (đọc/ghi).
- `so_dien_thoai`: SĐT liên hệ (đọc/ghi, kiểm tra chống trùng lặp).
- `email`: Địa chỉ email (đọc/ghi, kiểm tra chống trùng lặp).
- `mat_khau_hash`: Chuỗi băm mật khẩu SHA-256 (ghi nhận khi đổi mật khẩu).
- `anh_dai_dien`: Đường dẫn ảnh đại diện (đọc/ghi).
- `gioi_tinh`: Giới tính (đọc/ghi).
- `ngay_sinh`: Ngày sinh (đọc/ghi).
- `trang_thai`: Kiểm tra tài khoản (1: hoạt động, 0: bị khóa).

---

## 5. Kết quả kiểm thử tự động (29 / 29 PASS 100%)

Lệnh thực thi kiểm thử:
```powershell
.\mvnw.cmd test "-Dtest=MaHoaMatKhauTest,KhachHangServiceTest,KhachHangApiControllerTest,KhachHangControllerTest"
```

**Bằng chứng kết quả thực tế:**
```text
[INFO] Running vn.fpoly.simplain.controller.KhachHangApiControllerTest
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 2.649 s
[INFO] Running vn.fpoly.simplain.controller.KhachHangControllerTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.106 s
[INFO] Running vn.fpoly.simplain.service.KhachHangServiceTest
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.220 s
[INFO] Running vn.fpoly.simplain.util.MaHoaMatKhauTest
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.009 s
[INFO] 
[INFO] Results:
[INFO] Tests run: 29, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time: 7.674 s
[INFO] Finished at: 2026-10-21T10:04:55+07:00
```

---

## 6. Hướng dẫn kiểm thử thủ công trực quan

Khởi động ứng dụng:
```powershell
.\mvnw.cmd spring-boot:run
```

1. **Giao diện Web trực quan**:
   - Trang hồ sơ: mở trình duyệt tại `http://localhost:8080/khach-hang/ho-so?id=1`
   - Trang đổi mật khẩu: `http://localhost:8080/khach-hang/doi-mat-khau?id=1`
2. **Swagger UI**:
   - `http://localhost:8080/docs` (Nhóm **"Hồ sơ khách hàng"**)

---

## 7. Trạng thái nghiệm thu cuối cùng

**TRẠNG THÁI: PASS (ĐẠT HOÀN TOÀN — TIẾNG VIỆT 100%)**
- Toàn bộ tên lớp, phương thức, DTO, ngoại lệ, giao diện và tài liệu đã được chuyển sang tiếng Việt 100%.
- 29 bài kiểm thử tự động đều vượt qua thành công.
- Không tự ý commit và không push code lên remote.
