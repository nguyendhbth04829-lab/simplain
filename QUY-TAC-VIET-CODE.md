# QUY TẮC VIẾT CODE — Code style chung của nhóm

> File tiếng Việt 100% (nội dung). Tên class theo chuẩn Spring tiếng Anh;
> chú thích, tài liệu, Swagger và thông điệp trả về viết tiếng Việt có dấu.

## 1. Nguyên tắc chung

1. **Tài liệu và chú thích tiếng Việt 100%.** Tên class/phương thức/biến/hằng theo chuẩn Spring quốc tế (tiếng Anh).
2. **Một việc — một nơi.** Controller chỉ nhận request, Service xử lý logic, DTO chỉ mang dữ liệu.
3. **Mọi API mới phải có:** chú thích Swagger (`@Tag`, `@Operation`), DTO rõ ràng, và ít nhất 1 test.
4. **Định dạng tự động:** IDE đọc `.editorconfig` (4 dấu cách, UTF-8, xuống dòng LF, dòng tối đa 120 ký tự).

## 2. Đặt tên theo chuẩn Spring (tiếng Anh)

| Loại | Quy tắc | Ví dụ đúng |
|---|---|---|
| Gói (package) | chữ thường | `vn.fpoly.simplain.hello`, `vn.fpoly.simplain.config` |
| Controller | `...Controller` | `HelloController` |
| Service | `...Service` | `HelloService` |
| DTO yêu cầu | `...Request` | `HelloRequest` |
| DTO kết quả | `...Response` | `HelloResponse` |
| Cấu hình | `...Config` | `OpenApiConfig` |
| Test | `...Test` | `HelloControllerTest` |
| Phương thức | động từ tiếng Anh, camelCase | `getHello`, `greetByName` |
| Biến | danh từ rõ nghĩa | `name`, `message` |
| Hằng số | chữ hoa, gạch dưới | `DEFAULT_MESSAGE` |
| Đường dẫn API | chữ thường, gạch ngang | `/api/hello` |

Ví dụ sai: `DieuKhienXinChao`, `DichVuXinChao`, `xuly1`, `/api/getHello`.

## 3. Cấu trúc thư mục chuẩn cho mỗi tính năng

```text
src/main/java/vn/fpoly/simplain/
├── hello/                      # ví dụ tính năng hello
│   ├── HelloController.java    # @RestController, chỉ gọi service
│   ├── HelloService.java       # @Service, chứa logic
│   ├── HelloRequest.java       # DTO request (+ validation)
│   └── HelloResponse.java      # DTO response
└── config/
    └── OpenApiConfig.java      # cấu hình Swagger dùng chung
```

Ví dụ code mẫu đầy đủ xem trong gói `vn.fpoly.simplain.hello`.

## 4. Mẫu Controller chuẩn (bắt buộc gắn Swagger tiếng Việt)

```java
// HelloController.java — mẫu chuẩn, mọi controller mới copy theo
@Tag(name = "Xin chào", description = "API mẫu cho thành viên mới")
@RestController
@RequestMapping("/api/hello")
public class HelloController {

    // Luôn ghi chú tiếng Việt cho endpoint
    @Operation(summary = "Lấy lời chào mặc định")
    @GetMapping
    public HelloResponse getHello() { ... }
}
```

Quy tắc Swagger:

- `@Tag` ở đầu class: tên tiếng Việt.
- `@Operation(summary = "...")` cho từng endpoint: tiếng Việt.
- DTO có `@Schema(description = "...", example = "...")` tiếng Việt để Swagger hiển thị ví dụ.

## 5. Xử lý lỗi và validation

- DTO request dùng `jakarta.validation` (`@NotBlank`, `@Size`...).
- Controller dùng `@Valid` để tự kiểm tra đầu vào.
- Không trả về `null`. Lỗi trả về mã HTTP đúng: `400` sai đầu vào, `404` không tìm thấy.

## 6. Chú thích và log

```java
/**
 * Tạo lời chào theo tên người dùng.
 * Nếu tên trống thì dùng lời chào mặc định.
 */
public HelloResponse greetByName(String name) { ... }
```

- Mọi class public phải có JavaDoc tiếng Việt 1-2 dòng.
- Không dùng `System.out.println`. Dùng logger nếu cần ghi log.

## 7. Checklist trước khi commit

- [ ] Tên class/biến theo chuẩn Spring tiếng Anh, đúng bảng mục 2?
- [ ] Chú thích, Swagger và message tiếng Việt có dấu?
- [ ] `.editorconfig` không báo lỗi định dạng?
- [ ] API mới có test trong `src/test`?
- [ ] Chạy `.\mvnw.cmd test` xanh?
