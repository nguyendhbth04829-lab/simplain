package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.YeuThichToggleResponse;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.SanPhamYeuThich;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamYeuThichRepository;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Thêm / Bỏ yêu thích và Quản lý danh sách Wishlist (US14).
 */
@Service
public class SanPhamYeuThichService {

    private final SanPhamYeuThichRepository sanPhamYeuThichRepository;
    private final SanPhamRepository sanPhamRepository;
    private final SanPhamService sanPhamService;

    @Autowired
    public SanPhamYeuThichService(SanPhamYeuThichRepository sanPhamYeuThichRepository,
                                 SanPhamRepository sanPhamRepository,
                                 SanPhamService sanPhamService) {
        this.sanPhamYeuThichRepository = sanPhamYeuThichRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.sanPhamService = sanPhamService;
    }

    /**
     * Thao tác nhấn tim sản phẩm (Toggle Yêu thích - Tiêu chí 1):
     * - Nếu sản phẩm chưa có trong danh sách -> Thêm vào san_pham_yeu_thich.
     * - Nếu sản phẩm đã có -> Xóa khỏi san_pham_yeu_thich.
     */
    @Transactional
    public YeuThichToggleResponse toggleYeuThich(Long maKhachHangId, Long maSanPhamId) {
        if (maKhachHangId == null) {
            throw new IllegalArgumentException("Khách hàng chưa đăng nhập hoặc ID không hợp lệ!");
        }
        if (maSanPhamId == null) {
            throw new IllegalArgumentException("Mã sản phẩm không được để trống!");
        }

        SanPham sp = sanPhamRepository.findById(maSanPhamId)
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm không tồn tại trong hệ thống!"));

        boolean daThich = sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(maKhachHangId, maSanPhamId);

        if (daThich) {
            sanPhamYeuThichRepository.xoaYeuThich(maKhachHangId, maSanPhamId);
            long tongConLai = sanPhamYeuThichRepository.countByMaKhachHangId(maKhachHangId);
            return YeuThichToggleResponse.builder()
                    .thanhCong(true)
                    .daYeuThich(false)
                    .thongBao("Đã bỏ '" + sp.getTenSanPham() + "' khỏi danh sách yêu thích.")
                    .maSanPhamId(maSanPhamId)
                    .tongSoYeuThich(tongConLai)
                    .build();
        } else {
            SanPhamYeuThich yt = SanPhamYeuThich.builder()
                    .maKhachHangId(maKhachHangId)
                    .maSanPhamId(maSanPhamId)
                    .ngayThem(LocalDateTime.now())
                    .build();
            sanPhamYeuThichRepository.save(yt);
            long tongMoi = sanPhamYeuThichRepository.countByMaKhachHangId(maKhachHangId);
            return YeuThichToggleResponse.builder()
                    .thanhCong(true)
                    .daYeuThich(true)
                    .thongBao("Đã thêm '" + sp.getTenSanPham() + "' vào danh sách yêu thích!")
                    .maSanPhamId(maSanPhamId)
                    .tongSoYeuThich(tongMoi)
                    .build();
        }
    }

    /**
     * Lấy danh sách sản phẩm yêu thích đã được làm giàu thông tin hiển thị (Tiêu chí 2).
     */
    @Transactional(readOnly = true)
    public List<SanPham> layDanhSachSanPhamYeuThich(Long maKhachHangId) {
        if (maKhachHangId == null) {
            return Collections.emptyList();
        }

        List<SanPhamYeuThich> dsYeuThich = sanPhamYeuThichRepository.findByMaKhachHangIdOrderByNgayThemDesc(maKhachHangId);
        if (dsYeuThich.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> dsIdSp = dsYeuThich.stream()
                .map(SanPhamYeuThich::getMaSanPhamId)
                .collect(Collectors.toList());

        List<SanPham> danhSachSp = sanPhamRepository.findAllById(dsIdSp);

        // Giữ thứ tự thêm mới nhất lên đầu theo dsIdSp
        Map<Long, SanPham> mapSp = danhSachSp.stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));

        List<SanPham> ketQuaSapXep = new ArrayList<>();
        for (Long id : dsIdSp) {
            if (mapSp.containsKey(id)) {
                ketQuaSapXep.add(mapSp.get(id));
            }
        }

        // Làm giàu thông tin giá, ảnh đại diện, màu sắc, size, tồn kho
        sanPhamService.lamGiauThongTinHienThi(ketQuaSapXep);

        return ketQuaSapXep;
    }

    /**
     * Lấy danh sách ID các sản phẩm khách hàng đang yêu thích (để tô màu tim trên Storefront).
     */
    @Transactional(readOnly = true)
    public List<Long> layDanhSachMaSanPhamYeuThich(Long maKhachHangId) {
        if (maKhachHangId == null) {
            return Collections.emptyList();
        }
        return sanPhamYeuThichRepository.findMaSanPhamIdsByMaKhachHangId(maKhachHangId);
    }

    /**
     * Kiểm tra một sản phẩm cụ thể đã được khách hàng yêu thích hay chưa.
     */
    @Transactional(readOnly = true)
    public boolean kiemTraDaYeuThich(Long maKhachHangId, Long maSanPhamId) {
        if (maKhachHangId == null || maSanPhamId == null) {
            return false;
        }
        return sanPhamYeuThichRepository.existsByMaKhachHangIdAndMaSanPhamId(maKhachHangId, maSanPhamId);
    }

    /**
     * Đếm tổng số sản phẩm yêu thích của khách hàng.
     */
    @Transactional(readOnly = true)
    public long demSoLuongYeuThich(Long maKhachHangId) {
        if (maKhachHangId == null) {
            return 0;
        }
        return sanPhamYeuThichRepository.countByMaKhachHangId(maKhachHangId);
    }

    /**
     * Xóa sản phẩm khỏi danh sách yêu thích.
     */
    @Transactional
    public void xoaKhoiYeuThich(Long maKhachHangId, Long maSanPhamId) {
        if (maKhachHangId == null || maSanPhamId == null) {
            return;
        }
        sanPhamYeuThichRepository.xoaYeuThich(maKhachHangId, maSanPhamId);
    }
}
