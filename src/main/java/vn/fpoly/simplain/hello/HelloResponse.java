package vn.fpoly.simplain.hello;

import io.swagger.v3.oas.annotations.media.Schema;
import java.time.LocalDateTime;

/**
 * Dữ liệu trả về cho API hello.
 */
@Schema(description = "Lời chào trả về cho client")
public record HelloResponse(

        @Schema(description = "Câu chào hoàn chỉnh", example = "Xin chào, Cả lớp!")
        String message,

        @Schema(description = "Thời điểm tạo lời chào")
        LocalDateTime timestamp) {
}
