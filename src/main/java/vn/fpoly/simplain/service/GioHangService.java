package vn.fpoly.simplain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.ChiTietGioHang;
import vn.fpoly.simplain.entity.GioHang;
import vn.fpoly.simplain.repository.ChiTietGioHangRepository;
import vn.fpoly.simplain.repository.GoiComboCheckoutRepository;
import vn.fpoly.simplain.repository.GioHangRepository;
import vn.fpoly.simplain.repository.ThongTinSanPhamGioHang;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.UUID;

/**
 * Xử lý logic giỏ hàng (US17).
 * Controller chỉ gọi sang đây, không gọi Repository trực tiếp.
 */
@Service
public class GioHangService {

    private final GioHangRepository gioHangRepository;
    private final ChiTietGioHangRepository chiTietGioHangRepository;
    private final GoiComboCheckoutRepository goiComboCheckoutRepository;

    public GioHangService(GioHangRepository gioHangRepository,
                          ChiTietGioHangRepository chiTietGioHangRepository,
                          GoiComboCheckoutRepository goiComboCheckoutRepository) {
        this.gioHangRepository = gioHangRepository;
        this.chiTietGioHangRepository = chiTietGioHangRepository;
        this.goiComboCheckoutRepository = goiComboCheckoutRepository;
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

    public Map<Long, List<ThongTinSanPhamGioHang>> layBienTheLuaChonTheoSanPham() {
        return chiTietGioHangRepository.timTatCaBienTheDangKinhDoanh().stream()
                .collect(Collectors.groupingBy(ThongTinSanPhamGioHang::getMaSanPhamId,
                        LinkedHashMap::new, Collectors.toList()));
    }

    @Transactional
    public void doiBienThe(Long maKhachHangId, Long idDong, Long maBienTheMoiId) {
        GioHang gio = gioHangRepository.findByMaKhachHangId(maKhachHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy giỏ hàng."));
        ChiTietGioHang dong = chiTietGioHangRepository.findByIdAndMaGioHangId(idDong, gio.getId())
                .orElseThrow(() -> new IllegalArgumentException("Dòng hàng không thuộc giỏ này."));

        Map<Long, ThongTinSanPhamGioHang> bienTheConHang = chiTietGioHangRepository.timBienTheConHang().stream()
                .collect(Collectors.toMap(ThongTinSanPhamGioHang::getMaBienTheId, Function.identity()));
        ThongTinSanPhamGioHang bienTheMoi = bienTheConHang.get(maBienTheMoiId);
        ThongTinSanPhamGioHang bienTheCu = layThongTinSanPham(List.of(dong)).get(dong.getMaBienTheId());
        if (bienTheMoi == null || bienTheCu == null) {
            throw new IllegalArgumentException("Biến thể đã chọn không còn khả dụng.");
        }
        if (!Objects.equals(bienTheCu.getMaSanPhamId(), bienTheMoi.getMaSanPhamId())) {
            throw new IllegalArgumentException("Chỉ có thể đổi màu hoặc kích thước của cùng một sản phẩm.");
        }
        if (Objects.equals(dong.getMaBienTheId(), maBienTheMoiId)) return;

        ChiTietGioHang dongTrung = chiTietGioHangRepository
                .findByMaGioHangIdAndMaBienTheId(gio.getId(), maBienTheMoiId).stream()
                .filter(khac -> !Objects.equals(khac.getId(), dong.getId()))
                .filter(khac -> Objects.equals(khac.getMaComboId(), dong.getMaComboId()))
                .filter(khac -> Objects.equals(khac.getNhomComboToken(), dong.getNhomComboToken()))
                .findFirst().orElse(null);
        if (dongTrung != null) {
            dongTrung.setSoLuong((dongTrung.getSoLuong() == null ? 0 : dongTrung.getSoLuong())
                    + (dong.getSoLuong() == null ? 0 : dong.getSoLuong()));
            chiTietGioHangRepository.save(dongTrung);
            chiTietGioHangRepository.delete(dong);
        } else {
            dong.setMaBienTheId(maBienTheMoiId);
            chiTietGioHangRepository.save(dong);
        }
        chamGio(gio);
    }

    /** Trả về thông tin sản phẩm thật, tra cứu theo các ID biến thể trong giỏ. */
    public Map<Long, ThongTinSanPhamGioHang> layThongTinSanPham(List<ChiTietGioHang> chiTiet) {
        List<Long> maBienTheIds = chiTiet.stream()
                .map(ChiTietGioHang::getMaBienTheId)
                .filter(Objects::nonNull)
                .distinct()
                .toList();
        if (maBienTheIds.isEmpty()) {
            return Map.of();
        }
        return chiTietGioHangRepository.timThongTinSanPhamTrongGio(maBienTheIds).stream()
                .collect(Collectors.toMap(ThongTinSanPhamGioHang::getMaBienTheId, Function.identity()));
    }

    public BigDecimal tinhTongTienHang(List<ChiTietGioHang> chiTiet,
                                      Map<Long, ThongTinSanPhamGioHang> sanPhamTheoBienThe) {
        List<Long> comboIds = chiTiet.stream()
                .filter(dong -> dong.getNhomComboToken() != null && dong.getMaComboId() != null)
                .map(ChiTietGioHang::getMaComboId)
                .distinct()
                .toList();
        Map<Long, BigDecimal> giaCombo = goiComboCheckoutRepository.layGiaCombo(comboIds);
        Map<String, List<ChiTietGioHang>> nhomCombo = new LinkedHashMap<>();
        BigDecimal tong = BigDecimal.ZERO;

        for (ChiTietGioHang dong : chiTiet) {
            if (dong.getNhomComboToken() != null && dong.getMaComboId() != null
                    && giaCombo.containsKey(dong.getMaComboId())) {
                String key = dong.getMaComboId() + ":" + dong.getNhomComboToken();
                nhomCombo.computeIfAbsent(key, ignored -> new java.util.ArrayList<>()).add(dong);
                continue;
            }
            tong = tong.add(tinhThanhTienLe(dong, sanPhamTheoBienThe));
        }

        for (List<ChiTietGioHang> nhom : nhomCombo.values()) {
            ChiTietGioHang dongDau = nhom.get(0);
            tong = tong.add(giaCombo.get(dongDau.getMaComboId()));
            // Một token đại diện một gói combo; số lượng tăng thêm của từng dòng tính theo giá biến thể.
            for (ChiTietGioHang dong : nhom) {
                int soLuongThem = Math.max(0, (dong.getSoLuong() == null ? 1 : dong.getSoLuong()) - 1);
                if (soLuongThem > 0) {
                    ThongTinSanPhamGioHang sanPham = sanPhamTheoBienThe.get(dong.getMaBienTheId());
                    if (sanPham != null && sanPham.getGiaBan() != null) {
                        tong = tong.add(sanPham.getGiaBan().multiply(BigDecimal.valueOf(soLuongThem)));
                    }
                }
            }
        }
        return tong;
    }

    private BigDecimal tinhThanhTienLe(ChiTietGioHang dong,
                                       Map<Long, ThongTinSanPhamGioHang> sanPhamTheoBienThe) {
        ThongTinSanPhamGioHang sanPham = sanPhamTheoBienThe.get(dong.getMaBienTheId());
        if (sanPham == null || sanPham.getGiaBan() == null || dong.getSoLuong() == null) return BigDecimal.ZERO;
        return sanPham.getGiaBan().multiply(BigDecimal.valueOf(dong.getSoLuong()));
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
