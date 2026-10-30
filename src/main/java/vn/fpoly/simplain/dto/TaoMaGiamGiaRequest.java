package vn.fpoly.simplain.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO biểu mẫu tiếp nhận & Validate tạo chương trình Mã giảm giá (US26 - Mod 6 / Voucher - Khoa).
 * - Tiêu chí 1: Tạo Voucher (% hoặc Tiền mặt).
 * - Tiêu chí 2: Đặt giới hạn ngân sách, lượt dùng tối đa, đơn tối thiểu.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Biểu mẫu Admin tạo chương trình Mã giảm giá (US26)")
public class TaoMaGiamGiaRequest {

    @NotBlank(message = "Mã voucher không được để trống")
    @Size(min = 3, max = 30, message = "Mã voucher phải từ 3 đến 30 ký tự")
    @Pattern(regexp = "^[A-Za-z0-9_-]+$", message = "Mã voucher chỉ gồm chữ cái, chữ số và dấu gạch nối (VD: SALE20, SIMPLAIN100K)")
    @Schema(description = "Mã code voucher (In hoa, không dấu)", example = "SIMPLAIN20")
    private String maCode;

    @NotBlank(message = "Tên chương trình voucher không được để trống")
    @Size(min = 3, max = 150, message = "Tên chương trình voucher phải từ 3 đến 150 ký tự")
    @Schema(description = "Tên hiển thị của chiến dịch khuyến mãi", example = "Ưu đãi mùa hè giảm 20% cho đơn từ 200k")
    private String tenVoucher;

    @NotBlank(message = "Vui lòng chọn loại hình giảm giá")
    @Pattern(regexp = "^(PERCENT|FIXED_AMOUNT)$", message = "Loại giảm giá không hợp lệ (Chỉ chấp nhận PERCENT hoặc FIXED_AMOUNT)")
    @Schema(description = "Hình thức: PERCENT (% phần trăm) hoặc FIXED_AMOUNT (Tiền mặt)", example = "PERCENT")
    private String loaiGiamGia;

    @NotNull(message = "Giá trị giảm không được để trống")
    @DecimalMin(value = "0.01", message = "Giá trị giảm phải lớn hơn 0")
    @Schema(description = "Giá trị chiết khấu (% từ 1-100 hoặc số tiền mặt)", example = "20")
    private BigDecimal giaTriGiam;

    @Schema(description = "Số tiền giảm tối đa (Áp dụng khi chọn loại % phần trăm)", example = "50000")
    private BigDecimal giamToiDa;

    @NotNull(message = "Giá trị đơn tối thiểu không được để trống")
    @DecimalMin(value = "0.00", message = "Giá trị đơn hàng tối thiểu không được âm")
    @Schema(description = "Điều kiện giá trị đơn hàng tối thiểu để áp dụng voucher", example = "200000")
    private BigDecimal giaTriDonToiThieu;

    @Min(value = 1, message = "Tổng lượt dùng tối đa phải từ 1 lượt trở lên")
    @Schema(description = "Tổng số lượt phát hành tối đa (để trống nếu không giới hạn)", example = "500")
    private Integer tongSoLuong;

    @Builder.Default
    @NotNull(message = "Giới hạn mỗi khách hàng không được để trống")
    @Min(value = 1, message = "Mỗi khách hàng phải được dùng tối thiểu 1 lần")
    @Max(value = 100, message = "Giới hạn mỗi khách hàng không được quá 100 lần")
    @Schema(description = "Số lần tối đa 1 tài khoản khách hàng được dùng mã này", example = "1")
    private Integer gioiHanMoiUser = 1;

    @DecimalMin(value = "1000.00", message = "Giới hạn ngân sách tối thiểu từ 1,000 đ")
    @Schema(description = "Tổng ngân sách tối đa dành cho chương trình (để trống nếu không giới hạn)", example = "10000000")
    private BigDecimal nganSach;

    @NotNull(message = "Vui lòng chọn ngày giờ bắt đầu hiệu lực")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Schema(description = "Thời điểm bắt đầu áp dụng mã giảm giá", example = "2026-10-27T00:00")
    private LocalDateTime ngayBatDau;

    @NotNull(message = "Vui lòng chọn ngày giờ kết thúc hiệu lực")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    @Schema(description = "Thời điểm kết thúc áp dụng mã giảm giá", example = "2026-11-27T23:59")
    private LocalDateTime ngayKetThuc;

    @Builder.Default
    @Schema(description = "Trạng thái kích hoạt chương trình", example = "true")
    private Boolean kichHoat = true;

    @Size(max = 500, message = "Mô tả / Điều khoản sử dụng không được quá 500 ký tự")
    @Schema(description = "Mô tả điều khoản, ghi chú nội bộ hoặc hướng dẫn cho khách hàng", example = "Áp dụng cho toàn bộ danh mục áo nam SIMPLAIN.")
    private String moTa;
}
