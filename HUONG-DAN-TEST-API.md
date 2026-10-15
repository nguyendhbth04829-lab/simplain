# HƯỚNG DẪN TEST API — Swagger giống FastAPI docs

> File tiếng Việt 100%. Đọc xong là tự test được API không cần Postman.

## 1. Swagger là gì?

Swagger UI là trang tài liệu API sống, giống màn hình `/docs` của FastAPI:
vừa xem được danh sách API, vừa bấm **Try it out** để gọi thử ngay trên trình duyệt.

- Trang Swagger: http://localhost:8080/docs
- File JSON thô: http://localhost:8080/v3/api-docs

## 2. Chạy app rồi mở Swagger

```powershell
.\mvnw.cmd spring-boot:run
```

Mở trình duyệt vào http://localhost:8080/docs, bạn sẽ thấy nhóm **Xin chào** với 3 endpoint mẫu:

| Phương thức | Đường dẫn | Tác dụng |
|---|---|---|
| GET | `/api/hello` | Lấy lời chào mặc định |
| GET | `/api/hello/name/{name}` | Chào theo tên trên đường dẫn |
| POST | `/api/hello` | Chào theo tên trong JSON |

## 3. Gọi thử từng endpoint trên Swagger

1. Bấm vào endpoint muốn thử → bấm nút **Try it out**.
2. Điền tham số (ví dụ `name` = `Lớp Java 2026`).
3. Bấm **Execute**. Kéo xuống mục **Responses** xem kết quả.

Ví dụ POST `/api/hello` với body:

```json
{
  "name": "Cả lớp"
}
```

Kết quả đúng:

```json
{
  "message": "Xin chào, Cả lớp!",
  "timestamp": "2026-09-21T10:00:00"
}
```

## 4. Gọi bằng PowerShell (không cần Swagger)

```powershell
# GET mac dinh
Invoke-RestMethod http://localhost:8080/api/hello

# GET theo ten
Invoke-RestMethod http://localhost:8080/api/hello/name/An

# POST theo JSON
Invoke-RestMethod -Method Post -Uri http://localhost:8080/api/hello `
  -ContentType "application/json" -Body '{"name":"Cả lớp"}'
```

## 5. Chạy test tự động

```powershell
# Chay toan bo test (bao gom HelloControllerTest)
.\mvnw.cmd test

# Chi chay file test mau
.\mvnw.cmd -Dtest=HelloControllerTest test
```

File test mẫu: `src/test/java/vn/fpoly/simplain/hello/HelloControllerTest.java`.
Mọi endpoint mới phải có test tương tự: gọi endpoint thật qua `MockMvc`, kiểm tra mã `200`/`201` và nội dung JSON.

## 6. Thêm endpoint mới vào Swagger (cho thành viên mới)

1. Copy 1 phương thức trong `HelloController.java`.
2. Gắn `@Operation(summary = "Mô tả tiếng Việt")`.
3. Mô tả DTO bằng `@Schema(description = "...", example = "...")` tiếng Việt.
4. Chạy lại app, F5 trang Swagger là thấy endpoint mới — không cần cấu hình thêm.
