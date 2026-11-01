package vn.fpoly.simplain.service;

import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

/**
 * Dịch vụ gửi email thông báo qua Gmail SMTP (Hỗ trợ HTML Template).
 */
@Slf4j
@Service
public class EmailService {

    private final JavaMailSender mailSender;

    @Value("${spring.mail.username:simplain.store.vn@gmail.com}")
    private String fromEmail;

    public EmailService(@Autowired(required = false) JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    /**
     * Gửi email mã OTP xác thực lấy lại mật khẩu với giao diện HTML thương hiệu SIMPLAIN STORE.
     *
     * @param toEmail Địa chỉ người nhận
     * @param otp     Mã OTP 6 số
     * @param hoTen   Tên người nhận (nếu có)
     * @return true nếu gửi thành công hoặc đã ghi log xử lý an toàn
     */
    public boolean guiEmailOtpQuenMatKhau(String toEmail, String otp, String hoTen) {
        String tieuDe = "[SIMPLAIN STORE] Mã xác thực OTP đặt lại mật khẩu của bạn";
        String tenNguoiNhan = (hoTen != null && !hoTen.isBlank()) ? hoTen : "Quý khách";

        String htmlContent = """
            <!DOCTYPE html>
            <html lang="vi">
            <head>
                <meta charset="UTF-8">
                <style>
                    body { font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, Helvetica, Arial, sans-serif; background-color: #f8fafc; margin: 0; padding: 20px; color: #1e293b; }
                    .card { max-width: 540px; margin: 0 auto; background: #ffffff; border-radius: 20px; overflow: hidden; border: 1px solid #e2e8f0; box-shadow: 0 10px 25px -5px rgba(0,0,0,0.05); }
                    .header { background-color: #0a192f; padding: 28px 24px; text-align: center; }
                    .logo { color: #ffffff; font-size: 24px; font-weight: 900; letter-spacing: 2px; text-transform: uppercase; margin: 0; }
                    .logo span { color: #3b82f6; }
                    .content { padding: 32px 28px; }
                    .greeting { font-size: 16px; font-weight: 700; color: #0f172a; margin-bottom: 12px; }
                    .desc { font-size: 14px; line-height: 1.6; color: #475569; margin-bottom: 24px; }
                    .otp-box { background: #f0fdf4; border: 2px dashed #22c55e; border-radius: 16px; padding: 20px; text-align: center; margin-bottom: 24px; }
                    .otp-label { font-size: 12px; font-weight: 800; text-transform: uppercase; color: #166534; letter-spacing: 1px; margin-bottom: 8px; }
                    .otp-code { font-size: 36px; font-weight: 900; letter-spacing: 8px; color: #15803d; font-family: monospace; }
                    .otp-expiry { font-size: 12px; color: #16a34a; margin-top: 6px; font-weight: 600; }
                    .warning { background: #fffbeb; border-left: 4px solid #f59e0b; padding: 12px 16px; font-size: 13px; color: #b45309; border-radius: 0 8px 8px 0; margin-bottom: 24px; line-height: 1.5; }
                    .footer { background: #f8fafc; border-top: 1px solid #f1f5f9; padding: 20px; text-align: center; font-size: 12px; color: #94a3b8; line-height: 1.5; }
                </style>
            </head>
            <body>
                <div class="card">
                    <div class="header">
                        <h1 class="logo">SIMPLAIN<span>.</span></h1>
                    </div>
                    <div class="content">
                        <div class="greeting">Xin chào %s,</div>
                        <div class="desc">
                            Chúng tôi nhận được yêu cầu đặt lại mật khẩu cho tài khoản liên kết với địa chỉ email này trên hệ thống <strong>SIMPLAIN STORE</strong>. Vui lòng sử dụng mã xác thực OTP dưới đây để hoàn tất:
                        </div>
                        <div class="otp-box">
                            <div class="otp-label">MÃ XÁC THỰC OTP CỦA BẠN</div>
                            <div class="otp-code">%s</div>
                            <div class="otp-expiry">Mã có hiệu lực trong vòng 10 phút</div>
                        </div>
                        <div class="warning">
                            <strong>Lưu ý bảo mật:</strong> Tuyệt đối không chia sẻ mã xác thực này cho bất kỳ ai, kể cả nhân viên hỗ trợ của SIMPLAIN. Nếu bạn không thực hiện yêu cầu này, vui lòng bỏ qua email hoặc liên hệ với chúng tôi ngay lập tức.
                        </div>
                        <div style="font-size: 13px; color: #64748b;">
                            Trân trọng,<br>
                            <strong>Đội ngũ SIMPLAIN STORE</strong>
                        </div>
                    </div>
                    <div class="footer">
                        Email này được gửi tự động từ hệ thống Quản lý Bán hàng SIMPLAIN STORE.<br>
                        Mọi thắc mắc xin vui lòng gửi về cskh@simplain.vn.
                    </div>
                </div>
            </body>
            </html>
            """.formatted(tenNguoiNhan, otp);

        try {
            if (mailSender != null) {
                MimeMessage message = mailSender.createMimeMessage();
                MimeMessageHelper helper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.name());
                helper.setFrom(fromEmail, "SIMPLAIN STORE");
                helper.setTo(toEmail);
                helper.setSubject(tieuDe);
                helper.setText(htmlContent, true);
                mailSender.send(message);
                log.info("Đã gửi thành công email OTP quên mật khẩu đến: {}", toEmail);
                return true;
            } else {
                inMaOtpRaConsole(toEmail, otp, "Chưa kích hoạt MailSender");
                return true;
            }
        } catch (Exception e) {
            log.warn("Không thể gửi email qua SMTP ({}), tiến hành in mã OTP ra console để thử nghiệm: {}", e.getMessage(), otp);
            inMaOtpRaConsole(toEmail, otp, e.getMessage());
            return false;
        }
    }

    private void inMaOtpRaConsole(String toEmail, String otp, String reason) {
        System.out.println("================================================================================");
        System.out.println("[SIMPLAIN STORE - GMAIL OTP QUÊN MẬT KHẨU]");
        System.out.println("-> Người nhận: " + toEmail);
        System.out.println("-> MÃ OTP 6 SỐ: [" + otp + "]");
        System.out.println("-> Thời hạn: 10 phút kể từ bây giờ");
        System.out.println("-> Lý do log console: " + reason);
        System.out.println("================================================================================");
    }
}
