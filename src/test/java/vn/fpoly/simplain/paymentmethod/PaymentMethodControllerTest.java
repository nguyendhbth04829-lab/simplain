package vn.fpoly.simplain.paymentmethod;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Kiểm thử Controller US18, mock Service nên không cần SQL Server.
 * Chạy bằng: .\mvnw.cmd -Dtest=PaymentMethodControllerTest test
 */
@WebMvcTest(PaymentMethodController.class)
@DisplayName("Kiểm thử API phương thức thanh toán")
class PaymentMethodControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PaymentMethodService paymentMethodService;

    @Test
    @DisplayName("GET /api/thanh-toan/phuong-thuc trả về danh sách")
    void getAll() throws Exception {
        when(paymentMethodService.findAll(null)).thenReturn(List.of(
                new PaymentMethodResponse(1L, "COD", "Thanh toán khi nhận hàng", false, true),
                new PaymentMethodResponse(2L, "VNPAY", "VNPay", true, true)));

        mockMvc.perform(get("/api/thanh-toan/phuong-thuc"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].maPhuongThuc").value("COD"));
    }

    @Test
    @DisplayName("POST tạo mới trả về 201")
    void create() throws Exception {
        when(paymentMethodService.create(any())).thenReturn(
                new PaymentMethodResponse(3L, "MOMO", "MoMo", true, true));

        mockMvc.perform(post("/api/thanh-toan/phuong-thuc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"maPhuongThuc\":\"MOMO\",\"tenPhuongThuc\":\"MoMo\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maPhuongThuc").value("MOMO"));
    }

    @Test
    @DisplayName("POST thiếu tên bị từ chối 400")
    void rejectBlankTen() throws Exception {
        mockMvc.perform(post("/api/thanh-toan/phuong-thuc")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"maPhuongThuc\":\"COD\",\"tenPhuongThuc\":\"\"}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("PATCH bật/tắt trạng thái")
    void toggleStatus() throws Exception {
        when(paymentMethodService.updateStatus(eq(2L), eq(false))).thenReturn(
                new PaymentMethodResponse(2L, "VNPAY", "VNPay", true, false));

        mockMvc.perform(patch("/api/thanh-toan/phuong-thuc/{id}/trang-thai", 2L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"kichHoat\":false}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kichHoat").value(false));
    }
}
