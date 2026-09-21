# HƯỚNG DẪN THÀNH VIÊN MỚI — Từ git clone tới chạy được dự án

> File này tiếng Việt 100%. Tên file viết không dấu để tránh lỗi trên Windows và git.
> Đọc thêm: `QUY-DINH-NHANH-VA-COMMIT.md`, `QUY-TAC-VIET-CODE.md`, `HUONG-DAN-TEST-API.md`.

## 1. Chuẩn bị một lần duy nhất

1. Cài Java 17 trở lên. Kiểm tra:
   ```powershell
   java -version
   ```
2. Cài git. Kiểm tra:
   ```powershell
   git --version
   ```
3. Không cần cài Maven riêng. Dự án dùng sẵn `mvnw.cmd` (Windows) và `mvnw` (Linux/Mac).

## 2. Lấy code về máy (git clone)

```powershell
# 1. Clone (thay DIA-CHI-REPO bằng link thật của nhóm)
git clone <DIA-CHI-REPO>
Set-Location Simplain

# 2. Kiểm tra nhánh
git branch -a

# 3. Dùng mẫu commit chung của nhóm (chỉ làm 1 lần)
git config commit.template .gitmessage
```

> Lần đầu mở dự án bằng IntelliJ: chọn `Open` → trỏ tới thư mục `Simplain` → mở file `pom.xml` dưới dạng project → chờ IDE tải thư viện xong.

## 3. Chạy dự án

```powershell
# Cach 1: chay truc tiep
.\mvnw.cmd spring-boot:run

# Cach 2: chay file SimplainApplication.java tu IDE (nut Run)
```

Mở trình duyệt:

- Trang chào mẫu (JSON): http://localhost:8080/api/hello
- Tài liệu API kiểu FastAPI docs (Swagger): http://localhost:8080/docs
- File JSON OpenAPI thô: http://localhost:8080/v3/api-docs

## 4. Quy trình làm việc mỗi ngày (bắt buộc theo 1 form)

```powershell
# Buoc 1: ve nhanh main moi nhat
git checkout main
git pull origin main

# Buoc 2: tao nhanh rieng cho viec cua minh (xem quy dinh dat ten nhanh)
git checkout -b tinh-nang/ten-viec-cua-ban

# Buoc 3: lam code, chay test truoc khi commit
.\mvnw.cmd test

# Buoc 4: commit theo mau
git add .
git commit      # mau .gitmessage se tu hien ra

# Buoc 5: day len va mo Pull Request
git push -u origin tinh-nang/ten-viec-cua-ban
```

Sau đó lên GitHub mở Pull Request theo mẫu có sẵn, nhờ 1 bạn review rồi mới merge vào `main`.

## 5. Khi bị lỗi thường gặp

| Lỗi | Cách xử lý |
|---|---|
| `java -version` ra Java 8/11 | Cài lại Java 17, chỉnh `JAVA_HOME` |
| `.\mvnw.cmd test` đỏ | Đọc log dòng `ERROR`, sửa code rồi chạy lại, không commit khi test đỏ |
| Swagger trắng trang | Chắc chắn app đang chạy ở cổng 8080, thử `http://localhost:8080/v3/api-docs` |
| Commit lộ file `.env` | Xóa file khỏi git: `git rm --cached .env`, thêm vào `.gitignore` |

## 6. Sơ đồ thư mục cho người mới

```text
Simplain/
├── HUONG-DAN-THANH-VIEN-MOI.md   <- bạn đang đọc
├── QUY-DINH-NHANH-VA-COMMIT.md   <- luật nhánh + commit
├── QUY-TAC-VIET-CODE.md          <- luật viết code
├── HUONG-DAN-TEST-API.md         <- cách test API bằng Swagger
├── src/main/java/vn/fpoly/simplain/
│   ├── hello/                    <- code mẫu: HelloController, HelloService...
│   └── config/                   <- cấu hình Swagger (OpenApiConfig)
└── src/test/java/.../hello/      <- test mẫu: HelloControllerTest
```
