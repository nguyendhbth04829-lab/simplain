/**
 * Tác giả: Gia Minh
 * Chức năng: US14 - Dịch vụ xử lý nghiệp vụ Quản lý danh sách sản phẩm yêu thích (Wishlist)
 * Cập nhật xác nhận commit US14: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.SanPhamYeuThichResponse;
import vn.fpoly.simplain.dto.ThongBaoResponse;
import vn.fpoly.simplain.dto.TrangThaiYeuThichResponse;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamYeuThich;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamYeuThichRepository;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service quản lý danh sách sản phẩm yêu thích của khách hàng.
 */
@Service
public class SanPhamYeuThichService {

    private final SanPhamYeuThichRepository sanPhamYeuThichRepository;
    private final SanPhamRepository sanPhamRepository;
    private final KhachHangService khachHangService;

    public SanPhamYeuThichService(SanPhamYeuThichRepository sanPhamYeuThichRepository,
                                  SanPhamRepository sanPhamRepository,
                                  KhachHangService khachHangService) {
        this.sanPhamYeuThichRepository = sanPhamYeuThichRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.khachHangService = khachHangService;
    }

    /**
     * Lấy toàn bộ danh sách sản phẩm yêu thích của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @return danh sách DTO sản phẩm yêu thích
     */
    @Transactional(readOnly = true)
    public List<SanPhamYeuThichResponse> layDanhSachYeuThich(Long idKhachHang) {
        khachHangService.layKhachHangHopLe(idKhachHang);

        List<SanPhamYeuThich> danhSachYeuThich = sanPhamYeuThichRepository.findAllByMaKhachHangIdOrderByNgayThemDesc(idKhachHang);
        if (danhSachYeuThich.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> danhSachSanPhamId = danhSachYeuThich.stream()
                .map(SanPhamYeuThich::getMaSanPhamId)
                .toList();

        Map<Long, SanPham> banDoSanPham = sanPhamRepository.findByIdIn(danhSachSanPhamId).stream()
                .collect(Collectors.toMap(SanPham::getId, Function.identity()));

        return danhSachYeuThich.stream()
                .map(yt -> SanPhamYeuThichResponse.taoMoi(yt, banDoSanPham.get(yt.getMaSanPhamId())))
                .toList();
    }

    /**
     * Thêm sản phẩm vào danh sách yêu thích của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param maSanPhamId ID sản phẩm
     * @return DTO thông tin sản phẩm yêu thích đã thêm
     */
    @Transactional
    public SanPhamYeuThichResponse themVaoYeuThich(Long idKhachHang, Long maSanPhamId) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        SanPham sanPham = kiemTraVaLaySanPham(maSanPhamId);

        // Kiểm tra xem sản phẩm đã có trong danh sách yêu thích chưa (Idempotency)
        Optional<SanPhamYeuThich> daCo = sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(idKhachHang, maSanPhamId);
        if (daCo.isPresent()) {
            return SanPhamYeuThichResponse.taoMoi(daCo.get(), sanPham);
        }

        SanPhamYeuThich banGhiMoi = SanPhamYeuThich.builder()
                .maKhachHangId(idKhachHang)
                .maSanPhamId(maSanPhamId)
                .ngayThem(LocalDateTime.now())
                .build();

        SanPhamYeuThich daLuu = sanPhamYeuThichRepository.save(banGhiMoi);
        return SanPhamYeuThichResponse.taoMoi(daLuu, sanPham);
    }

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @param maSanPhamId ID sản phẩm
     * @return thông báo kết quả
     */
    @Transactional
    public ThongBaoResponse xoaKhoiYeuThich(Long idKhachHang, Long maSanPhamId) {
        khachHangService.layKhachHangHopLe(idKhachHang);

        SanPhamYeuThich yt = sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(idKhachHang, maSanPhamId)
                .orElseThrow(() -> new KhongTimThayException("Sản phẩm chưa có trong danh sách yêu thích của bạn"));

        sanPhamYeuThichRepository.delete(yt);
        return new ThongBaoResponse("Đã xóa sản phẩm khỏi danh sách yêu thích");
    }

    /**
     * Bật / Tắt trạng thái yêu thích sản phẩm (Toggle tim).
     *
     * @param idKhachHang ID khách hàng
     * @param maSanPhamId ID sản phẩm
     * @return DTO trạng thái yêu thích mới
     */
    @Transactional
    public TrangThaiYeuThichResponse toggleYeuThich(Long idKhachHang, Long maSanPhamId) {
        khachHangService.layKhachHangHopLe(idKhachHang);
        SanPham sanPham = kiemTraVaLaySanPham(maSanPhamId);

        Optional<SanPhamYeuThich> daCo = sanPhamYeuThichRepository.findByMaKhachHangIdAndMaSanPhamId(idKhachHang, maSanPhamId);
        if (daCo.isPresent()) {
            sanPhamYeuThichRepository.delete(daCo.get());
            return new TrangThaiYeuThichResponse(maSanPhamId, false, "Đã bỏ yêu thích sản phẩm");
        } else {
            SanPhamYeuThich banGhiMoi = SanPhamYeuThich.builder()
                    .maKhachHangId(idKhachHang)
                    .maSanPhamId(maSanPhamId)
                    .ngayThem(LocalDateTime.now())
                    .build();
            sanPhamYeuThichRepository.save(banGhiMoi);
            return new TrangThaiYeuThichResponse(maSanPhamId, true, "Đã thêm sản phẩm vào danh sách yêu thích");
        }
    }

    /**
     * Kiểm tra một sản phẩm đã có trong danh sách yêu thích của khách hàng hay chưa.
     *
     * @param idKhachHang ID khách hàng
     * @param maSanPhamId ID sản phẩm
     * @return true nếu đã yêu thích, false nếu chưa
     */
    @Transactional(readOnly = true)
    public boolean kiemTraDaYeuThich(Long idKhachHang, Long maSanPhamId) {
        if (idKhachHang == null || maSanPhamId == null) {
            return false;
        }
        return sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(idKhachHang, maSanPhamId);
    }

    /**
     * Đếm tổng số lượng sản phẩm yêu thích của khách hàng.
     *
     * @param idKhachHang ID khách hàng
     * @return số lượng
     */
    @Transactional(readOnly = true)
    public long demSoLuongYeuThich(Long idKhachHang) {
        if (idKhachHang == null) {
            return 0L;
        }
        return sanPhamYeuThichRepository.countByMaKhachHangId(idKhachHang);
    }

    /**
     * Kiểm tra tính tồn tại và trạng thái kinh doanh của sản phẩm.
     */
    private SanPham kiemTraVaLaySanPham(Long maSanPhamId) {
        if (maSanPhamId == null || maSanPhamId <= 0) {
            throw new DuLieuKhongHopLeException("ID sản phẩm không hợp lệ");
        }
        SanPham sanPham = sanPhamRepository.findById(maSanPhamId)
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm với ID: " + maSanPhamId));

        if (sanPham.getKichHoat() != null && !sanPham.getKichHoat()) {
            throw new DuLieuKhongHopLeException("Sản phẩm hiện đang ngừng kinh doanh, không thể thêm vào yêu thích");
        }
        return sanPham;
    }
}
