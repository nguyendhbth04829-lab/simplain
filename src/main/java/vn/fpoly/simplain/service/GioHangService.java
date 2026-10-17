package vn.fpoly.simplain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.ChiTietGioHang;
import vn.fpoly.simplain.entity.GioHang;
import vn.fpoly.simplain.repository.ChiTietGioHangRepository;
import vn.fpoly.simplain.repository.GioHangRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Xử lý logic giỏ hàng (US17).
 * Controller chỉ gọi sang đây, không gọi Repository trực tiếp.
 */
@Service
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;

    public GioHangService(GioHangRepository gioHangRepository,
                          ChiTietGioHangRepository chiTietGioHangRepository) {
        this.gioHangRepository = gioHangRepository;
        this.chiTietGioHangRepository = chiTietGioHangRepository;
    }

    /**
     * Lấy giỏ của khách, chưa có thì tạo mới.
     *
     * @param maKhachHangId id khách hàng (test tay nhập 1)
     * @return giỏ hàng
     */
    public GioHang layHoacTao(Long maKhachHangId) {
        return gioHangRepository.findByMaKhachHangId(maKhachHangId).orElseGet(() -> {
            GioHang moi = new GioHang();
            moi.setMaKhachHangId(maKhachHangId);
            moi.setNgayCapNhat(LocalDateTime.now());
            return gioHangRepository.save(moi);
        });
    }

    public List<ChiTietGioHang> layChiTiet(Long maGioHangId) {
        return chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(maGioHangId);
    }

    /**
     * Thêm biến thể vào giỏ. Trùng (biến thể + combo + token) thì cộng dồn số lượng.
     *
     * @param maKhachHangId id khách
     * @param maBienTheId id biến thể của Mod 2
     * @param maComboId id gói combo, null nếu mua lẻ
     * @param nhomComboToken token bó combo, null nếu mua lẻ
     * @param soLuong số lượng thêm
     */
    public void themMon(Long maKhachHangId, Long maBienTheId, Long maComboId,
                        String nhomComboToken, int soLuong) {
        GioHang gio = layHoacTao(maKhachHangId);
        String token = (nhomComboToken == null || nhomComboToken.isBlank()) ? null : nhomComboToken.trim();
        List<ChiTietGioHang> cungBienThe =
                chiTietGioHangRepository.findByMaGioHangIdAndMaBienTheId(gio.getId(), maBienTheId);
        for (ChiTietGioHang dong : cungBienThe) {
            if (Objects.equals(dong.getMaComboId(), maComboId)
                    && Objects.equals(dong.getNhomComboToken(), token)) {
                dong.setSoLuong(dong.getSoLuong() + soLuong);
                chiTietGioHangRepository.save(dong);
                chamGio(gio);
                return;
            }
        }
        ChiTietGioHang moi = new ChiTietGioHang();
        moi.setMaGioHangId(gio.getId());
        moi.setMaBienTheId(maBienTheId);
        moi.setMaComboId(maComboId);
        moi.setNhomComboToken(token);
        moi.setSoLuong(soLuong);
        moi.setNgayThem(LocalDateTime.now());
        chiTietGioHangRepository.save(moi);
        chamGio(gio);
    }

    /**
     * Sửa số lượng 1 dòng. Số lượng nhỏ hơn 1 thì xóa dòng.
     */
    public void suaSoLuong(Long idDong, int soLuong) {
        ChiTietGioHang dong = chiTietGioHangRepository.findById(idDong).orElse(null);
        if (dong == null) return;
        if (soLuong < 1) {
            chiTietGioHangRepository.deleteById(idDong);
            return;
        }
        dong.setSoLuong(soLuong);
        chiTietGioHangRepository.save(dong);
    }

    public void xoaDong(Long idDong) {
        chiTietGioHangRepository.deleteById(idDong);
    }

    /**
     * Tăng/giảm 1 đơn vị (nút -/+ ngoài giao diện).
     * Giảm từ 1 xuống 0 thì xóa dòng luôn.
     */
    public void doiSoLuong(Long idDong, int delta) {
        ChiTietGioHang dong = chiTietGioHangRepository.findById(idDong).orElse(null);
        if (dong == null) return;
        suaSoLuong(idDong, (dong.getSoLuong() == null ? 1 : dong.getSoLuong()) + delta);
    }

    /**
     * Sinh token mới để nhóm các món thành 1 bó combo.
     *
     * @return token 8 ký tự viết hoa
     */
    public String sinhToken() {
        return UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }

    /**
     * Xóa cả bó combo khỏi giỏ theo token.
     */
    @Transactional
    public void xoaNhomCombo(Long maGioHangId, String token) {
        chiTietGioHangRepository.deleteByMaGioHangIdAndNhomComboToken(maGioHangId, token);
    }

    public int demSoDong(Long maGioHangId) {
        return chiTietGioHangRepository.findByMaGioHangIdOrderByNgayThemDesc(maGioHangId).size();
    }

    private void chamGio(GioHang gio) {
        gio.setNgayCapNhat(LocalDateTime.now());
        gioHangRepository.save(gio);
    }
}
