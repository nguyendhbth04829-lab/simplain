# Hướng dẫn chạy ứng dụng

## Yêu cầu

- Java 17 trở lên.
- SQL Server đang chạy và có database `Simpl4` cùng schema cần thiết.
- Đặt biến môi trường `DB_PASSWORD`; có thể đặt `DB_USERNAME` nếu không dùng tài khoản `sa`.
- Nếu cần xác thực JWT từ ứng dụng khác, đặt `JWT_SECRET` trùng với khóa của ứng dụng đó.

## Khởi chạy

Mở PowerShell tại thư mục chứa file này và chạy:

```powershell
.\mvnw.cmd spring-boot:run
```

Ứng dụng mặc định chạy trên cổng `8080`. Cấu hình `ddl-auto=none` nên ứng dụng không tự tạo hoặc sửa schema database.

Các test tích hợp có thể ghi dữ liệu vào database đã cấu hình; chỉ chạy chúng trên database phù hợp để kiểm thử.
