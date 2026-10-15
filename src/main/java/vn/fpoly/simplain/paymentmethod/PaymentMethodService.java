package vn.fpoly.simplain.paymentmethod;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

/**
 * Service xử lý logic phương thức thanh toán (US18).
 * Controller chỉ gọi sang đây, không gọi Repository trực tiếp.
 */
@Service
public class PaymentMethodService {

    private final PaymentMethodRepository repository;

    public PaymentMethodService(PaymentMethodRepository repository) {
        this.repository = repository;
    }

    /**
     * Lấy toàn bộ, có thể lọc theo trạng thái bật/tắt.
     *
     * @param kichHoat null = lấy hết, true/false = lọc
     * @return danh sách DTO
     */
    public List<PaymentMethodResponse> findAll(Boolean kichHoat) {
        List<PaymentMethod> entities = kichHoat == null
                ? repository.findAll()
                : repository.findByKichHoat(kichHoat);
        return entities.stream().map(PaymentMethodResponse::from).toList();
    }

    /**
     * Lấy chi tiết theo id.
     *
     * @param id khóa chính
     * @return DTO chi tiết
     */
    public PaymentMethodResponse findById(Long id) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy phương thức thanh toán"));
        return PaymentMethodResponse.from(entity);
    }

    /**
     * Tạo mới (COD, VNPAY, MOMO...).
     *
     * @param request dữ liệu đã validate
     * @return DTO vừa tạo
     */
    public PaymentMethodResponse create(PaymentMethodRequest request) {
        String ma = request.maPhuongThuc().trim().toUpperCase();
        if (repository.existsByMaPhuongThuc(ma)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã phương thức đã tồn tại");
        }
        PaymentMethod entity = new PaymentMethod();
        entity.setMaPhuongThuc(ma);
        entity.setTenPhuongThuc(request.tenPhuongThuc().trim());
        entity.setLaTrucTuyen(request.laTrucTuyen() != null ? request.laTrucTuyen() : false);
        entity.setKichHoat(request.kichHoat() != null ? request.kichHoat() : true);
        return PaymentMethodResponse.from(repository.save(entity));
    }

    /**
     * Cập nhật toàn bộ thông tin.
     *
     * @param id id cần sửa
     * @param request dữ liệu mới
     * @return DTO sau khi sửa
     */
    public PaymentMethodResponse update(Long id, PaymentMethodRequest request) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy phương thức thanh toán"));
        String ma = request.maPhuongThuc().trim().toUpperCase();
        if (repository.existsByMaPhuongThucAndIdNot(ma, id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Mã phương thức đã tồn tại");
        }
        entity.setMaPhuongThuc(ma);
        entity.setTenPhuongThuc(request.tenPhuongThuc().trim());
        if (request.laTrucTuyen() != null) {
            entity.setLaTrucTuyen(request.laTrucTuyen());
        }
        if (request.kichHoat() != null) {
            entity.setKichHoat(request.kichHoat());
        }
        return PaymentMethodResponse.from(repository.save(entity));
    }

    /**
     * Bật/tắt nhanh cho Admin (yêu cầu chính của US18).
     *
     * @param id id cần đổi trạng thái
     * @param kichHoat true = bật, false = tắt
     * @return DTO sau khi đổi
     */
    public PaymentMethodResponse updateStatus(Long id, boolean kichHoat) {
        PaymentMethod entity = repository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(
                        HttpStatus.NOT_FOUND, "Không tìm thấy phương thức thanh toán"));
        entity.setKichHoat(kichHoat);
        return PaymentMethodResponse.from(repository.save(entity));
    }

    /**
     * Xóa cứng. Chỉ dùng khi chưa phát sinh giao dịch.
     *
     * @param id id cần xóa
     */
    public void delete(Long id) {
        if (!repository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy phương thức thanh toán");
        }
        repository.deleteById(id);
    }
}
