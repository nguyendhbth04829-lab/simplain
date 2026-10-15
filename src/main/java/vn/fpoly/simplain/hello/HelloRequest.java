package vn.fpoly.simplain.hello;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Dữ liệu đầu vào cho API hello.
 * Ví dụ JSON: { "name": "Cả lớp" }
 */
@Schema(description = "Tên người cần chào")
public record HelloRequest(

        @Schema(description = "Tên người nhận lời chào", example = "Cả lớp")
        @NotBlank(message = "Tên không được để trống")
        @Size(max = 100, message = "Tên tối đa 100 ký tự")
        String name) {
}
