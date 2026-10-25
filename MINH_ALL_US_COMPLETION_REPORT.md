# BÁO CÁO HOÀN THÀNH TOÀN BỘ CÁC USER STORY CỦA MINH (US12 — US16)
## MODULE 3: STOREFRONT & KHÁCH HÀNG

**Người thực hiện**: Gia Minh  
**Phạm vi hoàn thành**: Toàn bộ 5 User Story được phân công cho Minh:
- **US12**: Hồ sơ khách hàng & Đổi mật khẩu
- **US13**: Sổ địa chỉ nhận hàng (Cascading Dropdown Tỉnh/Huyện/Xã)
- **US14**: Danh sách sản phẩm yêu thích (Wishlist & Toggle Tim)
- **US15**: Đánh giá sản phẩm đã mua (1-5 sao, Nhận xét, JSON Ảnh, Điều kiện đơn hàng)
- **US16**: Khách hàng thân thiết Loyalty (Ví Coolcash, Tự động tích điểm, Tự động thăng hạng)
**Trạng thái**: Hoàn thành 100% — **117/117 ca kiểm thử tự động đạt (PASS)**  
**Quy chuẩn ngôn ngữ**: 100% Tiếng Việt (Tên lớp, phương thức, biến, DTO, view, tài liệu, thông báo lỗi)

---

## 1. TỔNG HỢP KẾT QUẢ TRIỂN KHAI THEO TỪNG USER STORY

### 1.1. US12 — Hồ sơ khách hàng & Đổi mật khẩu
- **Nghiệp vụ**:
  - Xem và cập nhật thông tin cá nhân: Họ tên, Số điện thoại (chuẩn 10 số VN), Email, Giới tính, Ngày sinh.
  - Đổi mật khẩu tài khoản với xác thực mật khẩu hiện tại, kiểm tra độ dài tối thiểu 6 ký tự và xác nhận mật khẩu mới.
  - Tiện ích băm mật khẩu SHA-256 (`MaHoaMatKhau.java`).
- **Giao diện & API**:
  - Web: `/khach-hang/ho-so`, `/khach-hang/doi-mat-khau`.
  - REST: `GET/PUT /api/khach-hang/ho-so`, `POST /api/khach-hang/doi-mat-khau`.
- **Số lượng test**: 29 tests PASS.

### 1.2. US13 — Sổ địa chỉ nhận hàng
- **Nghiệp vụ**:
  - CRUD địa chỉ giao hàng của khách hàng.
  - Tích hợp bộ dữ liệu phân cấp hành chính offline 63 tỉnh/thành (`danh-muc-hanh-chinh.json`).
  - Dropdown liên hoàn bắt buộc thứ tự Tỉnh/Thành → Huyện/Quận → Xã/Phường.
  - Quản lý địa chỉ mặc định trong Transaction: địa chỉ đầu tiên tự động mặc định; đặt mặc định tự động gỡ cờ các địa chỉ khác; xóa địa chỉ mặc định tự động kế thừa cho địa chỉ còn lại.
  - Cô lập dữ liệu: khách hàng này không thể xem/sửa/xóa địa chỉ khách hàng khác.
- **Giao diện & API**:
  - Web: `/khach-hang/dia-chi`, `/khach-hang/dia-chi/them`, `/khach-hang/dia-chi/sua/{id}`.
  - REST: `/api/khach-hang/dia-chi` (CRUD, đặt mặc định, danh mục hành chính 3 cấp).
- **Số lượng test**: 35 tests PASS.

### 1.3. US14 — Sản phẩm yêu thích (Wishlist)
- **Nghiệp vụ**:
  - Thêm sản phẩm vào yêu thích (thả tim) có kiểm tra trạng thái kinh doanh của sản phẩm.
  - Cơ chế Idempotent: thả tim lại không bị lỗi trùng lặp Unique Index `([ma_khach_hang_id], [ma_san_pham_id])`.
  - Bỏ thích (xóa khỏi wishlist).
  - Endpoint Toggle Tim và kiểm tra trạng thái tim rỗng / tim đỏ.
  - Màn hình lưới sản phẩm yêu thích kèm giá tiền, chất liệu, form dáng.
- **Giao diện & API**:
  - Web: `/khach-hang/yeu-thich`.
  - REST: `/api/khach-hang/yeu-thich` (GET danh sách, POST thêm, DELETE xóa, POST toggle, GET kiểm tra, GET đếm).
- **Số lượng test**: 26 tests PASS.

### 1.4. US15 — Đánh giá sản phẩm đã mua
- **Nghiệp vụ**:
  - Khách hàng gửi đánh giá cho sản phẩm từ chi tiết đơn hàng đã mua (`chi_tiet_don_hang`).
  - Kiểm tra điều kiện đơn hàng: Đơn hàng phải thuộc quyền sở hữu của khách hàng và đã ở trạng thái hoàn thành/thành công; chặn đánh giá trên đơn hàng đã hủy hoặc đang chờ.
  - Đánh giá từ 1 đến 5 sao kèm nhận xét chi tiết.
  - Đính kèm danh sách ảnh thực tế lưu dạng JSON mảng chuỗi (`danh_sach_anh_json`).
  - Chống đánh giá trùng lặp cho cùng một món hàng trong đơn (đảm bảo tính duy nhất qua index `[danh_gia_san_pham_index_3]`).
  - Thống kê điểm số sao trung bình và phân bố số lượng đánh giá 1-5 sao.
- **Giao diện & API**:
  - Web: `/khach-hang/danh-gia`, `/khach-hang/danh-gia/viet`.
  - REST: `POST /api/khach-hang/danh-gia`, `GET /api/khach-hang/danh-gia/san-pham/{id}`, `GET /api/khach-hang/danh-gia/san-pham/{id}/thong-ke`, `GET /api/khach-hang/danh-gia/cua-toi`.
- **Số lượng test**: 14 tests PASS.

### 1.5. US16 — Khách hàng thân thiết Loyalty & Điểm thưởng Coolcash
- **Nghiệp vụ**:
  - Quản lý các bậc hạng thành viên (`hang_thanh_vien`): Thành viên Mới (1%), Bạc (3%), Vàng (5%), Kim Cương (8%).
  - Tự động tích lũy điểm hoàn tiền Coolcash vào ví khi đơn hàng hoàn thành (quy đổi 1 điểm = 1 VNĐ).
  - Tự động cộng tổng chi tiêu năm và tự động thăng hạng thành viên khi vượt mốc chi tiêu tối thiểu.
  - Hỗ trợ đổi điểm Coolcash để giảm trừ tiền thanh toán đơn hàng.
  - Ghi nhật ký toàn bộ lịch sử biến động điểm (`bien_dong_diem`).
  - Hiển thị thẻ VIP ảo sang trọng, thanh tiến trình thăng hạng và bảng quyền lợi.
- **Giao diện & API**:
  - Web: `/khach-hang/hoi-vien`.
  - REST: `/api/khach-hang/hoi-vien`, `/api/khach-hang/hoi-vien/lich-su-diem`, `/api/khach-hang/hoi-vien/su-dung-diem`, `/api/khach-hang/hoi-vien/tich-diem-don-hang/{id}`, `/api/khach-hang/hoi-vien/hang-thanh-vien`.
- **Số lượng test**: 13 tests PASS.

---

## 2. BẢNG TỔNG HỢP CÁC ENDPOINT ĐỂ KIỂM THỬ (PORT 8080)

### 2.1. Giao diện Web Thymeleaf (Xem trực tiếp trên trình duyệt)
| Chức năng | Đường dẫn URL | Mô tả |
| :--- | :--- | :--- |
| **US12** | `http://localhost:8080/khach-hang/ho-so?id=1` | Trang hồ sơ cá nhân và cập nhật thông tin |
| **US12** | `http://localhost:8080/khach-hang/doi-mat-khau?id=1` | Màn hình đổi mật khẩu |
| **US13** | `http://localhost:8080/khach-hang/dia-chi?idKhachHang=1` | Sổ địa chỉ giao hàng của khách hàng |
| **US13** | `http://localhost:8080/khach-hang/dia-chi/them?idKhachHang=1` | Form thêm địa chỉ (Cascading dropdown 3 cấp) |
| **US14** | `http://localhost:8080/khach-hang/yeu-thich?idKhachHang=1` | Danh sách sản phẩm yêu thích (Wishlist) |
| **US15** | `http://localhost:8080/khach-hang/danh-gia?idKhachHang=1` | Lịch sử đánh giá của khách hàng |
| **US15** | `http://localhost:8080/khach-hang/danh-gia/viet?idKhachHang=1&maSanPhamId=1&maChiTietDonHangId=1` | Form viết đánh giá sản phẩm |
| **US16** | `http://localhost:8080/khach-hang/hoi-vien?idKhachHang=1` | Thẻ hội viên VIP, ví điểm Coolcash, bảng quyền lợi |

### 2.2. Tài liệu Swagger UI
- [http://localhost:8080/docs](http://localhost:8080/docs) (Đầy đủ 5 nhóm API tiếng Việt: Hồ sơ khách hàng, Sổ địa chỉ, Sản phẩm yêu thích, Đánh giá sản phẩm, Hội viên & Điểm thưởng Coolcash).

---

## 3. BẰNG CHỨNG KIỂM THỬ THỰC TẾ

Lệnh thực thi toàn bộ test suite của Minh:
```powershell
.\mvnw.cmd test "-Dtest=HoiVienLoyaltyServiceTest,HoiVienLoyaltyApiControllerTest,HoiVienLoyaltyControllerTest,DanhGiaSanPhamServiceTest,DanhGiaSanPhamApiControllerTest,DanhGiaSanPhamControllerTest,SanPhamYeuThichServiceTest,SanPhamYeuThichApiControllerTest,SanPhamYeuThichControllerTest,DiaChiServiceTest,DiaChiApiControllerTest,DiaChiControllerTest,MaHoaMatKhauTest,KhachHangServiceTest,KhachHangApiControllerTest,KhachHangControllerTest"
```

Kết quả thực tế ghi nhận từ Maven Surefire:
```text
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- DanhGiaSanPhamApiControllerTest (US15)
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- DanhGiaSanPhamControllerTest (US15)
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0 -- DiaChiApiControllerTest (US13)
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0 -- DiaChiControllerTest (US13)
[INFO] Tests run: 5, Failures: 0, Errors: 0, Skipped: 0 -- HoiVienLoyaltyApiControllerTest (US16)
[INFO] Tests run: 2, Failures: 0, Errors: 0, Skipped: 0 -- HoiVienLoyaltyControllerTest (US16)
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 -- KhachHangApiControllerTest (US12)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 -- KhachHangControllerTest (US12)
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0 -- SanPhamYeuThichApiControllerTest (US14)
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0 -- SanPhamYeuThichControllerTest (US14)
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- DanhGiaSanPhamServiceTest (US15)
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0 -- DiaChiServiceTest (US13)
[INFO] Tests run: 6, Failures: 0, Errors: 0, Skipped: 0 -- HoiVienLoyaltyServiceTest (US16)
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0 -- KhachHangServiceTest (US12)
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0 -- SanPhamYeuThichServiceTest (US14)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0 -- MaHoaMatKhauTest (US12)
[INFO] 
[INFO] Results:
[INFO] Tests run: 117, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  23.088 s
[INFO] Finished at: 2026-10-22T13:57:55+07:00
[INFO] ------------------------------------------------------------------------
```
- **Tổng số bài test tự động**: **117/117 test PASS 100%**.
- **Không có bất kỳ lỗi nào** (Failures = 0, Errors = 0, Skipped = 0).

---

## 4. CAM KẾT VÀ BÀN GIAO

1. Toàn bộ 5 User Story của Minh (US12, US13, US14, US15, US16) đã hoàn thành trọn vẹn, chạy được cả Web Thymeleaf và REST API Swagger.
2. Mã nguồn tuân thủ 100% tiếng Việt, giữ nguyên tính toàn vẹn của CSDL, không tự ý sửa đổi schema hay chạy script DDL nguy hiểm.
3. Giữ nguyên toàn bộ mã nguồn của các đồng đội trong dự án.
4. Chưa thực hiện `git commit` hay `git push` lên repository từ xa; toàn bộ mã nguồn đang nằm an toàn tại nhánh cục bộ `Giaminh`.
