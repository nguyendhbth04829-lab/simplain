# BÁO CÁO KHẢO SÁT VÀ KIỂM KÊ TOÀN DIỆN DỰ ÁN SIMPLAIN (SIMPL4)
**Tài liệu dành cho Dev: Minh | Mã phân công: Module 3 (US12 - US16)**  
*Ngày thực hiện khảo sát: 21/10/2026*

---

## 1. Cấu trúc tổng thể của project

Dự án **Simplain** (trong tài liệu nghiệp vụ còn gọi là **Simpl4**) là hệ thống thương mại điện tử thời trang được phát triển theo kiến trúc Spring Boot (Java 17+), kết hợp giữa dịch vụ RESTful API có tài liệu tương tác OpenAPI/Swagger UI và hệ thống quản trị CMS giao diện máy chủ (Server-side Rendering với Thymeleaf và Tailwind CSS).

### 1.1. Ngăn xếp công nghệ (Technology Stack)
- **Ngôn ngữ & Runtime**: Java 17+ (môi trường kiểm thử thực tế nhận diện JDK 23).
- **Framework nền tảng**: Spring Boot `4.1.1` (Spring Framework 7.x, Hibernate ORM 7.4.5, Tomcat 11 embedded).
- **Tầng dữ liệu (Persistence)**: Spring Data JPA, Hibernate ORM, Microsoft SQL Server JDBC Driver (`mssql-jdbc`).
- **Tầng giao diện & Web**: Spring Web MVC, Thymeleaf Template Engine, Tailwind CSS (qua CDN).
- **Tài liệu & Kiểm thử API**: Springdoc OpenAPI Starter WebMVC UI `3.1.1` (truy cập tại `/docs`, JSON OpenAPI tại `/v3/api-docs`).
- **Validation**: Jakarta Bean Validation (`spring-boot-starter-validation`).
- **Tiện ích**: Project Lombok (`@Data`, `@NoArgsConstructor`, `@AllArgsConstructor`, v.v.).
- **Build tool**: Apache Maven 3.9.16 thông qua Maven Wrapper (`mvnw`, `mvnw.cmd`).

### 1.2. Phân vùng cấu trúc thư mục
```text
E:\simplain\
├── .github\
│   └── pull_request_template.md        # Mẫu Pull Request chuẩn tiếng Việt
├── .idea\                              # Cấu hình dự án IntelliJ IDEA
├── .mvn\wrapper\
│   └── maven-wrapper.properties        # Cấu hình tải bản phân phối Maven 3.9.16
├── src\
│   ├── main\
│   │   ├── java\vn\fpoly\simplain\
│   │   │   ├── config\                 # Cấu hình hệ thống (Swagger, Resource upload)
│   │   │   ├── controller\             # Controller MVC / Thymeleaf của đồng đội
│   │   │   ├── entity\                 # Các JPA Entity mapping bảng CSDL
│   │   │   ├── hello\                  # Module mẫu chuẩn Spring REST API của dự án
│   │   │   ├── repository\             # Giao diện Spring Data JPA Repository
│   │   │   └── SimplainApplication.java# Lớp khởi chạy chính (Spring Boot Entry Point)
│   │   └── resources\
│   │       ├── application.properties  # Cấu hình cổng, kết nối CSDL, Swagger
│   │       └── templates\admin\        # Các trang giao diện HTML Thymeleaf Admin
│   └── test\
│       └── java\vn\fpoly\simplain\
│           ├── SimplainApplicationTests.java # Smoke test khởi tạo context
│           └── hello\HelloControllerTest.java# Test mẫu MockMvc REST API
├── uploads\                            # Thư mục chứa tệp tin tĩnh (ảnh banner...)
├── .editorconfig                       # Quy chuẩn format code chung
├── .gitattributes                      # Quy chuẩn ngắt dòng LF và mã hóa UTF-8
├── .gitignore                          # Danh sách bỏ qua trong Git
├── .gitmessage                         # Khung mẫu thông điệp commit chuẩn
├── HELP.md                             # Tài liệu mặc định của Spring Boot
├── HUONG-DAN-TEST-API.md               # Hướng dẫn kiểm thử qua Swagger UI
├── HUONG-DAN-THANH-VIEN-MOI.md         # Quy trình onboarding và làm việc hàng ngày
├── mvnw & mvnw.cmd                     # Script thực thi Maven Wrapper
├── opencode.json                       # Cấu hình phân quyền công cụ AI (OpenCode)
├── pom.xml                             # Tệp tin mô tả dự án và toàn bộ dependencies
├── QUY-DINH-NHANH-VA-COMMIT.md         # Quy định đặt tên branch và định dạng commit
├── QUY-TAC-VIET-CODE.md                # Quy tắc viết code chuẩn Spring, chú thích tiếng Việt
├── README.md                           # Giới thiệu tổng quan dự án
├── Simpl4_database_full_copy.sql       # Script DDL SQL 22 bảng CSDL trích xuất từ PDF
└── simplai_n product buclog.pdf        # Tài liệu Product Backlog gốc kèm DDL
```

---

## 2. Danh sách file đã kiểm kê

Toàn bộ 47 tệp tin và các thư mục hiện diện trong workspace đã được kiểm kê chi tiết:

| STT | Đường dẫn tương đối | Kích thước (bytes) | Phân loại | Khả năng đọc |
|:---:|:---|:---:|:---|:---|
| 1 | `README.md` | 2,179 | Markdown | Đọc trực tiếp |
| 2 | `HUONG-DAN-THANH-VIEN-MOI.md` | 3,180 | Markdown | Đọc trực tiếp |
| 3 | `QUY-DINH-NHANH-VA-COMMIT.md` | 3,226 | Markdown | Đọc trực tiếp |
| 4 | `QUY-TAC-VIET-CODE.md` | 3,928 | Markdown | Đọc trực tiếp |
| 5 | `HUONG-DAN-TEST-API.md` | 2,499 | Markdown | Đọc trực tiếp |
| 6 | `HELP.md` | 1,549 | Markdown | Đọc trực tiếp |
| 7 | `.github\pull_request_template.md` | 1,301 | Markdown | Đọc trực tiếp |
| 8 | `.editorconfig` | 487 | Config text | Đọc trực tiếp |
| 9 | `.gitattributes` | 700 | Config text | Đọc trực tiếp |
| 10 | `.gitignore` | 954 | Config text | Đọc trực tiếp |
| 11 | `.gitmessage` | 661 | Config text | Đọc trực tiếp |
| 12 | `opencode.json` | 172 | JSON Config | Đọc trực tiếp |
| 13 | `pom.xml` | 4,813 | Maven POM | Đọc trực tiếp |
| 14 | `mvnw` | 11,790 | Shell script | File build wrapper |
| 15 | `mvnw.cmd` | 8,481 | CMD script | File build wrapper |
| 16 | `.mvn\wrapper\maven-wrapper.properties` | 168 | Config text | Đọc trực tiếp |
| 17 | `Simpl4_database_full_copy.sql` | 11,619 | SQL Script | Đọc trực tiếp |
| 18 | `simplai_n product buclog.pdf` | 256,798 | Tài liệu PDF | Đọc qua công cụ chuyên dụng (`pypdf`) |
| 19 | `.idea\.gitignore` | 251 | Config text | Đọc trực tiếp |
| 20 | `.idea\awsToolkit.xml` | 311 | XML Config | Đọc trực tiếp |
| 21 | `.idea\compiler.xml` | 872 | XML Config | Đọc trực tiếp |
| 22 | `.idea\encodings.xml` | 191 | XML Config | Đọc trực tiếp |
| 23 | `.idea\jarRepositories.xml` | 864 | XML Config | Đọc trực tiếp |
| 24 | `.idea\misc.xml` | 478 | XML Config | Đọc trực tiếp |
| 25 | `.idea\vcs.xml` | 172 | XML Config | Đọc trực tiếp |
| 26 | `.idea\workspace.xml` | 3,746 | XML Config | Đọc trực tiếp |
| 27 | `src\main\resources\application.properties` | 693 | Config text | Đọc trực tiếp |
| 28 | `src\main\java\vn\fpoly\simplain\SimplainApplication.java` | 326 | Java Source | Đọc trực tiếp |
| 29 | `src\main\java\vn\fpoly\simplain\config\OpenApiConfig.java` | 1,044 | Java Source | Đọc trực tiếp |
| 30 | `src\main\java\vn\fpoly\simplain\config\WebConfig.java` | 523 | Java Source | Đọc trực tiếp |
| 31 | `src\main\java\vn\fpoly\simplain\entity\DanhMuc.java` | 1,000 | Java Source | Đọc trực tiếp |
| 32 | `src\main\java\vn\fpoly\simplain\entity\BoSuuTap.java` | 951 | Java Source | Đọc trực tiếp |
| 33 | `src\main\java\vn\fpoly\simplain\repository\DanhMucRepository.java` | 496 | Java Source | Đọc trực tiếp |
| 34 | `src\main\java\vn\fpoly\simplain\repository\BoSuuTapRepository.java` | 502 | Java Source | Đọc trực tiếp |
| 35 | `src\main\java\vn\fpoly\simplain\controller\DanhMucController.java` | 3,330 | Java Source | Đọc trực tiếp |
| 36 | `src\main\java\vn\fpoly\simplain\controller\BoSuuTapController.java` | 4,679 | Java Source | Đọc trực tiếp |
| 37 | `src\main\java\vn\fpoly\simplain\hello\HelloController.java` | 2,278 | Java Source | Đọc trực tiếp |
| 38 | `src\main\java\vn\fpoly\simplain\hello\HelloRequest.java` | 598 | Java Source | Đọc trực tiếp |
| 39 | `src\main\java\vn\fpoly\simplain\hello\HelloResponse.java` | 478 | Java Source | Đọc trực tiếp |
| 40 | `src\main\java\vn\fpoly\simplain\hello\HelloService.java` | 1,235 | Java Source | Đọc trực tiếp |
| 41 | `src\main\resources\templates\admin\danh-muc\hien-thi.html` | 8,906 | HTML/Thymeleaf | Đọc trực tiếp |
| 42 | `src\main\resources\templates\admin\danh-muc\view.html` | 4,580 | HTML/Thymeleaf | Đọc trực tiếp |
| 43 | `src\main\resources\templates\admin\bo-suu-tap\hien-thi.html` | 8,611 | HTML/Thymeleaf | Đọc trực tiếp |
| 44 | `src\main\resources\templates\admin\bo-suu-tap\view.html` | 4,663 | HTML/Thymeleaf | Đọc trực tiếp |
| 45 | `src\test\java\vn\fpoly\simplain\SimplainApplicationTests.java` | 220 | Java Test | Đọc trực tiếp |
| 46 | `src\test\java\vn\fpoly\simplain\hello\HelloControllerTest.java` | 2,618 | Java Test | Đọc trực tiếp |
| 47 | `uploads\1791627601949_sg-11134301-7reo3-m2xh8abdo3tz11.webp` | 58,308 | Image Binary | File nhị phân (ảnh) |

---

## 3. Những file đã đọc và mục đích của chúng

Toàn bộ nội dung của các tệp tin có thể đọc được đã được đọc đầy đủ từng dòng:

1. **`simplai_n product buclog.pdf`**:
   - *Mục đích*: Tài liệu nguồn gốc cao nhất của dự án. Trang 1 liệt kê 30 User Stories của hệ thống phân công cho 6 Dev (Duy, Nguyên, Minh, Trí, Hưng, Khoa), kèm theo vai trò, độ ưu tiên, Story Points và Tiêu chí chấp nhận (Acceptance Criteria). Trang 1–4 cung cấp toàn bộ kịch bản DDL tạo Database `Simpl4` và 22 bảng CSDL.
2. **`Simpl4_database_full_copy.sql`**:
   - *Mục đích*: File script SQL trích xuất từ PDF chứa định nghĩa toàn bộ 22 bảng, khóa chính identity, các ràng buộc unique, giá trị mặc định và 4 chỉ mục unique index.
3. **Bộ tài liệu quy chuẩn dự án (`README.md`, `HUONG-DAN-THANH-VIEN-MOI.md`, `QUY-DINH-NHANH-VA-COMMIT.md`, `QUY-TAC-VIET-CODE.md`, `HUONG-DAN-TEST-API.md`, `.github/pull_request_template.md`)**:
   - *Mục đích*: Quy định toàn bộ quy trình làm việc của nhóm:
     - Cách đặt tên nhánh: `<loai>/<mo-ta-ngan-gon-khong-dau>` (ví dụ: `tinh-nang/ho-so-khach-hang`).
     - Định dạng commit: `<loai>(<pham-vi>): <mo-ta-ngan-gon-tieng-viet>`.
     - Chuẩn code: Lớp theo chuẩn Spring (tiếng Anh), chú thích JavaDoc tiếng Việt có dấu, API phải có tài liệu Swagger (`@Tag`, `@Operation`, `@Schema`) tiếng Việt, DTO tách biệt, test tự động bằng `MockMvc`.
     - Quy định bảo vệ bí mật: Tuyệt đối không commit file `.env`, mật khẩu kết nối CSDL, token.
4. **`pom.xml`**:
   - *Mục đích*: Khai báo các thư viện phụ thuộc: Spring Boot Starter WebMVC, Thymeleaf, Validation, Springdoc OpenAPI, Lombok, Spring Data JPA, MSSQL JDBC driver, cấu hình plugin biên dịch Java 17.
5. **`src/main/resources/application.properties`**:
   - *Mục đích*: Cấu hình cổng server `8080`, đường dẫn Swagger `/docs` và `/v3/api-docs`, cấu hình datasource kết nối tới SQL Server `Simpl4`, JPA logging và Hibernate DDL auto update.
6. **Mã nguồn tính năng mẫu `hello` (`HelloController.java`, `HelloService.java`, `HelloRequest.java`, `HelloResponse.java`)**:
   - *Mục đích*: Thiết kế chuẩn mẫu (Architecture Blueprint) của nhóm để các thành viên tuân theo khi tạo API: Controller nhận request và gọi Service; Service xử lý logic; Request/Response là các Java Record có schema Swagger và annotation kiểm tra dữ liệu đầu vào.
7. **Mã nguồn US06 của đồng đội Nguyên (`DanhMuc.java`, `BoSuuTap.java`, `DanhMucRepository.java`, `BoSuuTapRepository.java`, `DanhMucController.java`, `BoSuuTapController.java`, `WebConfig.java`)**:
   - *Mục đích*: Hiện thực hóa tính năng CRUD Danh mục và Bộ sưu tập của Admin. Hỗ trợ upload ảnh banner lưu vào thư mục `uploads/`, phân trang và tìm kiếm.
8. **Mã nguồn giao diện Thymeleaf (`hien-thi.html`, `view.html`)**:
   - *Mục đích*: Cung cấp giao diện quản trị hai cột (form thêm mới bên trái, danh sách tìm kiếm/sửa/xóa bên phải) và trang cập nhật chi tiết, sử dụng Tailwind CSS.
9. **Mã nguồn kiểm thử (`HelloControllerTest.java`, `SimplainApplicationTests.java`)**:
   - *Mục đích*: Kiểm thử tích hợp khởi tạo ứng dụng và các kịch bản kiểm thử API `GET /api/hello`, `POST /api/hello` với MockMvc.
10. **Các file cấu hình môi trường & IDE (`.editorconfig`, `.gitattributes`, `.gitignore`, `.gitmessage`, `opencode.json`, `.idea/*`)**:
    - *Mục đích*: Thiết lập chuẩn format (UTF-8, LF, 4 spaces), quản lý Git và cấu hình SDK Java trong IntelliJ IDEA.

---

## 4. Các file không đọc được hoặc được loại trừ, kèm lý do

1. **`uploads\1791627601949_sg-11134301-7reo3-m2xh8abdo3tz11.webp`** (58,308 bytes):
   - *Lý do*: Đây là tệp tin hình ảnh nhị phân (WebP format) được tạo ra từ chức năng upload ảnh banner của `BoSuuTapController`. File này không chứa văn bản hay mã nguồn logic, đã được xác minh về mặt metadata tệp tin tĩnh.
2. **`mvnw`** (11,790 bytes) và **`mvnw.cmd`** (8,481 bytes):
   - *Lý do*: Script chuẩn của Apache Maven Wrapper để tự động tải Maven Runtime khi máy dev chưa cài Maven. Đã được kiểm tra tiêu đề và phân loại là file công cụ tự động, không chứa mã nguồn dự án.
3. **Thư mục biên dịch `target/`**:
   - *Lý do*: Thư mục chứa bytecode `.class`, báo cáo test `surefire-reports` và file sinh tự động sau lệnh build. Đã được loại trừ khỏi khảo sát mã nguồn theo nguyên tắc chuẩn (được định nghĩa trong `.gitignore`).

---

## 5. Kiến trúc frontend/backend và luồng giao tiếp

Hiện tại trong workspace có sự tồn tại song song của hai mô hình giao tiếp:

```
[Khách hàng / Trình duyệt]
       │
       ├─── (A) Gọi REST API (JSON) ───► @RestController (/api/...) ──► Service ──► DTO / Repository
       │                                       ▲ (Swagger Docs: /docs)
       │
       └─── (B) Giao diện Web Admin ────► @Controller (/admin/...) ────► Spring Data Repository ──► Trả về View Thymeleaf (HTML)
```

1. **Mô hình 1: RESTful API Quốc tế (Chuẩn hướng dẫn nhóm)**:
   - Đại diện bởi module `hello` (và nhánh `tri` với `paymentmethod`).
   - Giao tiếp qua HTTP Method (GET, POST, PUT, DELETE), dữ liệu định dạng JSON.
   - Tài liệu hóa hoàn toàn tự động bằng Swagger UI tại URL `http://localhost:8080/docs`.
   - Có DTO Request/Response riêng biệt, bắt lỗi bằng Jakarta Validation `@Valid`.
2. **Mô hình 2: Server-side Rendering MVC (Đồng đội triển khai tại US06)**:
   - Đại diện bởi `DanhMucController` và `BoSuuTapController`.
   - Điều hướng trực tiếp URL dạng `/admin/danh-muc/hien-thi`, `/admin/bo-suu-tap/hien-thi`.
   - Trả về mã HTML render từ template Thymeleaf kết hợp Tailwind CSS.
   - Luồng dữ liệu form submission thông qua `@ModelAttribute`, binding trực tiếp vào Entity.
3. **Quản lý Tệp tin tĩnh**:
   - Cấu hình qua [WebConfig.java](file:///e:/simplain/src/main/java/vn/fpoly/simplain/config/WebConfig.java) ánh xạ đường dẫn URL `/uploads/**` tới thư mục đĩa vật lý `file:uploads/`. Khi upload banner bộ sưu tập, tệp tin được ghi vào thư mục này với tên kèm timestamp tránh trùng lặp.

---

## 6. Database, các bảng quan trọng và các quan hệ đã xác minh

Cơ sở dữ liệu của dự án có tên **`Simpl4`** (Microsoft SQL Server), bao gồm **22 bảng dữ liệu**.

### 6.1. Bảng phân loại 22 bảng CSDL theo phân hệ
1. **Phân hệ Người dùng & Phân quyền**: `vai_tro`, `nhan_vien`, `khach_hang`, `dia_chi`.
2. **Phân hệ Sản phẩm & Danh mục**: `danh_muc`, `bo_suu_tap`, `mau_sac`, `kich_thuoc`, `san_pham`, `bien_the_san_pham`, `hinh_anh`.
3. **Phân hệ Bán hàng & Giỏ hàng**: `goi_combo`, `san_pham_trong_combo`, `gio_hang`, `chi_tiet_gio_hang`.
4. **Phân hệ Khuyến mãi**: `ma_giam_gia`, `voucher_so_huu`, `flash_sale`, `chi_tiet_flash_sale`.
5. **Phân hệ Đơn hàng & Thanh toán**: `trang_thai_don_hang`, `don_hang`, `chi_tiet_don_hang`, `lich_su_trang_thai_don_hang`, `phuong_thuc_thanh_toan`, `giao_dich_thanh_toan`.
6. **Phân hệ Hậu mãi & CSKH**: `yeu_cau_doi_tra`, `chi_tiet_san_pham_doi_tra`.
7. **Phân hệ Khách hàng thân thiết & Tương tác (Trọng tâm của Minh)**:
   - `san_pham_yeu_thich`: Lưu quan hệ Wishlist (`ma_khach_hang_id`, `ma_san_pham_id`).
   - `danh_gia_san_pham`: Đánh giá 1-5 sao, nhận xét và JSON danh sách ảnh theo `ma_chi_tiet_don_hang_id`.
   - `hang_thanh_vien`: Cấu hình hạng hội viên, chi tiêu tối thiểu, % hoàn tiền.
   - `ho_so_hoi_vien`: Quản lý `so_du_coolcash` và `tong_chi_tieu_nam` của từng khách hàng.
   - `bien_dong_diem`: Lịch sử cộng/trừ điểm Coolcash theo đơn hàng.
8. **Phân hệ Truyền thông**: `bai_viet_blog`, `banner_quang_cao`.

### 6.2. Các đặc điểm quan trọng của CSDL đã xác minh
- **Toàn bộ khóa chính** đều có kiểu `bigint PRIMARY KEY IDENTITY(1, 1)` (riêng bảng `ho_so_hoi_vien` dùng `ma_khach_hang_id` làm khóa chính, bảng `san_pham_trong_combo` dùng khóa chính kết hợp).
- **Tính toàn vẹn tham chiếu**: File DDL SQL và tài liệu PDF **không tạo ràng buộc khóa ngoại tường minh (`FOREIGN KEY REFERENCES`)** trong CSDL mà quản lý logic quan hệ thông qua mã ID cột (như `ma_khach_hang_id`, `ma_don_hang_id`...).
- **Chỉ mục duy nhất (Unique Index)** được thiết lập tường minh cho 4 nghiệp vụ chống trùng lặp:
  - `san_pham_yeu_thich_index_0`: UNIQUE(`ma_khach_hang_id`, `ma_san_pham_id`).
  - `voucher_so_huu_index_1`: UNIQUE(`ma_khach_hang_id`, `ma_giam_gia_id`).
  - `chi_tiet_flash_sale_index_2`: UNIQUE(`ma_flash_sale_id`, `ma_bien_the_id`).
  - `danh_gia_san_pham_index_3`: UNIQUE(`ma_chi_tiet_don_hang_id`).

---

## 7. Quy ước code và cách đồng đội triển khai các US hiện có

### 7.1. Đối chiếu quy ước chuẩn với thực tế triển khai
| Tiêu chí | Quy định chuẩn (`QUY-TAC-VIET-CODE.md`, `QUY-DINH-NHANH-VA-COMMIT.md`) | Đồng đội: Nguyên (`us-06-quanlydanhmuc-bosuutap`) | Đồng đội: Trí (`origin/tri`) |
|---|---|---|---|
| **Đặt tên nhánh** | `<loai>/<ten-viec-khong-dau>` (VD: `tinh-nang/danh-muc`) | `us-06-quanlydanhmuc-bosuutap` (Chưa đúng tiền tố `tinh-nang/`) | `tri` (Nhánh cá nhân) |
| **Định dạng commit** | `<loai>(<pham-vi>): <mo-ta-tieng-viet>` | `b20720d quản lý danh mục và bộ sưu tập` (Sai quy cách) | `7306525 tinh-nang(thanh-toan): us18 quan ly cong thanh toan` (Chuẩn) |
| **Đặt tên Class & Entity** | Chuẩn Spring tiếng Anh (`Category`, `Order`) | Đặt tên tiếng Việt không dấu: `DanhMuc`, `BoSuuTap` | Vừa có tiếng Anh (`PaymentMethod`), vừa có tiếng Việt (`PhuongThucThanhToan`) |
| **Phân tách tầng** | Controller -> Service -> Repository / DTO | Controller gọi thẳng Repository, không có Service, không dùng DTO | Có đầy đủ Controller, Service, Repository, DTO Request/Response |
| **Tài liệu Swagger** | Bắt buộc có `@Tag`, `@Operation`, `@Schema` | Không có (do viết Controller MVC trả về HTML Thymeleaf) | Đầy đủ Swagger tiếng Việt cho nhánh REST API |
| **Kiểm thử tự động** | Bắt buộc có ít nhất 1 test `MockMvc` trong `src/test` | Không viết bài kiểm thử nào cho US06 | Viết đầy đủ `PaymentMethodControllerTest` và `PaymentMethodServiceTest` |

### 7.2. Nhận xét và kinh nghiệm rút ra cho Minh
1. Việc đặt tên Entity bằng tiếng Việt (`DanhMuc`, `BoSuuTap`, `KhachHang`, `DiaChi`) bám sát 100% tên bảng trong file SQL DDL giúp cả nhóm dễ liên kết cột, nhưng cần lưu ý nếu triển khai REST API chuẩn thì nên tạo DTO tiếng Anh hoặc DTO rõ ràng để chuẩn hóa giao tiếp API.
2. Cần duy trì tầng Service xử lý nghiệp vụ, không nên gọi trực tiếp Repository từ Controller nhằm đảm bảo khả năng viết Unit Test độc lập với CSDL.

---

## 8. Trạng thái test, build và các vấn đề đã phát hiện

### 8.1. Kết quả kiểm tra Build và Test thực tế
- **Kết quả biên dịch (`javac` / `mvn compile`)**: **THÀNH CÔNG**. Toàn bộ mã nguồn Java và template không có lỗi cú pháp.
- **Kết quả kiểm thử tự động (`mvn test`)**: **THẤT BẠI (5 Errors / 5 Tests)**.
  - Các bài test thất bại gồm: `SimplainApplicationTests.contextLoads`, và toàn bộ 4 test trong `HelloControllerTest` (`getDefaultHello`, `greetByPath`, `greetByJson`, `rejectBlankName`).

### 8.2. Nguyên nhân cốt lõi của lỗi Test
1. Khi đồng đội đưa `spring-boot-starter-data-jpa` và driver `mssql-jdbc` vào `pom.xml` đồng thời thêm cấu hình kết nối CSDL trong `application.properties`:
   - `spring.datasource.url=jdbc:sqlserver://localhost:1433;databaseName=Simpl4;...`
   - Thông tin xác thực CSDL mặc định `sa`.
2. Do `@SpringBootTest` khởi tạo toàn bộ Spring Context thực tế, Spring Data JPA cố gắng mở kết nối mạng tới SQL Server `localhost:1433`. Vì máy tính cục bộ tại thời điểm chạy test chưa khởi chạy SQL Server hoặc tài khoản mật khẩu CSDL không khớp, HikariPool ném lỗi:
   ```text
   HHH000247: ErrorCode: 18456, SQLState: S0001
   Login failed for user 'sa'.
   Cannot create PoolableConnectionFactory
   ```
3. Context bị crash khiến mọi bài test tự động đều bị ngắt quãng.

### 8.3. Vấn đề bảo vệ thông tin bí mật
- Cần tuân thủ mục 3 của yêu cầu: Không lưu trữ tài khoản mật khẩu cứng trong mã nguồn đưa lên Git. Dự án cần có cấu hình hồ sơ riêng cho môi trường kiểm thử (ví dụ dùng H2 in-memory test database hoặc `@DataJpaTest` / Mock Service độc lập) để đảm bảo `mvn test` luôn chạy xanh mà không bắt buộc phải có máy chủ SQL Server đang kết nối.

---

## 9. Danh sách US chính xác thuộc phạm vi của Minh, căn cứ vào tài liệu

Căn cứ trang 1 của tài liệu gốc `simplai_n product buclog.pdf` và định nghĩa DDL trong `Simpl4_database_full_copy.sql`, toàn bộ **Module 3 (Mod 3)** bao gồm **5 User Stories** được giao cụ thể cho **Minh**:

```
┌───────┬──────────────┬──────────────────────────────┬──────────────┬──────────┬────┬────────────────────────────────────────────────────────┐
│ Mã US │ Module / Epic│ Tóm tắt nội dung chức năng   │ Vai trò      │ Ưu tiên  │ SP │ Tiêu chí chấp nhận cốt lõi (Acceptance Criteria)       │
├───────┼──────────────┼──────────────────────────────┼──────────────┼──────────┼────┼────────────────────────────────────────────────────────┤
│ US12  │ Mod 3 / Hồ sơ│ Cập nhật thông tin cá nhân   │ Khách hàng   │ Cao      │ 3  │ 1. Cập nhật Tên, SĐT, Email, Giới tính, Ngày sinh.     │
│       │              │                              │              │          │    │ 2. Cho phép Đổi mật khẩu.                              │
├───────┼──────────────┼──────────────────────────────┼──────────────┼──────────┼────┼────────────────────────────────────────────────────────┤
│ US13  │ Mod 3 / Địa  │ Quản lý sổ địa chỉ giao hàng │ Khách hàng   │ Cao      │ 5  │ 1. CRUD Sổ địa chỉ.                                    │
│       │       chỉ    │                              │              │          │    │ 2. Render dropdown Tỉnh/Huyện/Xã.                      │
│       │              │                              │              │          │    │ 3. Set cờ Địa chỉ mặc định.                            │
├───────┼──────────────┼──────────────────────────────┼──────────────┼──────────┼────┼────────────────────────────────────────────────────────┤
│ US14  │ Mod 3 / Wish-│ Thêm sản phẩm vào Yêu thích  │ Khách hàng   │ Thấp     │ 2  │ 1. Nhấn tim sản phẩm (Lưu san_pham_yeu_thich).         │
│       │       list   │                              │              │          │    │ 2. Hiển thị trang Danh sách yêu thích.                 │
├───────┼──────────────┼──────────────────────────────┼──────────────┼──────────┼────┼────────────────────────────────────────────────────────┤
│ US15  │ Mod 3 / Đánh │ Đánh giá sản phẩm đã mua     │ Khách hàng   │ TB       │ 5  │ 1. Check điều kiện ma_chi_tiet_don_hang_id hoàn thành. │
│       │       giá    │                              │              │          │    │ 2. Upload JSON ảnh.                                    │
│       │              │                              │              │          │    │ 3. Rating 1-5 sao.                                     │
├───────┼──────────────┼──────────────────────────────┼──────────────┼──────────┼────┼────────────────────────────────────────────────────────┤
│ US16  │ Mod 3 / Hội  │ Tự động tích điểm Loyalty    │ Hệ thống     │ TB       │ 8  │ 1. Đơn hàng hoàn thành cộng % điểm (so_du_coolcash).   │
│       │       viên   │ (Coolcash)                   │              │          │    │ 2. Đạt mốc tự động lên Hạng (hang_thanh_vien).         │
└───────┴──────────────┴──────────────────────────────┴──────────────┴──────────┴────┴────────────────────────────────────────────────────────┘
```

### Chi tiết các bảng CSDL phục vụ trực tiếp cho 5 US của Minh:
- **US12**: Bảng `khach_hang` (các trường: `ho_ten`, `so_dien_thoai`, `email`, `mat_khau_hash`, `gioi_tinh`, `ngay_sinh`, `anh_dai_dien`).
- **US13**: Bảng `dia_chi` (các trường: `ma_khach_hang_id`, `ten_nguoi_nhan`, `so_dien_thoai`, `tinh_thanh`, `quan_huyen`, `phuong_xa`, `dia_chi_chi_tiet`, `la_mac_dinh`).
- **US14**: Bảng `san_pham_yeu_thich` liên kết với bảng `san_pham` và `khach_hang`.
- **US15**: Bảng `danh_gia_san_pham` (các trường: `ma_san_pham_id`, `ma_khach_hang_id`, `ma_chi_tiet_don_hang_id`, `so_sao`, `noi_dung`, `danh_sach_anh_json`, `ngay_tao`).
- **US16**: Bảng `ho_so_hoi_vien`, `hang_thanh_vien`, `bien_dong_diem`, kết nối với sự kiện hoàn thành của bảng `don_hang`.

---

## 10. Các điểm cần xác minh và thống nhất trước khi triển khai

Trước khi bắt tay vào triển khai bất kỳ mã nguồn nào của Minh, các điểm then chốt sau đây cần được thống nhất:

1. **Xác định US đầu tiên**:
   - Theo trình tự logic và mức độ phụ thuộc dữ liệu: **US12 (Cập nhật thông tin cá nhân & Đổi mật khẩu của Khách hàng)** là US tiên quyết và cơ bản nhất. Thực thể `khach_hang` là gốc của mọi dữ liệu địa chỉ (US13), wishlist (US14), đánh giá (US15) và hồ sơ hội viên (US16).
2. **Hình thức triển khai (REST API vs Thymeleaf View)**:
   - Theo chuẩn tài liệu dự án `QUY-TAC-VIET-CODE.md`, toàn bộ API mới phải có Swagger và DTO chuẩn RESTful. Đồng thời, đồng đội (Nguyên và Trí) cũng đang làm thêm màn hình Admin Thymeleaf.
   - *Đề xuất*: Với US12 (chức năng của Khách hàng Storefront / Client Mobile/Web), triển khai chuẩn REST API có đầy đủ DTO (`CustomerProfileUpdateRequest`, `ChangePasswordRequest`), Service xử lý mã hóa mật khẩu và tài liệu Swagger UI. Nếu cần giao diện demo, có thể bổ sung trang view HTML tương ứng.
3. **Cơ chế xác thực Khách hàng (User Authentication Context)**:
   - US01 (Xác thực đăng nhập và cấp phát JWT token cho Khách hàng của Duy) hiện chưa hoàn thành hoặc chưa được merge vào nhánh này.
   - Do đó, trong giai đoạn triển khai US12 của Minh, giải pháp truyền thông tin nhận diện người dùng hiện tại (Current Customer) sẽ được xử lý linh hoạt (ví dụ: truyền `ma_khach_hang_id` qua Request Header hoặc Session/Param mô phỏng để dễ dàng tích hợp ngay khi US01 hoàn thiện).
4. **Cô lập CSDL khi chạy Test tự động**:
   - Cần cấu hình test profile (hoặc dùng `@WebMvcTest` kết hợp `@MockBean` service) để các bài kiểm thử unit test cho US của Minh luôn xanh 100% khi chạy `mvnw.cmd test`, không bị ảnh hưởng bởi lỗi kết nối SQL Server bên ngoài.
5. **Quy tắc tuân thủ Git**:
   - Tạo nhánh mới từ nhánh làm việc hiện tại theo đúng quy định: `tinh-nang/us12-ho-so-khach-hang`.
   - Cam kết không tự động chuyển sang US tiếp theo, không push code lên remote cho đến khi hoàn thành báo cáo nghiệm thu và có sự xác nhận từ bạn.
