# BÁO CÁO HOÀN THÀNH TRIỂN KHAI US14 — SẢN PHẨM YÊU THÍCH (WISHLIST)

**Người thực hiện**: Gia Minh (Module 3 — Storefront & Khách hàng)  
**Phạm vi hoàn thành**: Duy nhất **US14 — Sản phẩm yêu thích (Wishlist)**  
**Trạng thái**: Hoàn thành 100% — Đã kiểm thử và chạy thực tế 91/91 test đạt (PASS)  
**Quy chuẩn ngôn ngữ**: 100% Tiếng Việt (Tên lớp, phương thức, biến, DTO, view, tài liệu, thông báo lỗi)

---

## 1. TỔNG QUAN NGHIỆP VỤ US14

Chức năng **Sản phẩm yêu thích (Wishlist)** cho phép khách hàng lưu lại những sản phẩm họ quan tâm hoặc yêu thích để tiện theo dõi, so sánh và quyết định mua sắm sau:

1. **Thêm sản phẩm vào yêu thích (Thả tim)**:
   - Khách hàng nhấn biểu tượng trái tim trên sản phẩm -> lưu vào bảng `san_pham_yeu_thich`.
   - **Tính Idempotent**: Hệ thống kiểm tra trước xem sản phẩm đã có trong danh sách yêu thích chưa. Nếu đã có sẵn, trả về thông tin hiện tại mà không bị lỗi vi phạm ràng buộc Unique Constraint của cơ sở dữ liệu.
   - **Xác thực sản phẩm**: Kiểm tra sản phẩm có tồn tại trong CSDL và đang ở trạng thái kinh doanh (`kich_hoat == true`). Không cho phép lưu sản phẩm đã ngừng kinh doanh.
2. **Xóa sản phẩm khỏi yêu thích (Bỏ tim)**:
   - Khách hàng có thể xóa sản phẩm khỏi danh sách yêu thích bất cứ lúc nào.
   - Chỉ xóa sản phẩm thuộc về chính khách hàng đó.
3. **Bật / Tắt trạng thái yêu thích (Toggle Tim)**:
   - Hỗ trợ endpoint toggle chuyên biệt cho nút bấm trái tim trên giao diện Storefront: nếu chưa thích thì thêm vào, nếu đã thích thì xóa đi, trả về trạng thái boolean trực tiếp cho client cập nhật giao diện mà không cần gọi nhiều API.
4. **Kiểm tra trạng thái yêu thích & Đếm số lượng**:
   - Cung cấp API kiểm tra nhanh một sản phẩm cụ thể đã được khách hàng thả tim hay chưa để hiển thị tim rỗng (♡) hoặc tim đỏ (♥).
   - Cung cấp API đếm tổng số sản phẩm yêu thích để hiển thị huy hiệu (badge count) trên header/thanh điều hướng.
5. **Hiển thị trang Danh sách yêu thích**:
   - Giao diện Web hiển thị lưới sản phẩm thời trang gọn gàng: tên sản phẩm, mã SKU, giá bán cơ bản, chất liệu, form dáng, trạng thái còn bán, nút bỏ yêu thích.
   - Trạng thái danh sách rỗng thân thiện khi khách hàng chưa lưu sản phẩm nào.

---

## 2. CƠ SỞ DỮ LIỆU VÀ ÁNH XẠ ENTITY

### 2.1. Cấu trúc bảng CSDL `Simpl4`
Theo kịch bản CSDL gốc `Simpl4_database_full_copy.sql`:

1. **Bảng `san_pham_yeu_thich`**:
   - `id` (bigint, Identity, Khóa chính)
   - `ma_khach_hang_id` (bigint, NOT NULL): ID khách hàng
   - `ma_san_pham_id` (bigint, NOT NULL): ID sản phẩm
   - `ngay_them` (datetime2, mặc định `GETDATE()`): Thời điểm thêm
   - **Unique Index**:
     ```sql
     CREATE UNIQUE INDEX [san_pham_yeu_thich_index_0] ON [san_pham_yeu_thich] ([ma_khach_hang_id], [ma_san_pham_id])
     ```
     Đảm bảo 1 khách hàng chỉ có thể lưu 1 sản phẩm vào danh sách yêu thích đúng 1 lần.

2. **Bảng `san_pham`**:
   - `id` (bigint, Identity, Khóa chính)
   - `ma_danh_muc_id` (bigint, NOT NULL)
   - `ma_bo_suu_tap_id` (bigint, NULL)
   - `ma_san_pham` (varchar(50), UNIQUE)
   - `ten_san_pham` (nvarchar(255), NOT NULL)
   - `duong_dan_slug` (varchar(255), UNIQUE)
   - `chat_lieu` (nvarchar(150))
   - `form_dang` (nvarchar(100))
   - `gia_co_ban` (decimal(18,2), NOT NULL)
   - `noi_bat` (bit, mặc định 0)
   - `kich_hoat` (bit, mặc định 1)
   - `ngay_tao` (datetime2)

### 2.2. Ánh xạ JPA Entity
- `SanPham.java`: Ánh xạ trực tiếp bảng `san_pham`, sử dụng các kiểu dữ liệu nguyên bản (`BigDecimal`, `LocalDateTime`).
- `SanPhamYeuThich.java`: Ánh xạ trực tiếp bảng `san_pham_yeu_thich`.
- Ánh xạ trường ID dạng số (`Long maKhachHangId`, `Long maSanPhamId`) theo quy chuẩn chung của dự án, không định nghĩa Foreign Key tự sinh gây phá vỡ CSDL hiện có.

---

## 3. BẢO MẬT VÀ CÔ LẬP KHÁCH HÀNG

1. **Xác thực danh tính**:
   - REST API nhận định danh qua HTTP Header `X-Customer-Id`.
   - Web Controller nhận qua `@RequestParam Long idKhachHang`.
   - Kiểm tra `KhachHangService.layKhachHangHopLe(idKhachHang)`: ném `ChuaDangNhapException` (401) nếu thiếu ID, ném `TaiKhoanBiKhoaException` (403) nếu tài khoản bị khóa (`trangThai == 0`).
2. **Cô lập dữ liệu**:
   - Mọi thao tác truy vấn và xóa đều ràng buộc kép `(maKhachHangId, maSanPhamId)`.
   - Khách hàng A không thể xem hoặc xóa sản phẩm trong danh sách yêu thích của khách hàng B.

---

## 4. DANH SÁCH FILE ĐÃ TẠO VÀ CHỈNH SỬA CHO US14

| STT | Đường dẫn file | Vai trò / Chức năng |
| :--- | :--- | :--- |
| 1 | `src/main/java/vn/fpoly/simplain/entity/SanPham.java` | Thực thể JPA ánh xạ bảng `san_pham` |
| 2 | `src/main/java/vn/fpoly/simplain/entity/SanPhamYeuThich.java` | Thực thể JPA ánh xạ bảng `san_pham_yeu_thich` |
| 3 | `src/main/java/vn/fpoly/simplain/repository/SanPhamRepository.java` | Repository truy vấn sản phẩm theo ID, mã, slug, danh sách ID |
| 4 | `src/main/java/vn/fpoly/simplain/repository/SanPhamYeuThichRepository.java` | Repository truy vấn danh sách yêu thích, kiểm tra tồn tại, xóa |
| 5 | `src/main/java/vn/fpoly/simplain/dto/ThemYeuThichRequest.java` | DTO yêu cầu thêm sản phẩm vào yêu thích có validation |
| 6 | `src/main/java/vn/fpoly/simplain/dto/SanPhamYeuThichResponse.java` | DTO phản hồi chi tiết sản phẩm kèm thông tin giá, chất liệu, form |
| 7 | `src/main/java/vn/fpoly/simplain/dto/TrangThaiYeuThichResponse.java` | DTO phản hồi trạng thái thả tim/toggle |
| 8 | `src/main/java/vn/fpoly/simplain/service/SanPhamYeuThichService.java` | Service nghiệp vụ CRUD yêu thích, toggle, kiểm tra, đếm số lượng |
| 9 | `src/main/java/vn/fpoly/simplain/controller/SanPhamYeuThichApiController.java` | REST API Controller chuẩn OpenAPI/Swagger tiếng Việt |
| 10 | `src/main/java/vn/fpoly/simplain/controller/SanPhamYeuThichController.java` | Controller giao diện Web Thymeleaf |
| 11 | `src/main/resources/templates/khach-hang/yeu-thich/danh-sach.html` | Màn hình lưới sản phẩm yêu thích, trạng thái rỗng, nút bỏ thích |
| 12 | `src/main/resources/templates/khach-hang/ho-so.html` | Thêm liên kết điều hướng sang trang Yêu thích |
| 13 | `src/main/resources/templates/khach-hang/dia-chi/danh-sach.html` | Thêm liên kết điều hướng sang trang Yêu thích |
| 14 | `src/test/java/vn/fpoly/simplain/service/SanPhamYeuThichServiceTest.java` | 13 ca kiểm thử đơn vị tầng nghiệp vụ SanPhamYeuThichService |
| 15 | `src/test/java/vn/fpoly/simplain/controller/SanPhamYeuThichApiControllerTest.java` | 10 ca kiểm thử tầng REST API MockMvc |
| 16 | `src/test/java/vn/fpoly/simplain/controller/SanPhamYeuThichControllerTest.java` | 3 ca kiểm thử tầng Web Controller MockMvc |

---

## 5. DANH SÁCH ENDPOINT API VÀ GIAO DIỆN WEB

### 5.1. REST API (`/api/khach-hang/yeu-thich`)
*(Yêu cầu Header `X-Customer-Id: 1`)*
- `GET /api/khach-hang/yeu-thich`: Lấy danh sách sản phẩm yêu thích của khách hàng
- `POST /api/khach-hang/yeu-thich/{maSanPhamId}`: Thêm sản phẩm vào yêu thích (qua URL)
- `POST /api/khach-hang/yeu-thich`: Thêm sản phẩm vào yêu thích (qua Body JSON `{"maSanPhamId": 1}`)
- `DELETE /api/khach-hang/yeu-thich/{maSanPhamId}`: Xóa sản phẩm khỏi yêu thích
- `POST /api/khach-hang/yeu-thich/toggle/{maSanPhamId}`: Bật/tắt trạng thái thả tim (Toggle)
- `GET /api/khach-hang/yeu-thich/kiem-tra/{maSanPhamId}`: Kiểm tra sản phẩm đã thích chưa
- `GET /api/khach-hang/yeu-thich/dem`: Đếm số lượng sản phẩm yêu thích

### 5.2. Giao diện Web Thymeleaf (`/khach-hang/yeu-thich`)
- `GET /khach-hang/yeu-thich?idKhachHang={id}`: Màn hình danh sách sản phẩm yêu thích
- `POST /khach-hang/yeu-thich/them`: Submit thêm sản phẩm vào yêu thích
- `POST /khach-hang/yeu-thich/xoa`: Submit xóa sản phẩm khỏi yêu thích

---

## 6. BẰNG CHỨNG KIỂM THỬ THỰC TẾ

Lệnh thực thi kiểm thử:
```powershell
.\mvnw.cmd test "-Dtest=SanPhamYeuThichServiceTest,SanPhamYeuThichApiControllerTest,SanPhamYeuThichControllerTest,DiaChiServiceTest,DiaChiApiControllerTest,DiaChiControllerTest,MaHoaMatKhauTest,KhachHangServiceTest,KhachHangApiControllerTest,KhachHangControllerTest"
```

Kết quả thực tế ghi nhận từ Maven Surefire:
```text
[INFO] Running vn.fpoly.simplain.controller.SanPhamYeuThichApiControllerTest (US14)
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.controller.SanPhamYeuThichControllerTest (US14)
[INFO] Tests run: 3, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.service.SanPhamYeuThichServiceTest (US14)
[INFO] Tests run: 13, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.controller.DiaChiApiControllerTest (US13)
[INFO] Tests run: 12, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.controller.DiaChiControllerTest (US13)
[INFO] Tests run: 7, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.service.DiaChiServiceTest (US13)
[INFO] Tests run: 16, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.controller.KhachHangApiControllerTest (US12)
[INFO] Tests run: 10, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.controller.KhachHangControllerTest (US12)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.service.KhachHangServiceTest (US12)
[INFO] Tests run: 11, Failures: 0, Errors: 0, Skipped: 0
[INFO] Running vn.fpoly.simplain.util.MaHoaMatKhauTest (US12)
[INFO] Tests run: 4, Failures: 0, Errors: 0, Skipped: 0
[INFO] 
[INFO] Results:
[INFO] Tests run: 91, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
```
- **Tổng số ca kiểm thử**: **91/91 test PASS 100%** (26 test US14, 35 test US13, 30 test US12).
- **Failures**: 0, **Errors**: 0, **Skipped**: 0.

---

## 7. CAM KẾT VÀ BÀN GIAO

1. Đã tuân thủ nghiêm ngặt **100% tiếng Việt** trong toàn bộ code mới, DTO, phương thức, thông báo lỗi và giao diện.
2. Hoàn thành trọn vẹn **duy nhất US14 — Sản phẩm yêu thích**.
3. **KHÔNG** làm lan sang US15 (Đánh giá & Bình luận) hay US16 (Điểm thưởng tích lũy).
4. **KHÔNG** tự ý thực hiện `git commit` hay `git push` lên repository từ xa. Mã nguồn hiện đang nằm an toàn trên nhánh cục bộ `Giaminh`.
