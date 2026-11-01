# Simplain — Dự án mẫu cho cả nhóm

> Tài liệu tiếng Việt 100%. Code theo chuẩn Spring quốc tế (Controller/Service/DTO tiếng Anh);
> chú thích, Swagger và thông điệp trả về viết tiếng Việt có dấu.

## 1. Thành viên mới bắt đầu từ đâu?

Đọc theo thứ tự:

1. `HUONG-DAN-THANH-VIEN-MOI.md` — git clone, chạy dự án, quy trình mỗi ngày.
2. `QUY-DINH-NHANH-VA-COMMIT.md` — luật đặt tên nhánh và commit theo 1 form.
3. `QUY-TAC-VIET-CODE.md` — code style chuẩn Spring.
4. `HUONG-DAN-TEST-API.md` — test API bằng Swagger giống FastAPI docs.

## 2. Chạy nhanh

```powershell
# Chay app
.\mvnw.cmd spring-boot:run

# Chay test
.\mvnw.cmd test
```

Mở sau khi chạy:

- API mẫu: http://localhost:8080/api/hello
- Swagger (gọi thử trực tiếp): http://localhost:8080/docs
- OpenAPI JSON: http://localhost:8080/v3/api-docs

## 3. API mẫu

| Phương thức | Đường dẫn | Ví dụ |
|---|---|---|
| GET | `/api/hello` | `{"message":"Xin chào, Simplain!"}` |
| GET | `/api/hello/name/{name}` | `/api/hello/name/An` |
| POST | `/api/hello` | Body `{"name":"Cả lớp"}` → `201 Created` |

Code mẫu trong `src/main/java/vn/fpoly/simplain/hello/`:

- `HelloController.java` — controller (chỉ nhận request)
- `HelloService.java` — service (chứa logic)
- `HelloRequest.java` — DTO request
- `HelloResponse.java` — DTO response

Cấu hình Swagger: `src/main/java/vn/fpoly/simplain/config/OpenApiConfig.java`.
Test mẫu: `src/test/java/vn/fpoly/simplain/hello/HelloControllerTest.java`.

## 4. Quy trình nhóm (tóm tắt)

```powershell
git checkout main
git pull origin main
git checkout -b tinh-nang/ten-viec-cua-ban
# ... viet code ...
.\mvnw.cmd test
git add .
git commit   # theo mau .gitmessage
git push -u origin tinh-nang/ten-viec-cua-ban
```

Mở Pull Request theo mẫu trong `.github/pull_request_template.md`.

## 5. Yêu cầu môi trường

- Java 17+
- Git
- Không cần cài Maven (dùng sẵn `mvnw.cmd` / `mvnw`)

## 6. Giấy phép và liên hệ

Nội bộ nhóm học tập FPoly. Mọi thắc mắc ghi vào mục Issues của repo.
