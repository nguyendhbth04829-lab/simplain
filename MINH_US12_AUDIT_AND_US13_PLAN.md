# BÁO CÁO KIỂM TRA LẠI US12 VÀ KẾ HOẠCH TRIỂN KHAI US13
**Tác giả: Gia Minh | Phân hệ: Module 3 (Storefront & Khách hàng)**  
*Thời điểm thực hiện & cập nhật commit: 25/10/2026 - Gia Minh*

---

## PHẦN 1: BÁO CÁO KIỂM TRA LẠI US12 (HỒ SƠ KHÁCH HÀNG)

### 1. Cơ chế băm và xác minh mật khẩu
- **Hiện trạng mã nguồn**:
  - Lớp [`MaHoaMatKhau.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/util/MaHoaMatKhau.java) đang sử dụng thuật toán `MessageDigest.getInstance("SHA-256")` băm trực tiếp chuỗi mật khẩu thô sang chuỗi hex 64 ký tự.
- **Đánh giá tính an toàn và tương thích**:
  - **Hạn chế an toàn (Vấn đề bảo mật)**: SHA-256 thuần túy **không sử dụng Salt (muối)**. Mặc dù là hàm băm một chiều mật mã học, các chuỗi băm unsalted SHA-256 có nguy cơ bị tấn công dò tìm bằng bảng băm tính sẵn (Rainbow Table) hoặc tấn công vét cạn (brute-force) tốc độ cao nếu kẻ tấn công có được bản sao lưu CSDL.
  - **Tính tương thích với US01 (Đăng ký / Đăng nhập của Duy)**:
    - Trong tài liệu Product Backlog, US01 của Duy yêu cầu: *"1. Validate form. 2. Mã hóa mật khẩu. 3. Cấp phát và verify JWT Token."*
    - Hiện tại US01 chưa được hợp nhất vào nhánh làm việc. Nếu sau này Duy sử dụng `BCryptPasswordEncoder` (Spring Security), chuỗi băm của BCrypt (dạng `$2a$10$...`) sẽ không tương thích với chuỗi băm SHA-256 hex hiện tại của US12.
  - **Đề xuất nâng cấp an toàn & tương thích**:
    1. *Phương án tối ưu (Chuẩn Spring Security)*: Bổ sung thư viện `spring-security-crypto` vào `pom.xml` và sử dụng `BCryptPasswordEncoder`. Thuật toán này tự sinh salt ngẫu nhiên cho mỗi mật khẩu và chống vét cạn cực tốt.
    2. *Phương án giữ nguyên thư viện hiện tại*: Bổ sung Salt ngẫu nhiên bằng `SecureRandom` trong `MaHoaMatKhau` và lưu chuỗi dạng `salt:hash` vào cột `mat_khau_hash` (cột `varchar(255)` trong CSDL hoàn toàn đủ dung lượng lưu trữ).

### 2. Cấu hình xác thực, phân quyền và kiểm tra tài khoản bị khóa
- **Hiện trạng mã nguồn**:
  - Lớp [`KhachHangService.java`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/service/KhachHangService.java) thực hiện kiểm tra qua phương thức `layKhachHangHopLe`:
    - `idKhachHang == null || idKhachHang <= 0` ➔ ném [`ChuaDangNhapException`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/ChuaDangNhapException.java) (HTTP 401).
    - Không tìm thấy ID trong CSDL ➔ ném [`KhongTimThayException`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/KhongTimThayException.java) (HTTP 404).
    - `trangThai == 0` (tài khoản bị khóa) ➔ ném [`TaiKhoanBiKhoaException`](file:///e:/simplain/src/main/java/vn/fpoly/simplain/exception/TaiKhoanBiKhoaException.java) (HTTP 403).
- **Đánh giá bảo mật**:
  - **Hạn chế bảo mật (Thiếu lớp bảo vệ JWT)**: Hiện tại, API nhận diện khách hàng thông qua giá trị thô `X-Customer-Id` trên Header (hoặc `id` trên query param web). Do US01 chưa có sẵn trong project để cấp phát và verify JWT Token, hệ thống hiện tại chưa thể chứng thực được người gửi request có thực sự sở hữu `id` đó hay không (nguy cơ lỗi IDOR).
  - **Đề xuất**: Ngay khi US01 hoàn thành bộ lọc `JwtAuthenticationFilter`, `KhachHangApiController` sẽ được điều chỉnh để lấy `idKhachHang` trực tiếp từ `SecurityContext` thay vì đọc từ Request Header.

### 3. Đánh giá tính tương thích khi chuyển đổi 100% sang tiếng Việt
- Toàn bộ tên lớp, phương thức, DTO và ngoại lệ của US12 được đặt trong các gói:
  - `vn.fpoly.simplain.entity.KhachHang`
  - `vn.fpoly.simplain.repository.KhachHangRepository`
  - `vn.fpoly.simplain.service.KhachHangService`
  - `vn.fpoly.simplain.controller.KhachHangApiController`, `KhachHangController`
  - `vn.fpoly.simplain.dto.*`
  - `vn.fpoly.simplain.exception.*`
  - `vn.fpoly.simplain.util.MaHoaMatKhau`
- **Kết luận**: Các thành phần này hoàn toàn tách biệt, không sửa đổi hay ghi đè bất kỳ file nào của đồng đội (như `DanhMucController`, `BoSuuTapController`, `HelloController`). Không làm gián đoạn bất kỳ endpoint hay cấu hình nào của dự án.

### 4. Kết quả kiểm thử tự động thực tế
- Đã chạy thực tế bộ kiểm thử tự động:
  ```powershell
  .\mvnw.cmd test "-Dtest=MaHoaMatKhauTest,KhachHangServiceTest,KhachHangApiControllerTest,KhachHangControllerTest"
  ```
- **Kết quả**: **29 / 29 bài test PASS 100% (Thời gian chạy: 7.674s)**:
  - [`KhachHangApiControllerTest`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/controller/KhachHangApiControllerTest.java): 10/10 test đạt (kiểm thử 200 OK, 400 Bad Request, 401 Unauthorized, 404 Not Found, 409 Conflict, validation lỗi tên/email/sđt/mật khẩu).
  - [`KhachHangControllerTest`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/controller/KhachHangControllerTest.java): 4/4 test đạt (kiểm thử render view Thymeleaf và chuyển hướng POST form).
  - [`KhachHangServiceTest`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/service/KhachHangServiceTest.java): 11/11 test đạt (kiểm thử logic nghiệp vụ, tài khoản bị khóa, trùng email/sđt, sai mật khẩu cũ, mật khẩu mới trùng mật khẩu cũ).
  - [`MaHoaMatKhauTest`](file:///e:/simplain/src/test/java/vn/fpoly/simplain/util/MaHoaMatKhauTest.java): 4/4 test đạt (kiểm thử tính toàn vẹn hàm băm).

---

## PHẦN 2: KẾ HOẠCH TRIỂN KHAI US13 (QUẢN LÝ SỔ ĐỊA CHỈ)

### 1. Phạm vi nghiệp vụ của US13
Căn cứ tài liệu gốc `simplai_n product buclog.pdf` (Trang 1):
- **User Story**: *"Là Khách hàng, tôi muốn quản lý sổ địa chỉ giao hàng"*
- **Vai trò**: Khách hàng | **Độ ưu tiên**: Cao | **Story Points**: 5
- **Tiêu chí chấp nhận cốt lõi (Acceptance Criteria)**:
  1. **CRUD Sổ địa chỉ**: Xem danh sách địa chỉ của tôi, thêm địa chỉ mới, chỉnh sửa thông tin địa chỉ, xóa địa chỉ.
  2. **Render dropdown Tỉnh / Huyện / Xã**: Hỗ trợ chọn danh mục hành chính 3 cấp theo thứ tự phân cấp.
  3. **Set cờ Địa chỉ mặc định (`la_mac_dinh`)**: Đảm bảo mỗi khách hàng chỉ có duy nhất 1 địa chỉ mặc định tại một thời điểm.

### 2. Khảo sát cấu trúc thực tế của bảng `dia_chi`
Đã kiểm tra trực tiếp từ CSDL `Simpl4` trên SQL Server:
```sql
CREATE TABLE [dia_chi] (
    [id] bigint PRIMARY KEY IDENTITY(1, 1),
    [ma_khach_hang_id] bigint NOT NULL,
    [ten_nguoi_nhan] nvarchar(150) NOT NULL,
    [so_dien_thoai] varchar(20) NOT NULL,
    [tinh_thanh] nvarchar(100) NOT NULL,
    [quan_huyen] nvarchar(100) NOT NULL,
    [phuong_xa] nvarchar(100) NOT NULL,
    [dia_chi_chi_tiet] nvarchar(255) NOT NULL,
    [la_mac_dinh] bit DEFAULT (0)
)
```
- **Quan hệ bảng**:
  - `dia_chi.ma_khach_hang_id` ➔ Khóa ngoại logic tham chiếu đến `khach_hang.id`.
  - Mối quan hệ: Một khách hàng có thể có nhiều địa chỉ nhận hàng (1 - N).
- **Quy tắc cô lập dữ liệu (Data Isolation)**: Khách hàng chỉ được phép xem, sửa, xóa các bản ghi địa chỉ có `ma_khach_hang_id` trùng với ID của chính mình.

### 3. Quy tắc nghiệp vụ cờ Địa chỉ mặc định (`la_mac_dinh`)
1. **Thêm mới địa chỉ**:
   - Nếu đây là địa chỉ đầu tiên của khách hàng: Tự động gán `la_mac_dinh = true`.
   - Nếu khách hàng tích chọn `la_mac_dinh = true`: Hệ thống thực hiện câu lệnh gỡ cờ (`la_mac_dinh = false`) trên toàn bộ các địa chỉ hiện có của khách hàng đó, sau đó lưu địa chỉ mới với cờ `true`.
2. **Cập nhật địa chỉ**:
   - Nếu cập nhật một địa chỉ thành mặc định (`la_mac_dinh = true`): Gỡ cờ mặc định của tất cả các địa chỉ khác của khách hàng.
3. **Xóa địa chỉ**:
   - Nếu xóa một địa chỉ phụ (`la_mac_dinh = false`): Tiến hành xóa bình thường.
   - Nếu xóa địa chỉ đang là mặc định (`la_mac_dinh = true`): Tự động chọn địa chỉ còn lại gần nhất (theo ID) để gán làm mặc định mới, tránh tình trạng khách hàng còn địa chỉ nhưng không có địa chỉ mặc định.

### 4. Giải pháp cho Dropdown Tỉnh / Huyện / Xã
- Trong CSDL hiện tại **không có bảng danh mục hành chính**.
- **Giải pháp kiến trúc**:
  - *Tầng Backend*: Xây dựng `DanhMucHanhChinhService` cung cấp dữ liệu phân cấp 63 Tỉnh/Thành phố, Quận/Huyện, Phường/Xã chuẩn Việt Nam (từ file cấu hình/JSON nội bộ) thông qua endpoint hỗ trợ:
    - `GET /api/dia-chi/tinh-thanh`: Danh sách các tỉnh/thành.
    - `GET /api/dia-chi/quan-huyen?tinhThanh={ten}`: Danh sách quận/huyện theo tỉnh/thành.
    - `GET /api/dia-chi/phuong-xa?tinhThanh={ten}&quanHuyen={ten}`: Danh sách phường/xã.
  - *Tầng Frontend (Thymeleaf)*: Sử dụng 3 thẻ `<select>` kết hợp Javascript nhẹ lắng nghe sự kiện `change` để cập nhật cascading dropdown tức thì, tự nhiên và mượt mà.

### 5. Danh sách file dự kiến tạo mới cho US13 (100% Tiếng Việt)
1. **Entity**:
   - `src/main/java/vn/fpoly/simplain/entity/DiaChi.java`
2. **Repository**:
   - `src/main/java/vn/fpoly/simplain/repository/DiaChiRepository.java`
3. **DTOs**:
   - `src/main/java/vn/fpoly/simplain/dto/ThemDiaChiRequest.java`
   - `src/main/java/vn/fpoly/simplain/dto/CapNhatDiaChiRequest.java`
   - `src/main/java/vn/fpoly/simplain/dto/DiaChiResponse.java`
4. **Service**:
   - `src/main/java/vn/fpoly/simplain/service/DiaChiService.java`
   - `src/main/java/vn/fpoly/simplain/service/DanhMucHanhChinhService.java`
5. **Controllers**:
   - `src/main/java/vn/fpoly/simplain/controller/DiaChiApiController.java` (REST API cho Swagger / Client)
   - `src/main/java/vn/fpoly/simplain/controller/DiaChiController.java` (Giao diện Web Thymeleaf)
6. **Giao diện Thymeleaf HTML**:
   - `src/main/resources/templates/khach-hang/dia-chi/danh-sach.html`
   - `src/main/resources/templates/khach-hang/dia-chi/form.html`
7. **Kiểm thử tự động**:
   - `src/test/java/vn/fpoly/simplain/service/DiaChiServiceTest.java`
   - `src/test/java/vn/fpoly/simplain/controller/DiaChiApiControllerTest.java`
   - `src/test/java/vn/fpoly/simplain/controller/DiaChiControllerTest.java`

### 6. Các điểm cần xác nhận trước khi code US13
1. **Nguồn dữ liệu danh mục hành chính**: Bạn muốn danh mục Tỉnh/Huyện/Xã được nạp từ tệp dữ liệu chuẩn nội bộ của project (chạy offline độc lập không phụ thuộc mạng) hay gọi trực tiếp Open API bên ngoài?
2. **Giới hạn số lượng địa chỉ**: Dự án có quy định một khách hàng chỉ được lưu tối đa bao nhiêu địa chỉ (ví dụ tối đa 5 hoặc 10 địa chỉ) hay không giới hạn?
