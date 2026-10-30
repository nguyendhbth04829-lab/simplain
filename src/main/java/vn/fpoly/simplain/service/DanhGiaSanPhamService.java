package vn.fpoly.simplain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.DanhGiaRequest;
import vn.fpoly.simplain.dto.ThongKeDanhGiaResponse;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Service xử lý nghiệp vụ Đánh giá sản phẩm đã mua (US15).
 * 1. Check điều kiện ma_chi_tiet_don_hang_id đã hoàn thành.
 * 2. Upload JSON ảnh.
 * 3. Rating 1-5 sao.
 */
@Service
public class DanhGiaSanPhamService {

    private final DanhGiaSanPhamRepository danhGiaSanPhamRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final DonHangRepository donHangRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final ObjectMapper objectMapper;

    private static final String THU_MUC_UPLOAD_ANH = "uploads/danh-gia";

    @Autowired
    public DanhGiaSanPhamService(DanhGiaSanPhamRepository danhGiaSanPhamRepository,
                                 ChiTietDonHangRepository chiTietDonHangRepository,
                                 DonHangRepository donHangRepository,
                                 BienTheSanPhamRepository bienTheSanPhamRepository,
                                 SanPhamRepository sanPhamRepository) {
        this.danhGiaSanPhamRepository = danhGiaSanPhamRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.donHangRepository = donHangRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.objectMapper = new ObjectMapper();
    }

    /**
     * Tiêu chí 1: Kiểm tra chặt chẽ điều kiện ma_chi_tiet_don_hang_id:
     * - Chi tiết đơn hàng phải tồn tại.
     * - Đơn hàng phải thuộc về chính khách hàng đang đăng nhập.
     * - Đơn hàng phải ở trạng thái HOAN_THANH (id=4 hoặc maTrangThai='HOAN_THANH').
     * - Chi tiết đơn hàng đó chưa từng được đánh giá trước đây.
     * - Đúng sản phẩm cần đánh giá.
     */
    @Transactional(readOnly = true)
    public void kiemTraDieuKienDanhGia(Long maKhachHangId, Long maSanPhamId, Long maChiTietDonHangId) {
        if (maKhachHangId == null) {
            throw new IllegalArgumentException("Vui lòng đăng nhập để thực hiện đánh giá sản phẩm!");
        }
        if (maChiTietDonHangId == null) {
            throw new IllegalArgumentException("Vui lòng chọn chi tiết đơn hàng đã mua để đánh giá!");
        }

        ChiTietDonHang ct = chiTietDonHangRepository.findById(maChiTietDonHangId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chi tiết đơn hàng tương ứng (ID: " + maChiTietDonHangId + ")!"));

        DonHang dh = ct.getDonHang();
        if (dh == null && ct.getMaDonHangId() != null) {
            dh = donHangRepository.findById(ct.getMaDonHangId()).orElse(null);
        }
        if (dh == null) {
            throw new IllegalArgumentException("Không tìm thấy đơn hàng chứa mặt hàng này!");
        }

        if (dh.getKhachHang() == null || !Objects.equals(dh.getKhachHang().getId(), maKhachHangId)) {
            throw new IllegalArgumentException("Bạn chỉ được đánh giá các đơn hàng do chính tài khoản của bạn đặt mua!");
        }

        TrangThaiDonHang tt = dh.getTrangThaiDonHang();
        boolean daHoanThanh = (tt != null && ("HOAN_THANH".equalsIgnoreCase(tt.getMaTrangThai()) || Long.valueOf(4).equals(tt.getId())));
        if (!daHoanThanh) {
            throw new IllegalArgumentException("Đơn hàng chưa hoàn thành! Bạn chỉ có thể đánh giá sau khi đơn hàng được giao thành công (HOAN_THANH).");
        }

        BienTheSanPham bt = ct.getBienThe();
        if (bt == null && ct.getMaBienTheId() != null) {
            bt = bienTheSanPhamRepository.findById(ct.getMaBienTheId()).orElse(null);
        }
        if (bt != null && maSanPhamId != null && !Objects.equals(bt.getMaSanPhamId(), maSanPhamId)) {
            throw new IllegalArgumentException("Chi tiết đơn hàng không thuộc về sản phẩm bạn đang đánh giá!");
        }

        if (danhGiaSanPhamRepository.existsByMaChiTietDonHangId(maChiTietDonHangId)) {
            throw new IllegalArgumentException("Sản phẩm trong đơn hàng này đã được bạn gửi đánh giá trước đó rồi!");
        }
    }

    /**
     * Lấy danh sách chi tiết đơn hàng đủ điều kiện đánh giá của khách hàng đối với một sản phẩm cụ thể.
     */
    @Transactional(readOnly = true)
    public List<ChiTietDonHang> layDanhSachChiTietDonHangDuDieuKien(Long khachHangId, Long sanPhamId) {
        if (khachHangId == null || sanPhamId == null) {
            return Collections.emptyList();
        }
        List<ChiTietDonHang> dsHoanThanh = chiTietDonHangRepository.findChiTietDonHangHoanThanhTheoSanPham(khachHangId, sanPhamId);
        return dsHoanThanh.stream()
                .filter(ct -> !danhGiaSanPhamRepository.existsByMaChiTietDonHangId(ct.getId()))
                .collect(Collectors.toList());
    }

    /**
     * Thêm mới đánh giá sản phẩm (US15 - Đầy đủ 3 tiêu chí):
     * 1. Check điều kiện ma_chi_tiet_don_hang_id đã hoàn thành.
     * 2. Upload JSON ảnh.
     * 3. Rating 1-5 sao.
     */
    @Transactional
    public DanhGiaSanPham themDanhGia(Long maKhachHangId, DanhGiaRequest req, MultipartFile[] anhFiles) {
        // Tiêu chí 1: Validate điều kiện đơn hàng hoàn thành & chưa đánh giá
        kiemTraDieuKienDanhGia(maKhachHangId, req.getMaSanPhamId(), req.getMaChiTietDonHangId());

        // Tiêu chí 3: Validate số sao đánh giá (1-5 sao)
        if (req.getSoSao() == null || req.getSoSao() < 1 || req.getSoSao() > 5) {
            throw new IllegalArgumentException("Số sao đánh giá phải từ 1 đến 5 sao!");
        }

        if (req.getNoiDung() == null || req.getNoiDung().trim().length() < 5) {
            throw new IllegalArgumentException("Nội dung đánh giá cần ít nhất 5 ký tự để chia sẻ trải nghiệm hữu ích!");
        }

        // Tiêu chí 2: Xử lý danh sách ảnh dạng JSON
        List<String> danhSachAnh = new ArrayList<>();

        // 1. Lưu file ảnh tải lên từ máy tính (nếu có)
        if (anhFiles != null) {
            for (MultipartFile file : anhFiles) {
                if (file != null && !file.isEmpty()) {
                    String url = luuAnhDanhGia(file);
                    danhSachAnh.add(url);
                }
            }
        }

        // 2. Thêm các URL ảnh trực tiếp (nếu có)
        if (req.getDanhSachAnhUrls() != null) {
            for (String url : req.getDanhSachAnhUrls()) {
                if (url != null && !url.isBlank()) {
                    danhSachAnh.add(url.trim());
                }
            }
        }

        // Chuyển danh sách đường dẫn ảnh sang định dạng chuỗi JSON
        String jsonAnh = null;
        try {
            if (!danhSachAnh.isEmpty()) {
                jsonAnh = objectMapper.writeValueAsString(danhSachAnh);
            }
        } catch (Exception e) {
            jsonAnh = "[]";
        }

        DanhGiaSanPham danhGia = DanhGiaSanPham.builder()
                .maSanPhamId(req.getMaSanPhamId())
                .maKhachHangId(maKhachHangId)
                .maChiTietDonHangId(req.getMaChiTietDonHangId())
                .soSao(req.getSoSao())
                .noiDung(req.getNoiDung().trim())
                .danhSachAnhJson(jsonAnh)
                .ngayTao(LocalDateTime.now())
                .build();

        danhGia.setDanhSachAnh(danhSachAnh);
        return danhGiaSanPhamRepository.save(danhGia);
    }

    /**
     * Lấy toàn bộ danh sách đánh giá của sản phẩm kèm giải mã JSON ảnh phục vụ hiển thị.
     */
    @Transactional(readOnly = true)
    public List<DanhGiaSanPham> layDanhSachDanhGiaTheoSanPham(Long sanPhamId) {
        if (sanPhamId == null) {
            return Collections.emptyList();
        }
        List<DanhGiaSanPham> ds = danhGiaSanPhamRepository.findByMaSanPhamIdOrderByNgayTaoDesc(sanPhamId);
        for (DanhGiaSanPham dg : ds) {
            dg.setDanhSachAnh(giaiMaDanhSachAnh(dg.getDanhSachAnhJson()));
        }
        return ds;
    }

    /**
     * Lấy danh sách đánh giá của một khách hàng cụ thể.
     */
    @Transactional(readOnly = true)
    public List<DanhGiaSanPham> layDanhSachDanhGiaCuaKhachHang(Long khachHangId) {
        if (khachHangId == null) {
            return Collections.emptyList();
        }
        List<DanhGiaSanPham> ds = danhGiaSanPhamRepository.findByMaKhachHangIdOrderByNgayTaoDesc(khachHangId);
        for (DanhGiaSanPham dg : ds) {
            dg.setDanhSachAnh(giaiMaDanhSachAnh(dg.getDanhSachAnhJson()));
        }
        return ds;
    }

    /**
     * Thống kê số lượng sao và điểm trung bình của sản phẩm (US15 - Tiêu chí 3).
     */
    @Transactional(readOnly = true)
    public ThongKeDanhGiaResponse layThongKeDanhGiaTheoSanPham(Long sanPhamId) {
        if (sanPhamId == null) {
            return ThongKeDanhGiaResponse.builder().build();
        }

        long tong = danhGiaSanPhamRepository.countByMaSanPhamId(sanPhamId);
        Double saoTbRaw = danhGiaSanPhamRepository.tinhSaoTrungBinh(sanPhamId);
        double saoTb = saoTbRaw != null ? Math.round(saoTbRaw * 10.0) / 10.0 : 5.0;

        long s5 = danhGiaSanPhamRepository.countByMaSanPhamIdAndSoSao(sanPhamId, 5);
        long s4 = danhGiaSanPhamRepository.countByMaSanPhamIdAndSoSao(sanPhamId, 4);
        long s3 = danhGiaSanPhamRepository.countByMaSanPhamIdAndSoSao(sanPhamId, 3);
        long s2 = danhGiaSanPhamRepository.countByMaSanPhamIdAndSoSao(sanPhamId, 2);
        long s1 = danhGiaSanPhamRepository.countByMaSanPhamIdAndSoSao(sanPhamId, 1);

        int pt5 = tong > 0 ? (int) Math.round((double) s5 * 100 / tong) : 0;
        int pt4 = tong > 0 ? (int) Math.round((double) s4 * 100 / tong) : 0;
        int pt3 = tong > 0 ? (int) Math.round((double) s3 * 100 / tong) : 0;
        int pt2 = tong > 0 ? (int) Math.round((double) s2 * 100 / tong) : 0;
        int pt1 = tong > 0 ? (int) Math.round((double) s1 * 100 / tong) : 0;

        return ThongKeDanhGiaResponse.builder()
                .tongDanhGia(tong)
                .saoTrungBinh(saoTb)
                .soLuong5Sao(s5)
                .soLuong4Sao(s4)
                .soLuong3Sao(s3)
                .soLuong2Sao(s2)
                .soLuong1Sao(s1)
                .phanTram5Sao(pt5)
                .phanTram4Sao(pt4)
                .phanTram3Sao(pt3)
                .phanTram2Sao(pt2)
                .phanTram1Sao(pt1)
                .build();
    }

    /**
     * Giải mã chuỗi JSON ảnh thành danh sách đường dẫn URL.
     */
    public List<String> giaiMaDanhSachAnh(String json) {
        if (json == null || json.isBlank() || "[]".equals(json.trim())) {
            return Collections.emptyList();
        }
        try {
            return objectMapper.readValue(json, new TypeReference<List<String>>() {});
        } catch (Exception e) {
            return Collections.emptyList();
        }
    }

    /**
     * Lưu file ảnh đánh giá tải lên vào thư mục uploads/danh-gia/.
     */
    public String luuAnhDanhGia(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            return null;
        }
        try {
            String tenGoc = file.getOriginalFilename();
            String duoiTep = ".jpg";
            if (tenGoc != null && tenGoc.contains(".")) {
                duoiTep = tenGoc.substring(tenGoc.lastIndexOf("."));
            }
            String tenTep = "review_" + UUID.randomUUID().toString().replace("-", "") + duoiTep;
            Path thuMuc = Paths.get(THU_MUC_UPLOAD_ANH);
            if (!Files.exists(thuMuc)) {
                Files.createDirectories(thuMuc);
            }
            Files.copy(file.getInputStream(), thuMuc.resolve(tenTep), StandardCopyOption.REPLACE_EXISTING);
            return "/" + THU_MUC_UPLOAD_ANH + "/" + tenTep;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file ảnh đánh giá: " + e.getMessage(), e);
        }
    }
}
