/**
 * Tác giả: Gia Minh
 * Chức năng: US15 - Dịch vụ xử lý nghiệp vụ Đánh giá sản phẩm đã mua
 * Cập nhật xác nhận commit US15: 26/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.DanhGiaResponse;
import vn.fpoly.simplain.dto.TaoDanhGiaRequest;
import vn.fpoly.simplain.dto.ThongKeDanhGiaResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.exception.DuLieuKhongHopLeException;
import vn.fpoly.simplain.exception.KhongTimThayException;
import vn.fpoly.simplain.exception.XungDotDuLieuException;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Service quản lý đánh giá sản phẩm, điểm số sao, upload ảnh JSON và kiểm tra điều kiện đơn hàng.
 */
@Service
public class DanhGiaSanPhamService {

    private final DanhGiaSanPhamRepository danhGiaSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final KhachHangService khachHangService;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final DonHangRepository donHangRepository;
    private final TrangThaiDonHangRepository trangThaiDonHangRepository;
    private final ObjectMapper objectMapper;

    public DanhGiaSanPhamService(DanhGiaSanPhamRepository danhGiaSanPhamRepository,
                                SanPhamRepository sanPhamRepository,
                                KhachHangService khachHangService,
                                ChiTietDonHangRepository chiTietDonHangRepository,
                                DonHangRepository donHangRepository,
                                TrangThaiDonHangRepository trangThaiDonHangRepository) {
        this.danhGiaSanPhamRepository = danhGiaSanPhamRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.khachHangService = khachHangService;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.donHangRepository = donHangRepository;
        this.trangThaiDonHangRepository = trangThaiDonHangRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Gửi đánh giá cho sản phẩm từ chi tiết đơn hàng đã mua và hoàn thành.
     *
     * @param idKhachHang ID khách hàng gửi đánh giá
     * @param yeuCau      DTO thông tin đánh giá
     * @return DTO thông tin đánh giá vừa tạo
     */
    @Transactional
    public DanhGiaResponse taoDanhGia(Long idKhachHang, TaoDanhGiaRequest yeuCau) {
        KhachHang khachHang = khachHangService.layKhachHangHopLe(idKhachHang);

        SanPham sanPham = sanPhamRepository.findById(yeuCau.maSanPhamId())
                .orElseThrow(() -> new KhongTimThayException("Không tìm thấy sản phẩm với ID: " + yeuCau.maSanPhamId()));

        // Kiểm tra điều kiện chi tiết đơn hàng
        kiemTraDieuKienDonHang(idKhachHang, yeuCau.maChiTietDonHangId());

        // Kiểm tra chống trùng lặp đánh giá cho cùng 1 món hàng trong đơn
        if (danhGiaSanPhamRepository.existsByMaChiTietDonHangId(yeuCau.maChiTietDonHangId())) {
            throw new XungDotDuLieuException("Sản phẩm trong đơn hàng này đã được bạn đánh giá trước đó");
        }

        String anhJson = chuyenDanhSachAnhThanhJson(yeuCau.danhSachAnh());

        DanhGiaSanPham danhGiaMoi = DanhGiaSanPham.builder()
                .maKhachHangId(idKhachHang)
                .maSanPhamId(yeuCau.maSanPhamId())
                .maChiTietDonHangId(yeuCau.maChiTietDonHangId())
                .soSao(yeuCau.soSao())
                .noiDung(yeuCau.noiDung() != null ? yeuCau.noiDung().trim() : null)
                .danhSachAnhJson(anhJson)
                .ngayTao(LocalDateTime.now())
                .build();

        DanhGiaSanPham daLuu = danhGiaSanPhamRepository.save(danhGiaMoi);

        return DanhGiaResponse.taoMoi(
                daLuu,
                sanPham.getTenSanPham(),
                khachHang.getHoTen(),
                chuyenJsonThanhDanhSachAnh(daLuu.getDanhSachAnhJson())
        );
    }

    /**
     * Lấy danh sách tất cả đánh giá của một sản phẩm.
     *
     * @param maSanPhamId ID sản phẩm
     * @return danh sách DTO đánh giá
     */
    @Transactional(readOnly = true)
    public List<DanhGiaResponse> layDanhSachTheoSanPham(Long maSanPhamId) {
        SanPham sanPham = sanPhamRepository.findById(maSanPhamId).orElse(null);
        String tenSp = sanPham != null ? sanPham.getTenSanPham() : "Sản phẩm";

        List<DanhGiaSanPham> danhSach = danhGiaSanPhamRepository.findAllByMaSanPhamIdOrderByNgayTaoDesc(maSanPhamId);
        return danhSach.stream()
                .map(dg -> DanhGiaResponse.taoMoi(
                        dg,
                        tenSp,
                        layTenKhachHang(dg.getMaKhachHangId()),
                        chuyenJsonThanhDanhSachAnh(dg.getDanhSachAnhJson())
                ))
                .toList();
    }

    /**
     * Lấy danh sách đánh giá của chính khách hàng hiện tại.
     *
     * @param idKhachHang ID khách hàng
     * @return danh sách DTO đánh giá của khách hàng
     */
    @Transactional(readOnly = true)
    public List<DanhGiaResponse> layDanhSachTheoKhachHang(Long idKhachHang) {
        KhachHang kh = khachHangService.layKhachHangHopLe(idKhachHang);

        List<DanhGiaSanPham> danhSach = danhGiaSanPhamRepository.findAllByMaKhachHangIdOrderByNgayTaoDesc(idKhachHang);
        if (danhSach.isEmpty()) {
            return Collections.emptyList();
        }

        List<Long> danhSachSpId = danhSach.stream().map(DanhGiaSanPham::getMaSanPhamId).toList();
        Map<Long, SanPham> banDoSp = sanPhamRepository.findByIdIn(danhSachSpId).stream()
                .collect(Collectors.toMap(SanPham::getId, Function.identity()));

        return danhSach.stream()
                .map(dg -> {
                    SanPham sp = banDoSp.get(dg.getMaSanPhamId());
                    String tenSp = sp != null ? sp.getTenSanPham() : "Sản phẩm";
                    return DanhGiaResponse.taoMoi(
                            dg,
                            tenSp,
                            kh.getHoTen(),
                            chuyenJsonThanhDanhSachAnh(dg.getDanhSachAnhJson())
                    );
                })
                .toList();
    }

    /**
     * Thống kê tổng quan số sao và điểm trung bình của một sản phẩm.
     *
     * @param maSanPhamId ID sản phẩm
     * @return DTO thống kê đánh giá
     */
    @Transactional(readOnly = true)
    public ThongKeDanhGiaResponse layThongKeDanhGia(Long maSanPhamId) {
        List<DanhGiaSanPham> danhSach = danhGiaSanPhamRepository.findAllByMaSanPhamIdOrderByNgayTaoDesc(maSanPhamId);
        if (danhSach.isEmpty()) {
            return new ThongKeDanhGiaResponse(maSanPhamId, 0.0, 0, 0, 0, 0, 0, 0);
        }

        long count5 = danhSach.stream().filter(d -> d.getSoSao() == 5).count();
        long count4 = danhSach.stream().filter(d -> d.getSoSao() == 4).count();
        long count3 = danhSach.stream().filter(d -> d.getSoSao() == 3).count();
        long count2 = danhSach.stream().filter(d -> d.getSoSao() == 2).count();
        long count1 = danhSach.stream().filter(d -> d.getSoSao() == 1).count();

        double tongDiem = danhSach.stream().mapToInt(DanhGiaSanPham::getSoSao).sum();
        double diemTb = BigDecimal.valueOf(tongDiem / danhSach.size())
                .setScale(1, RoundingMode.HALF_UP)
                .doubleValue();

        return new ThongKeDanhGiaResponse(
                maSanPhamId,
                diemTb,
                danhSach.size(),
                count5,
                count4,
                count3,
                count2,
                count1
        );
    }

    /**
     * Kiểm tra điều kiện đơn hàng đã hoàn tất trước khi cho phép đánh giá.
     */
    private void kiemTraDieuKienDonHang(Long idKhachHang, Long maChiTietDonHangId) {
        if (maChiTietDonHangId == null || maChiTietDonHangId <= 0) {
            throw new DuLieuKhongHopLeException("Mã chi tiết đơn hàng không hợp lệ");
        }

        Optional<ChiTietDonHang> optCt = chiTietDonHangRepository.findById(maChiTietDonHangId);
        if (optCt.isPresent()) {
            ChiTietDonHang ct = optCt.get();
            DonHang donHang = donHangRepository.findById(ct.getMaDonHangId())
                    .orElseThrow(() -> new KhongTimThayException("Không tìm thấy đơn hàng tương ứng"));

            if (!idKhachHang.equals(donHang.getMaKhachHangId())) {
                throw new DuLieuKhongHopLeException("Bạn không thể đánh giá sản phẩm trong đơn hàng của khách hàng khác");
            }

            // Kiểm tra trạng thái đơn hàng (nếu có thông tin trạng thái)
            Optional<TrangThaiDonHang> optTt = trangThaiDonHangRepository.findById(donHang.getMaTrangThaiId());
            if (optTt.isPresent()) {
                String maTt = optTt.get().getMaTrangThai();
                if (maTt != null && (maTt.equalsIgnoreCase("HUY") || maTt.equalsIgnoreCase("DA_HUY") || maTt.equalsIgnoreCase("CHO_XAC_NHAN"))) {
                    throw new DuLieuKhongHopLeException("Chỉ có thể đánh giá sản phẩm sau khi đơn hàng đã hoàn thành hoặc giao thành công");
                }
            }
        }
    }

    private String layTenKhachHang(Long maKhachHangId) {
        try {
            KhachHang kh = khachHangService.layKhachHangHopLe(maKhachHangId);
            return kh != null ? kh.getHoTen() : "Khách hàng Simplain";
        } catch (Exception e) {
            return "Khách hàng Simplain";
        }
    }

    private String chuyenDanhSachAnhThanhJson(List<String> danhSachAnh) {
        if (danhSachAnh == null || danhSachAnh.isEmpty()) {
            return "[]";
        }
        try {
            return objectMapper.writeValueAsString(danhSachAnh);
        } catch (Exception e) {
            return "[]";
        }
    }

    private List<String> chuyenJsonThanhDanhSachAnh(String json) {
        if (json == null || json.isBlank() || json.equals("[]")) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }
}
