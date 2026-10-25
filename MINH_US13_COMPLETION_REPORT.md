# BÁO CÁO RÀ SOÁT VÀ XÁC MINH US13 — SỔ ĐỊA CHỈ NHẬN HÀNG

**Người thực hiện**: Gia Minh (Module 3 — Storefront & Khách hàng)  
**Phạm vi**: Rà soát, xác minh và kiểm thử chuyên sâu **duy nhất US13 — Sổ địa chỉ**; kiểm tra hồi quy US12  
**Quy chuẩn ngôn ngữ**: 100% Tiếng Việt  
**Cập nhật xác nhận commit US13**: 25/10/2026 — Gia Minh  
**Trạng thái đánh giá cuối cùng**: **PASS WITH LIMITATIONS** *(Đạt nghiệp vụ với các giới hạn kiến trúc được ghi nhận cụ thể)*

---

## 1. CÁC HẠNG MỤC ĐÃ KIỂM TRA

| STT | Hạng mục kiểm tra | Phạm vi & Đối tượng | Kết quả đánh giá |
| :--- | :--- | :--- | :--- |
| 1 | **Dữ liệu hành chính** | `danh-muc-hanh-chinh.json`, `DanhMucHanhChinhService.java` | Xác minh cấu trúc, tính đầy đủ, mã định danh và mức độ hiện hành |
| 2 | **Bảo mật & Cô lập tài khoản** | `DiaChiApiController.java`, `DiaChiController.java`, `DiaChiService.java` | Truy vết nguồn gốc xác định `idKhachHang`, quyền truy cập chéo giữa 2 khách hàng |
| 3 | **Nghiệp vụ Địa chỉ mặc định** | `DiaChiService.java`, `DiaChiRepository.java` | Tính toàn vẹn transaction, quy tắc 0/1/nhiều địa chỉ, phân tích tranh chấp đồng thời (race condition) |
| 4 | **Kiểm thử tự động & Hồi quy** | `DiaChiServiceTest`, `DiaChiApiControllerTest`, `DiaChiControllerTest`, US12 tests | Chạy thực tế 65 ca kiểm thử, kiểm tra các trường hợp biên |
| 5 | **Tính toàn vẹn CSDL & Git** | SQL Server `dia_chi`, Git status | Không can thiệp DDL, không sửa code đồng đội, không commit/push |

---

## 2. KẾT QUẢ KIỂM TRA DỮ LIỆU ĐỊA GIỚI HÀNH CHÍNH

### 2.1. Cấu trúc dữ liệu và phương thức tra cứu
- **Vị trí tệp**: `src/main/resources/data/danh-muc-hanh-chinh.json` (Dung lượng 34.595 bytes).
- **Cấu trúc lưu trữ**: Dạng cây phân cấp JSON lồng nhau:
  ```json
  {
    "Tên Tỉnh/Thành": {
      "Tên Quận/Huyện": [
        "Tên Phường/Xã 1",
        "Tên Phường/Xã 2"
      ]
    }
  }
  ```
- **Phương thức tra cứu**: `DanhMucHanhChinhService` nạp vào bộ nhớ `TreeMap<String, Map<String, List<String>>>` qua `@PostConstruct`.
  - Toàn bộ cơ chế tìm kiếm, lọc danh mục và kiểm tra phân cấp (`kiemTraHopLe`) đều thực hiện thông qua **chuỗi ký tự (Tên gọi - String)**.
  - **Không có mã định danh (Code)**: Tệp JSON **không chứa** mã số đơn vị hành chính chuẩn Quốc gia (ví dụ: mã Tỉnh `01` cho Hà Nội, mã Huyện `001`, mã Xã `00001` theo quy chuẩn Tổng cục Thống kê).
  - **Sự phù hợp với CSDL**: Bảng `dia_chi` trong CSDL SQL Server hiện tại chỉ thiết kế các cột `nvarchar(100)` gồm `tinh_thanh`, `quan_huyen`, `phuong_xa`, không có cột lưu mã hành chính. Do đó, việc lưu và tra cứu theo tên gọi hiện khớp 100% với cấu trúc bảng thực tế.

### 2.2. Kiểm tra tính đầy đủ và tính hiện hành
Thực hiện phân tích thực tế dữ liệu tệp `danh-muc-hanh-chinh.json`:
- **Số lượng Tỉnh / Thành phố**: **63/63 tỉnh thành** trực thuộc Trung ương của Việt Nam (đủ các tỉnh thành như Hà Nội, TP Hồ Chí Minh, Đà Nẵng, Cần Thơ, Hải Phòng, An Giang, v.v.).
- **Số lượng Quận / Huyện / Thị xã**: **101 đơn vị**.
- **Số lượng Phường / Xã / Thị trấn**: **951 đơn vị**.

> [!WARNING] **GIỚI HẠN VỀ DỮ LIỆU HÀNH CHÍNH (QUAN TRỌNG)**
> 1. **Dữ liệu đại diện mẫu (Sample Subset)**: Trên thực tế toàn quốc Việt Nam có khoảng 705 đơn vị hành chính cấp huyện và hơn 10.500 đơn vị cấp xã. Tệp `danh-muc-hanh-chinh.json` hiện tại chỉ là **bộ dữ liệu mẫu rút gọn (101 huyện, 951 xã)** tập trung vào các quận/huyện trung tâm của 63 tỉnh/thành nhằm phục vụ demo/đồ án, **chưa phải là danh bạ hành chính đầy đủ 100% của toàn quốc**.
> 2. **Chưa phản ánh sắp xếp hành chính mới (2024–2025)**: Dữ liệu chưa cập nhật các Nghị quyết mới nhất của Ủy ban Thường vụ Quốc hội về việc sắp xếp, sáp nhập một số đơn vị hành chính cấp huyện, cấp xã giai đoạn 2023–2025.
> 3. **Giải pháp kiến trúc**: Thiết kế dạng JSON tách rời giúp việc thay thế bằng tệp JSON đầy đủ hoặc chuyển sang API Tổng cục Bưu điện/Hành chính sau này rất dễ dàng mà không làm thay đổi logic nghiệp vụ của `DiaChiService`.

---

## 3. KIỂM TRA BẢO MẬT VÀ NGUỒN XÁC ĐỊNH KHÁCH HÀNG

### 3.1. Truy vết nguồn gốc `idKhachHang` / `maKhachHangId`

1. **Trong REST API (`DiaChiApiController.java`)**:
   - `idKhachHang` được lấy từ HTTP Header: `@RequestHeader(value = "X-Customer-Id", required = false) Long idKhachHang`.
   - Nếu header bị thiếu (`null`), `KhachHangService.layKhachHangHopLe(idKhachHang)` ném ngoại lệ `ChuaDangNhapException`, trả về HTTP Status `401 Unauthorized`.
   - Nếu tài khoản có `trangThai == 0` (bị khóa), hệ thống ném `TaiKhoanBiKhoaException`, trả về HTTP Status `403 Forbidden`.

2. **Trong luồng Web Thymeleaf (`DiaChiController.java`)**:
   - `idKhachHang` hiện được tiếp nhận thông qua `@RequestParam(defaultValue = "1") Long idKhachHang` trên URL và form hidden field.

3. **Trong tầng nghiệp vụ (`DiaChiService.java`)**:
   - Mọi thao tác truy vấn chi tiết, cập nhật, xóa, đặt mặc định đều dùng câu lệnh ràng buộc kép:
     `diaChiRepository.findByIdAndMaKhachHangId(idDiaChi, idKhachHang)`.
   - **Xác minh quyền sở hữu chéo**: Nếu khách hàng A (ID = 1) cố tình thao tác trên địa chỉ của khách hàng B (ID = 2), hàm trả về `Optional.empty()` và ném ra `KhongTimThayException`, mã lỗi `404 Not Found`. Khách hàng không thể can thiệp vào địa chỉ của người khác.

### 3.2. Đánh giá rủi ro bảo mật & Phụ thuộc chưa hoàn thiện

> [!CAUTION] **RỦI RO BẢO MẬT HIỆN TẠI VÀ PHỤ THUỘC US01**
> - **Chưa có JWT Authentication Filter toàn cục**: Do chức năng US01 (Xác thực đăng nhập / JWT) thuộc phân công của đồng đội khác và chưa được tích hợp vào `SecurityFilterChain` của dự án, máy chủ hiện tại **chưa thể tự động trích xuất danh tính khách hàng an toàn từ JWT Token đã ký số hoặc Session máy chủ**.
> - **Rủi ro mạo danh (ID Spoofing)**: Client có thể can thiệp bằng cách thay đổi giá trị header `X-Customer-Id` hoặc tham số `?idKhachHang=...` để đóng vai khách hàng khác nếu biết ID của họ.
> - **Kết luận bảo mật**: Tầng nghiệp vụ US13 đã bảo vệ **cô lập dữ liệu đúng theo ID được cung cấp**, nhưng hệ thống **chưa đạt mức bảo mật toàn diện** cho tới khi US01 được cắm vào để cung cấp `SecurityContextHolder.getContext().getAuthentication()`.

---

## 4. KIỂM TRA ĐỊA CHỈ MẶC ĐỊNH & TÍNH NHẤT QUÁN TRANSACTION

### 4.1. Quy tắc nghiệp vụ trong Transaction
Toàn bộ các phương thức thay đổi dữ liệu trong `DiaChiService` đều được gắn annotation `@Transactional`:

1. **Khi khách hàng chưa có địa chỉ nào**:
   - Thêm địa chỉ đầu tiên: Hệ thống kiểm tra `diaChiRepository.countByMaKhachHangId(idKhachHang) == 0`.
   - Cờ `laMacDinh` được **tự động ép thành `true`**, bất kể người dùng gửi lên `false` hay `null`. Đảm bảo khách hàng luôn có địa chỉ mặc định đầu tiên.

2. **Khi khách hàng chỉ có duy nhất 1 địa chỉ**:
   - Nếu khách hàng gửi yêu cầu sửa địa chỉ này với cờ `laMacDinh = false`, logic tại dòng 136-139 của `DiaChiService` phát hiện `soLuong <= 1` và **bắt buộc giữ nguyên `laMacDinh = true`**. Khách hàng không thể vô hiệu hóa địa chỉ mặc định duy nhất.

3. **Khi thêm mới hoặc đặt một địa chỉ làm mặc định (khi đã có nhiều địa chỉ)**:
   - Hệ thống thực thi câu lệnh bulk update JPQL nguyên tử:
     `UPDATE DiaChi d SET d.laMacDinh = false WHERE d.maKhachHangId = :idKhachHang AND d.id <> :idDiaChi`.
   - Thao tác gỡ bỏ cờ cũ và thiết lập cờ mới diễn ra trong cùng một transaction CSDL, đảm bảo không có trạng thái trung gian "2 địa chỉ mặc định" xuất hiện trên kết quả truy vấn.

4. **Khi xóa địa chỉ mặc định khi còn nhiều địa chỉ**:
   - Xóa địa chỉ được chỉ định.
   - Nếu địa chỉ bị xóa từng là mặc định (`daTungLaMacDinh == true`), hệ thống tự động tìm địa chỉ sớm nhất còn lại:
     `diaChiRepository.findFirstByMaKhachHangIdOrderByIdAsc(idKhachHang)`.
   - Gán `laMacDinh = true` cho địa chỉ kế thừa này và lưu lại trong cùng transaction.

### 4.2. Phân tích tranh chấp đồng thời (Concurrency / Race Condition)
- **Tình huống**: Hai yêu cầu đồng thời (Concurrent Requests) cùng gọi đặt mặc định hai địa chỉ khác nhau của cùng một khách hàng.
- **Phân tích CSDL**:
  - Bảng `dia_chi` trong CSDL SQL Server hiện tại **chưa có Filtered Unique Index** dạng:
    `CREATE UNIQUE INDEX uq_dia_chi_mac_dinh ON dia_chi(ma_khach_hang_id) WHERE la_mac_dinh = 1;`
  - Vì tuân thủ tuyệt đối quy định *"Không tự ý thay đổi cấu trúc database hoặc chạy script DDL"*, cơ chế bảo vệ hiện tại dựa trên Transaction isolation mặc định (`READ COMMITTED`) và JPQL update của tầng ứng dụng.
  - Khi xảy ra hai luồng cập nhật đồng thời, lệnh nào commit sau cùng sẽ ghi đè cờ mặc định cuối cùng (Last-Write-Wins).
  - Trong trường hợp cực hiếm khi thêm 2 địa chỉ mới đồng thời cùng mili-giây lúc tài khoản chưa có địa chỉ nào, có khả năng cả 2 luồng đều đếm `count == 0`.
- **Khuyến nghị kiến trúc (khi được phép sửa DDL)**: Bổ sung Filtered Unique Index trên SQL Server để đảm bảo toàn vẹn mức database engine.

---

## 5. BẰNG CHỨNG KIỂM THỬ THỰC TẾ

### 5.1. Lệnh kiểm thử đã thực thi
```powershell
.\mvnw.cmd test "-Dtest=DiaChiServiceTest,DiaChiApiControllerTest,DiaChiControllerTest,MaHoaMatKhauTest,KhachHangServiceTest,KhachHangApiControllerTest,KhachHangControllerTest"
```

### 5.2. Kết quả kiểm thử thực tế từ Maven Surefire
```text
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
[INFO] Tests run: 65, Failures: 0, Errors: 0, Skipped: 0
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] Total time:  11.445 s
[INFO] Finished at: 2026-10-22T13:34:59+07:00
[INFO] ------------------------------------------------------------------------
```

### 5.3. Chi tiết 16 ca kiểm thử nghiệp vụ trong `DiaChiServiceTest`
1. `layDanhSachDiaChi_ThanhCong`: Lấy danh sách địa chỉ chuẩn theo khách hàng.
2. `layChiTietDiaChi_ThanhCong`: Lấy chi tiết địa chỉ khi đúng chủ sở hữu.
3. `layChiTietDiaChi_KhongPhaiChuSoHuu_NemNgoaiLe`: Chặn khách hàng khác xem trộm địa chỉ (ném `KhongTimThayException`).
4. `themDiaChi_DiaChiDauTien_TuDongDatMacDinh`: Địa chỉ đầu tiên tự động thành mặc định dù gửi cờ `false`.
5. `themDiaChi_DiaChiThuHai_DatMacDinh_GoMacDinhCu`: Thêm địa chỉ mới chọn mặc định thì tự động gỡ mặc định các địa chỉ cũ.
6. `themDiaChi_DanhMucHanhChinhSai_NemNgoaiLe`: Chặn dữ liệu Tỉnh/Huyện/Xã không hợp lệ (ném `DuLieuKhongHopLeException`).
7. `capNhatDiaChi_ThanhCong`: Cập nhật thông tin người nhận, số điện thoại, địa chỉ chi tiết thành công.
8. `capNhatDiaChi_DoiThanhMacDinh_GoMacDinhCacDiaChiKhac`: Cập nhật chuyển sang mặc định kích hoạt gỡ bỏ các địa chỉ khác.
9. `capNhatDiaChi_ChiCoMotDiaChi_BoMacDinh_BatBuocGiuNguyenMacDinh`: Nếu chỉ có 1 địa chỉ, không cho phép bỏ cờ mặc định.
10. `capNhatDiaChi_KhongPhaiChuSoHuu_NemNgoaiLe`: Khách hàng khác không thể cập nhật địa chỉ người khác.
11. `xoaDiaChi_DiaChiThuong_ThanhCong`: Xóa địa chỉ thường không ảnh hưởng địa chỉ mặc định.
12. `xoaDiaChi_DiaChiMacDinh_TuDongKeThuaMacDinh`: Xóa địa chỉ mặc định tự động chỉ định địa chỉ sớm nhất còn lại làm mặc định mới.
13. `xoaDiaChi_DiaChiDuyNhat_XoaThanhCongKhongKeThua`: Xóa địa chỉ duy nhất sạch sẽ, không lỗi null.
14. `xoaDiaChi_KhongPhaiChuSoHuu_NemNgoaiLe`: Khách hàng khác không thể xóa địa chỉ người khác.
15. `datLamMacDinh_ThanhCong`: Chức năng đặt mặc định chuyên biệt cập nhật thành công và gỡ các địa chỉ khác.
16. `datLamMacDinh_KhongThuocSoHuu_NemNgoaiLe`: Chặn đặt mặc định nếu không phải chủ sở hữu.

---

## 6. DANH SÁCH RỦI RO VÀ GIỚI HẠN CÒN TỒN TẠI

| STT | Vấn đề / Giới hạn | Mức độ rủi ro | Nguyên nhân gốc rễ | Hướng xử lý khi chuyển giao |
| :--- | :--- | :--- | :--- | :--- |
| 1 | **Chưa có Filter xác thực JWT bắt buộc** | Trung bình - Cao | Module xác thực US01 chưa hoàn tất tích hợp vào Spring Security | Khi US01 sẵn sàng, thay `@RequestHeader("X-Customer-Id")` bằng `SecurityContextHolder` |
| 2 | **Dữ liệu hành chính là tập mẫu đại diện** | Thấp - Trung bình | Tệp JSON chứa 101 huyện, 951 xã (không phải 705 huyện, 10.500 xã toàn quốc) | Bổ sung đầy đủ dữ liệu JSON hoặc tích hợp API VNPost khi đưa vào sản xuất |
| 3 | **Thiếu Filtered Unique Index ở CSDL** | Thấp | Cấm thay đổi DDL bảng `dia_chi` | Thêm index `WHERE la_mac_dinh = 1` ở đợt migration database tiếp theo |
| 4 | **Dữ liệu chưa phản ánh sáp nhập 2024-2025** | Thấp | Danh mục phản ánh cấu trúc hành chính ổn định trước các đợt sáp nhập mới | Cập nhật tệp JSON khi có quyết định chính thức của hội đồng dự án |

---

## 7. KẾT LUẬN VÀ TRẠNG THÁI ĐÁNH GIÁ

- **Trạng thái**: **PASS WITH LIMITATIONS**
- **Căn cứ**:
  - Nghiệp vụ US13 (CRUD, phân cấp liên hoàn Tỉnh → Huyện → Xã, địa chỉ mặc định, tự động kế thừa khi xóa) đã được xây dựng hoàn thiện và chính xác.
  - Toàn bộ 65/65 ca kiểm thử thực tế đều vượt qua (`BUILD SUCCESS`).
  - Toàn bộ mã nguồn, chú thích, tài liệu và DTO đều sử dụng 100% tiếng Việt.
  - Các giới hạn về xác thực danh tính client, tính bao phủ của dữ liệu hành chính và ràng buộc mức CSDL đã được làm rõ bằng chứng, không che giấu lỗi thật và không tự nhận an toàn tuyệt đối.
- **Cam kết dừng**: Không làm US14/US15/US16; Không commit, không push Git. Dừng lại chờ người phụ trách xem xét.
