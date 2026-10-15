package vn.fpoly.simplain.paymentmethod;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

/**
 * Kiểm thử Service US18, không cần DB thật.
 * Chạy bằng: .\mvnw.cmd -Dtest=PaymentMethodServiceTest test
 */
@ExtendWith(MockitoExtension.class)
@DisplayName("Kiểm thử Service phương thức thanh toán")
class PaymentMethodServiceTest {

    @Mock
    private PaymentMethodRepository repository;

    @InjectMocks
    private PaymentMethodService service;

    @Test
    @DisplayName("Tạo COD mới khi mã chưa tồn tại")
    void createCod() {
        PaymentMethodRequest request = new PaymentMethodRequest(
                "COD", "Thanh toán khi nhận hàng", false, true);
        when(repository.existsByMaPhuongThuc("COD")).thenReturn(false);
        when(repository.save(any())).thenAnswer(inv -> {
            PaymentMethod e = inv.getArgument(0);
            e.setId(1L);
            return e;
        });

        PaymentMethodResponse result = service.create(request);

        assertEquals("COD", result.maPhuongThuc());
        assertTrue(result.kichHoat());
        verify(repository).save(any());
    }

    @Test
    @DisplayName("Tạo trùng mã VNPAY bị từ chối 409")
    void rejectDuplicateMa() {
        PaymentMethodRequest request = new PaymentMethodRequest(
                "VNPAY", "VNPay", true, true);
        when(repository.existsByMaPhuongThuc("VNPAY")).thenReturn(true);

        assertThrows(ResponseStatusException.class, () -> service.create(request));
    }

    @Test
    @DisplayName("Bật/tắt trạng thái MoMo")
    void toggleStatus() {
        PaymentMethod entity = new PaymentMethod(2L, "MOMO", "MoMo", true, true);
        when(repository.findById(2L)).thenReturn(Optional.of(entity));
        when(repository.save(any())).thenAnswer(inv -> inv.getArgument(0));

        PaymentMethodResponse result = service.updateStatus(2L, false);

        assertEquals(false, result.kichHoat());
    }

    @Test
    @DisplayName("Lọc danh sách đang bật")
    void filterKichHoat() {
        when(repository.findByKichHoat(true)).thenReturn(
                List.of(new PaymentMethod(1L, "COD", "COD", false, true)));

        assertEquals(1, service.findAll(true).size());
    }
}
