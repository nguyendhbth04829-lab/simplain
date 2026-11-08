package vn.fpoly.simplain.controller.advice;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.MultipartException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Bắt và xử lý lỗi liên quan đến Multipart và dung lượng file tải lên.
 */
@ControllerAdvice
public class GlobalUploadExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalUploadExceptionHandler.class);

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public String xuLyLoiVuotDungLuong(MaxUploadSizeExceededException ex,
                                       HttpServletRequest request,
                                       RedirectAttributes redirectAttributes) {
        log.error("Lỗi MaxUploadSizeExceededException: message={}, maxUploadSize={}, cause={}",
                ex.getMessage(), ex.getMaxUploadSize(), (ex.getCause() != null ? ex.getCause().getMessage() : "null"), ex);
        String causeMsg = ex.getCause() != null ? ex.getCause().getMessage() : "";
        String fullMsg = ((ex.getMessage() != null ? ex.getMessage() : "") + " " + causeMsg).toLowerCase();
        String thongBao;
        if (fullMsg.contains("part") || fullMsg.contains("parameter")) {
            thongBao = "Số lượng trường dữ liệu trong biểu mẫu vượt quá giới hạn xử lý của máy chủ. Vui lòng thử lại.";
        } else if (ex.getMaxUploadSize() > 0) {
            thongBao = "Dung lượng tệp tải lên vượt quá giới hạn cho phép (" + (ex.getMaxUploadSize() / (1024 * 1024)) + "MB). Vui lòng chọn ảnh nhỏ hơn.";
        } else {
            thongBao = "Lỗi xử lý biểu mẫu tải lên: " + (causeMsg != null && !causeMsg.isBlank() ? causeMsg : ex.getMessage());
        }

        String referer = request.getHeader("Referer");
        redirectAttributes.addFlashAttribute("loiNghiepVu", thongBao);
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/cua-hang";
    }

    @ExceptionHandler(MultipartException.class)
    public String xuLyLoiMultipart(MultipartException ex,
                                   HttpServletRequest request,
                                   RedirectAttributes redirectAttributes) {
        log.error("Lỗi MultipartException: message={}, cause={}",
                ex.getMessage(), (ex.getCause() != null ? ex.getCause().getMessage() : "null"), ex);
        String referer = request.getHeader("Referer");
        String chiTiet = ex.getCause() != null ? ex.getCause().getMessage() : ex.getMessage();
        redirectAttributes.addFlashAttribute("loiNghiepVu",
                "Lỗi xử lý file tải lên (" + chiTiet + "). Vui lòng thử lại.");
        if (referer != null && !referer.isBlank()) {
            return "redirect:" + referer;
        }
        return "redirect:/cua-hang";
    }
}
