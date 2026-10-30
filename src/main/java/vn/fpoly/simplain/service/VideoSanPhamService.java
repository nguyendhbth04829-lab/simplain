package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.VideoSanPham;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.VideoSanPhamRepository;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ Quản lý Video quảng cáo sản phẩm [video_san_pham].
 * Mỗi sản phẩm có 1 video (1-to-1).
 * Hỗ trợ chọn video từ máy tính (Upload) hoặc nhập đường dẫn URL video.
 */
@Service
public class VideoSanPhamService {

    private final VideoSanPhamRepository videoSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;

    @Autowired
    public VideoSanPhamService(VideoSanPhamRepository videoSanPhamRepository,
                               SanPhamRepository sanPhamRepository) {
        this.videoSanPhamRepository = videoSanPhamRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    @Transactional(readOnly = true)
    public Page<VideoSanPham> layDanhSachPhanTrang(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<VideoSanPham> page = videoSanPhamRepository.timKiemPhanTrang(tuKhoaChuan, kichHoat, pageable);
        page.getContent().forEach(this::napThongTinSanPham);
        return page;
    }

    @Transactional(readOnly = true)
    public List<VideoSanPham> layTatCa() {
        List<VideoSanPham> list = videoSanPhamRepository.findAll(Sort.by("id").descending());
        list.forEach(this::napThongTinSanPham);
        return list;
    }

    @Transactional(readOnly = true)
    public VideoSanPham layTheoId(Long id) {
        VideoSanPham v = videoSanPhamRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy video quảng cáo với ID: " + id));
        napThongTinSanPham(v);
        return v;
    }

    @Transactional(readOnly = true)
    public Optional<VideoSanPham> layTheoSanPhamId(Long maSanPhamId) {
        Optional<VideoSanPham> opt = videoSanPhamRepository.findByMaSanPhamId(maSanPhamId);
        opt.ifPresent(this::napThongTinSanPham);
        return opt;
    }

    @Transactional(readOnly = true)
    public Optional<VideoSanPham> layVideoKichHoatTheoSanPhamId(Long maSanPhamId) {
        Optional<VideoSanPham> opt = videoSanPhamRepository.findByMaSanPhamIdAndKichHoatTrue(maSanPhamId);
        opt.ifPresent(this::napThongTinSanPham);
        return opt;
    }

    /**
     * Thêm mới Video quảng cáo sản phẩm (hỗ trợ chọn file từ máy hoặc nhập URL).
     */
    @Transactional
    public VideoSanPham them(VideoSanPham video, MultipartFile fileVideo) {
        if (video.getMaSanPhamId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm gắn video quảng cáo!");
        }
        SanPham sp = sanPhamRepository.findById(video.getMaSanPhamId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm đã chọn không tồn tại trên hệ thống!"));

        if (videoSanPhamRepository.existsByMaSanPhamId(video.getMaSanPhamId())) {
            throw new IllegalArgumentException("Sản phẩm [" + sp.getTenSanPham() + "] đã có video quảng cáo! Mỗi sản phẩm chỉ được gắn 1 video duy nhất.");
        }

        if (video.getTieuDe() == null || video.getTieuDe().isBlank()) {
            throw new IllegalArgumentException("Tiêu đề video quảng cáo không được để trống!");
        }

        // Xác định nguồn video: ưu tiên file tải lên từ máy, nếu không thì lấy URL nhập vào
        String urlChot = null;
        if (fileVideo != null && !fileVideo.isEmpty()) {
            urlChot = luuVideoTuFile(fileVideo);
        } else if (video.getVideoUrl() != null && !video.getVideoUrl().isBlank()) {
            urlChot = video.getVideoUrl().trim();
        }

        if (urlChot == null || urlChot.isBlank()) {
            throw new IllegalArgumentException("Vui lòng chọn video từ máy tính hoặc nhập đường dẫn video!");
        }

        video.setTieuDe(video.getTieuDe().trim());
        video.setVideoUrl(urlChot);
        if (video.getMoTa() != null) {
            video.setMoTa(video.getMoTa().trim());
        }
        if (video.getKichHoat() == null) {
            video.setKichHoat(true);
        }

        VideoSanPham saved = videoSanPhamRepository.save(video);

        // Đồng bộ video_url vào bảng san_pham
        sp.setVideoUrl(urlChot);
        sanPhamRepository.save(sp);

        napThongTinSanPham(saved);
        return saved;
    }

    public VideoSanPham them(VideoSanPham video) {
        return them(video, null);
    }

    /**
     * Cập nhật thông tin Video quảng cáo (hỗ trợ chọn file mới từ máy hoặc sửa URL hoặc giữ nguyên).
     */
    @Transactional
    public VideoSanPham sua(Long id, VideoSanPham duLieuMoi, MultipartFile fileVideo) {
        VideoSanPham hienTai = layTheoId(id);

        if (duLieuMoi.getMaSanPhamId() == null) {
            throw new IllegalArgumentException("Vui lòng chọn sản phẩm gắn video quảng cáo!");
        }

        SanPham sp = sanPhamRepository.findById(duLieuMoi.getMaSanPhamId())
                .orElseThrow(() -> new IllegalArgumentException("Sản phẩm đã chọn không tồn tại trên hệ thống!"));

        if (videoSanPhamRepository.existsByMaSanPhamIdAndIdNot(duLieuMoi.getMaSanPhamId(), id)) {
            throw new IllegalArgumentException("Sản phẩm [" + sp.getTenSanPham() + "] đã có video quảng cáo khác! Mỗi sản phẩm chỉ được gắn 1 video duy nhất.");
        }

        if (duLieuMoi.getTieuDe() == null || duLieuMoi.getTieuDe().isBlank()) {
            throw new IllegalArgumentException("Tiêu đề video quảng cáo không được để trống!");
        }

        // Xác định đường dẫn video cập nhật:
        // 1. Nếu có upload file mới từ máy -> dùng file mới
        // 2. Nếu không có file mới nhưng có nhập/sửa URL -> dùng URL mới
        // 3. Nếu cả 2 đều không nhập mới -> giữ nguyên videoUrl hiện tại
        String urlChot = hienTai.getVideoUrl();
        if (fileVideo != null && !fileVideo.isEmpty()) {
            urlChot = luuVideoTuFile(fileVideo);
        } else if (duLieuMoi.getVideoUrl() != null && !duLieuMoi.getVideoUrl().isBlank()) {
            urlChot = duLieuMoi.getVideoUrl().trim();
        }

        if (urlChot == null || urlChot.isBlank()) {
            throw new IllegalArgumentException("Đường dẫn video quảng cáo không được để trống!");
        }

        // Nếu thay đổi sang sản phẩm khác, xóa video_url ở sản phẩm cũ
        if (!hienTai.getMaSanPhamId().equals(duLieuMoi.getMaSanPhamId())) {
            sanPhamRepository.findById(hienTai.getMaSanPhamId()).ifPresent(oldSp -> {
                oldSp.setVideoUrl(null);
                sanPhamRepository.save(oldSp);
            });
        }

        hienTai.setMaSanPhamId(duLieuMoi.getMaSanPhamId());
        hienTai.setTieuDe(duLieuMoi.getTieuDe().trim());
        hienTai.setVideoUrl(urlChot);
        hienTai.setMoTa(duLieuMoi.getMoTa() != null ? duLieuMoi.getMoTa().trim() : null);
        if (duLieuMoi.getKichHoat() != null) {
            hienTai.setKichHoat(duLieuMoi.getKichHoat());
        }

        VideoSanPham saved = videoSanPhamRepository.save(hienTai);

        // Đồng bộ video_url vào sản phẩm mới
        sp.setVideoUrl(hienTai.getVideoUrl());
        sanPhamRepository.save(sp);

        napThongTinSanPham(saved);
        return saved;
    }

    public VideoSanPham sua(Long id, VideoSanPham duLieuMoi) {
        return sua(id, duLieuMoi, null);
    }

    /**
     * Lưu file video tải lên từ máy tính vào thư mục uploads/
     */
    public String luuVideoTuFile(MultipartFile fileVideo) {
        if (fileVideo == null || fileVideo.isEmpty()) {
            return null;
        }
        try {
            String tenGoc = fileVideo.getOriginalFilename();
            String phanMoRong = ".mp4";
            if (tenGoc != null && tenGoc.contains(".")) {
                phanMoRong = tenGoc.substring(tenGoc.lastIndexOf("."));
            }
            String tenTep = "video_" + System.currentTimeMillis() + "_" + UUID.randomUUID().toString().substring(0, 8) + phanMoRong;
            Path thuMuc = Paths.get("uploads/");
            if (!Files.exists(thuMuc)) {
                Files.createDirectories(thuMuc);
            }
            Files.copy(fileVideo.getInputStream(), thuMuc.resolve(tenTep), StandardCopyOption.REPLACE_EXISTING);
            return "/uploads/" + tenTep;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi lưu file video từ máy tính: " + e.getMessage(), e);
        }
    }

    @Transactional
    public VideoSanPham doiTrangThai(Long id) {
        VideoSanPham hienTai = layTheoId(id);
        boolean trangThaiMoi = !Boolean.TRUE.equals(hienTai.getKichHoat());
        hienTai.setKichHoat(trangThaiMoi);
        VideoSanPham saved = videoSanPhamRepository.save(hienTai);
        napThongTinSanPham(saved);
        return saved;
    }

    @Transactional
    public VideoSanPham capNhatTrangThai(Long id, Boolean kichHoat) {
        VideoSanPham hienTai = layTheoId(id);
        hienTai.setKichHoat(kichHoat);
        VideoSanPham saved = videoSanPhamRepository.save(hienTai);
        napThongTinSanPham(saved);
        return saved;
    }

    @Transactional
    public void xoa(Long id) {
        VideoSanPham hienTai = layTheoId(id);
        sanPhamRepository.findById(hienTai.getMaSanPhamId()).ifPresent(sp -> {
            if (hienTai.getVideoUrl().equals(sp.getVideoUrl())) {
                sp.setVideoUrl(null);
                sanPhamRepository.save(sp);
            }
        });
        videoSanPhamRepository.delete(hienTai);
    }

    @Transactional(readOnly = true)
    public long demTongSo() {
        return videoSanPhamRepository.count();
    }

    private void napThongTinSanPham(VideoSanPham video) {
        if (video != null && video.getMaSanPhamId() != null) {
            sanPhamRepository.findById(video.getMaSanPhamId()).ifPresent(sp -> {
                video.setTenSanPham(sp.getTenSanPham());
                video.setMaSanPham(sp.getMaSanPham());
                video.setAnhSanPham(sp.getAnhDaiDienUrl());
            });
        }
    }
}
