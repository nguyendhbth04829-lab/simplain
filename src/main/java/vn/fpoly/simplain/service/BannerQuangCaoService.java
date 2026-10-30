package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.TaoBannerQuangCaoRequest;
import vn.fpoly.simplain.entity.BannerQuangCao;
import vn.fpoly.simplain.repository.BannerQuangCaoRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ Quản lý Banner Quảng Cáo (US30 - Mod 6 / Nội dung - Khoa).
 * - Tiêu chí 2: CRUD banner_quang_cao tại các vị trí hiển thị trên Web.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BannerQuangCaoService {

    private static final String THU_MUC_UPLOAD_BANNER = "uploads/banner";

    private final BannerQuangCaoRepository bannerQuangCaoRepository;

    /**
     * Lấy danh sách banner quảng cáo có tìm kiếm, lọc vị trí, trạng thái kích hoạt và phân trang.
     */
    @Transactional(readOnly = true)
    public Page<BannerQuangCao> layDanhSachBanner(String tuKhoa, String viTri, Boolean kichHoat, Pageable pageable) {
        String tk = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        String vt = (viTri != null && !viTri.isBlank() && !"TAT_CA".equalsIgnoreCase(viTri)) ? viTri.trim() : null;
        return bannerQuangCaoRepository.timKiemBanner(tk, vt, kichHoat, pageable);
    }

    /**
     * Lấy danh sách banner đang kích hoạt theo vị trí hiển thị cụ thể trên Web.
     */
    @Transactional(readOnly = true)
    public List<BannerQuangCao> layBannerTheoViTri(String viTri) {
        if (viTri == null || viTri.isBlank()) {
            return List.of();
        }
        return bannerQuangCaoRepository.findByViTriHienThiAndKichHoatTrueOrderByIdDesc(viTri.trim().toUpperCase());
    }

    /**
     * Lấy chi tiết banner theo ID.
     */
    @Transactional(readOnly = true)
    public BannerQuangCao layTheoId(Long id) {
        return bannerQuangCaoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy banner quảng cáo với ID: " + id));
    }

    /**
     * Tạo mới banner quảng cáo.
     */
    @Transactional
    public BannerQuangCao taoMoi(TaoBannerQuangCaoRequest request, MultipartFile fileAnh) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu banner không được để trống!");
        }

        // 1. Xử lý hình ảnh (upload file hoặc link URL)
        String urlAnh = request.getHinhAnhUrl();
        if (fileAnh != null && !fileAnh.isEmpty()) {
            urlAnh = luuFileAnh(fileAnh);
        }
        if (urlAnh == null || urlAnh.isBlank()) {
            throw new IllegalArgumentException("Vui lòng tải lên file ảnh hoặc nhập đường dẫn link hình ảnh banner!");
        }

        BannerQuangCao banner = BannerQuangCao.builder()
                .tieuDe(request.getTieuDe().trim())
                .hinhAnhUrl(urlAnh.trim())
                .duongDanDich(request.getDuongDanDich() != null ? request.getDuongDanDich().trim() : null)
                .viTriHienThi(request.getViTriHienThi().trim().toUpperCase())
                .kichHoat(request.getKichHoat() != null ? request.getKichHoat() : true)
                .build();

        return bannerQuangCaoRepository.save(banner);
    }

    /**
     * Cập nhật banner quảng cáo.
     */
    @Transactional
    public BannerQuangCao capNhat(Long id, TaoBannerQuangCaoRequest request, MultipartFile fileAnh) {
        BannerQuangCao existing = layTheoId(id);

        // 1. Xử lý hình ảnh
        String urlAnh = existing.getHinhAnhUrl();
        if (fileAnh != null && !fileAnh.isEmpty()) {
            urlAnh = luuFileAnh(fileAnh);
        } else if (request.getHinhAnhUrl() != null && !request.getHinhAnhUrl().isBlank()) {
            urlAnh = request.getHinhAnhUrl().trim();
        }

        existing.setTieuDe(request.getTieuDe().trim());
        existing.setHinhAnhUrl(urlAnh);
        existing.setDuongDanDich(request.getDuongDanDich() != null ? request.getDuongDanDich().trim() : null);
        existing.setViTriHienThi(request.getViTriHienThi().trim().toUpperCase());
        if (request.getKichHoat() != null) {
            existing.setKichHoat(request.getKichHoat());
        }

        return bannerQuangCaoRepository.save(existing);
    }

    /**
     * Bật / tắt trạng thái kích hoạt banner.
     */
    @Transactional
    public boolean chuyenTrangThaiKichHoat(Long id) {
        BannerQuangCao banner = layTheoId(id);
        boolean trangThaiMoi = !Boolean.TRUE.equals(banner.getKichHoat());
        banner.setKichHoat(trangThaiMoi);
        bannerQuangCaoRepository.save(banner);
        log.info("Chuyển trạng thái banner ID [{}] sang: {}", id, trangThaiMoi);
        return trangThaiMoi;
    }

    /**
     * Xóa banner quảng cáo.
     */
    @Transactional
    public void xoa(Long id) {
        BannerQuangCao banner = layTheoId(id);
        bannerQuangCaoRepository.delete(banner);
        log.info("Đã xóa banner quảng cáo ID [{}] thành công", id);
    }

    /**
     * Lưu file ảnh banner tải lên vào thư mục tĩnh uploads/banner.
     */
    private String luuFileAnh(MultipartFile file) {
        try {
            String tenGoc = file.getOriginalFilename();
            String duoiFile = "";
            if (tenGoc != null && tenGoc.contains(".")) {
                duoiFile = tenGoc.substring(tenGoc.lastIndexOf(".")).toLowerCase();
            }
            if (!duoiFile.matches("\\.(jpg|jpeg|png|webp|gif)$")) {
                duoiFile = ".png";
            }

            Path thuMucUpload = Paths.get(THU_MUC_UPLOAD_BANNER);
            if (!Files.exists(thuMucUpload)) {
                Files.createDirectories(thuMucUpload);
            }

            String tenFileMoi = UUID.randomUUID() + duoiFile;
            Path duongDanLuu = thuMucUpload.resolve(tenFileMoi);
            Files.copy(file.getInputStream(), duongDanLuu);

            return "/" + THU_MUC_UPLOAD_BANNER + "/" + tenFileMoi;
        } catch (IOException e) {
            log.error("Lỗi khi lưu file ảnh banner: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lưu file ảnh tải lên: " + e.getMessage());
        }
    }
}
