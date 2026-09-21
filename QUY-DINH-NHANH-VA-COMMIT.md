# QUY ĐỊNH NHÁNH VÀ COMMIT — Cả nhóm làm theo 1 form

> File tiếng Việt 100%. Tên file không dấu để git và Windows không lỗi.

## 1. Mô hình nhánh

- `main`: nhánh ổn định, luôn chạy được, luôn test xanh. Không commit trực tiếp vào `main`.
- Nhánh công việc: tách từ `main`, làm xong thì mở Pull Request về `main`.

### Cách đặt tên nhánh

```text
<loai>/<mo-ta-ngan-gon-khong-dau>
```

Loại nhánh (viết thường, không dấu):

| Tiền tố | Dùng khi |
|---|---|
| `tinh-nang/` | Làm tính năng mới. Ví dụ: `tinh-nang/api-hello` |
| `sua-loi/` | Sửa lỗi. Ví dụ: `sua-loi/khong-hien-tieng-viet` |
| `tai-lieu/` | Chỉ sửa tài liệu. Ví dụ: `tai-lieu/huong-dan-swagger` |
| `kiem-thu/` | Chỉ thêm/sửa test. Ví dụ: `kiem-thu/endpoint-hello` |
| `tai-cau-truc/` | Sắp xếp lại code, không đổi hành vi |
| `nong/` | Sửa lỗi khẩn trên `main`. Ví dụ: `nong/sua-cau-hinh-swagger` |

Lệnh mẫu:

```powershell
git checkout main
git pull origin main
git checkout -b tinh-nang/api-hello
```

## 2. Quy định commit

### Cấu trúc (bắt buộc)

```text
<loai>(<pham-vi>): <mo-ta-ngan-gon-tieng-viet>
```

- `<loai>`: xem bảng dưới, viết thường, không dấu.
- `<pham-vi>`: phần code đụng tới. Ví dụ: `hello`, `chung`, `cau-hinh`.
- `<mo-ta>`: tiếng Việt có dấu, viết thường, không dấu chấm cuối câu, dưới 72 ký tự.

### Các loại commit

| Loại | Khi nào dùng | Ví dụ |
|---|---|---|
| `tinh-nang` | Thêm API, thêm chức năng | `tinh-nang(hello): them API POST hello` |
| `sua-loi` | Sửa lỗi | `sua-loi(hello): xu ly ten de trong` |
| `tai-lieu` | Sửa file `.md` | `tai-lieu(chung): bo sung anh swagger` |
| `kiem-thu` | Thêm/sửa test | `kiem-thu(hello): them test ten co dau` |
| `tai-cau-truc` | Đổi cấu trúc, giữ nguyên kết quả | `tai-cau-truc(hello): tach service rieng` |
| `dinh-dang` | Khoảng trắng, xuống dòng | `dinh-dang(chung): chuan hoa editorconfig` |
| `cau-hinh` | Đổi `pom.xml`, `application.properties` | `cau-hinh(swagger): bat chuc nang try it out` |
| `hoan-lai` | Revert commit cũ | `hoan-lai(hello): hoan lai commit sai` |

### Ví dụ đúng / sai

```text
ĐÚNG: tinh-nang(hello): them API lay loi chao theo ten
SAI:  update code
SAI:  Thêm api mới!!!
SAI:  TINH-NANG: them api
```

### Dùng mẫu commit có sẵn

```powershell
git config commit.template .gitmessage
git commit   # khung mẫu tự hiện, chỉ việc điền
```

## 3. Quy định Pull Request

1. Một PR chỉ làm một việc.
2. Test phải xanh (`.\mvnw.cmd test`) trước khi nhờ review.
3. Điền đủ mẫu PR trong `.github/pull_request_template.md`.
4. Ít nhất 1 thành viên khác bấm Approve mới được merge.
5. Merge theo kiểu `Squash` để lịch sử `main` gọn theo 1 dòng 1 việc.

## 4. Tuyệt đối không commit

- File `.env`, mật khẩu, token.
- Thư mục `target/`, `build/`, `out/`, `.idea/`, `.vscode/`.
- File log `*.log`.

Nếu lỡ commit nhầm:

```powershell
git rm --cached <ten-file-lo>
# roi them ten file do vao .gitignore, commit lai
```
