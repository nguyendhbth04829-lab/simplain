package vn.fpoly.simplain.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.fpoly.simplain.dto.TaoBaiVietBlogRequest;
import vn.fpoly.simplain.entity.BaiVietBlog;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.repository.BaiVietBlogRepository;
import vn.fpoly.simplain.repository.NhanVienRepository;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.Normalizer;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.regex.Pattern;

/**
 * Service xử lý nghiệp vụ Quản lý Bài viết Blog (US30 - Mod 6 / Nội dung - Khoa).
 * - Tiêu chí 1: CRUD bai_viet_blog (SEO friendly).
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class BaiVietBlogService {

    private static final String THU_MUC_UPLOAD_BLOG = "uploads/blog";
    private static final Pattern DIACRITICAL_MARKS = Pattern.compile("\\p{InCombiningDiacriticalMarks}+");

    private final BaiVietBlogRepository baiVietBlogRepository;
    private final NhanVienRepository nhanVienRepository;

    /**
     * Lấy danh sách bài viết blog có tìm kiếm từ khóa, lọc chuyên mục và phân trang.
     */
    @Transactional(readOnly = true)
    public Page<BaiVietBlog> layDanhSachBaiViet(String tuKhoa, String chuyenMuc, Pageable pageable) {
        String tk = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        String cm = (chuyenMuc != null && !chuyenMuc.isBlank() && !"TAT_CA".equalsIgnoreCase(chuyenMuc)) ? chuyenMuc.trim() : null;
        Page<BaiVietBlog> page = baiVietBlogRepository.timKiemBaiViet(tk, cm, pageable);

        // Nạp tên tác giả phục vụ hiển thị
        page.forEach(b -> {
            if (b.getTacGia() != null) {
                b.setTenTacGia(b.getTacGia().getHoTen());
            } else if (b.getMaTacGiaId() != null) {
                nhanVienRepository.findById(b.getMaTacGiaId()).ifPresent(nv -> b.setTenTacGia(nv.getHoTen()));
            }
            if (b.getTenTacGia() == null) {
                b.setTenTacGia("Admin Hệ thống");
            }
        });
        return page;
    }

    /**
     * Lấy danh sách tất cả chuyên mục blog hiện có.
     */
    @Transactional(readOnly = true)
    public List<String> layDanhSachChuyenMuc() {
        return baiVietBlogRepository.layDanhSachChuyenMuc();
    }

    /**
     * Lấy chi tiết bài viết blog theo ID.
     */
    @Transactional(readOnly = true)
    public BaiVietBlog layTheoId(Long id) {
        return baiVietBlogRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết blog với ID: " + id));
    }

    /**
     * Lấy chi tiết bài viết blog theo Slug SEO.
     */
    @Transactional(readOnly = true)
    public BaiVietBlog layTheoSlug(String slug) {
        return baiVietBlogRepository.findByDuongDanSlug(slug)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy bài viết blog với Slug: " + slug));
    }

    /**
     * Tạo mới bài viết blog (SEO friendly).
     */
    @Transactional
    public BaiVietBlog taoMoi(TaoBaiVietBlogRequest request, MultipartFile fileAnh, Long maTacGiaId) {
        if (request == null) {
            throw new IllegalArgumentException("Dữ liệu bài viết không được để trống!");
        }

        // 1. Chuẩn hóa & kiểm tra slug SEO
        String slug = chuanHoaVaTaoSlug(request.getDuongDanSlug(), request.getTieuDe());
        if (baiVietBlogRepository.existsByDuongDanSlugIgnoreCase(slug)) {
            throw new IllegalArgumentException("Đường dẫn slug [" + slug + "] đã tồn tại trong hệ thống. Vui lòng chọn slug khác!");
        }

        // 2. Xác định tác giả
        Long tacGiaId = request.getMaTacGiaId();
        if (tacGiaId == null) {
            tacGiaId = maTacGiaId;
        }
        if (tacGiaId == null) {
            NhanVien defaultAdmin = nhanVienRepository.findAll().stream().findFirst().orElse(null);
            if (defaultAdmin != null) {
                tacGiaId = defaultAdmin.getId();
            } else {
                throw new IllegalStateException("Không tìm thấy tài khoản nhân viên / admin tác giả trong hệ thống!");
            }
        }

        // 3. Xử lý ảnh đại diện
        String urlAnh = request.getAnhDaiDienUrl();
        if (fileAnh != null && !fileAnh.isEmpty()) {
            urlAnh = luuFileAnh(fileAnh);
        }

        // 4. Ngày xuất bản
        LocalDateTime ngayXuatBan = request.getNgayXuatBan();
        if (ngayXuatBan == null) {
            ngayXuatBan = LocalDateTime.now();
        }

        BaiVietBlog blog = BaiVietBlog.builder()
                .maTacGiaId(tacGiaId)
                .chuyenMuc(request.getChuyenMuc().trim())
                .tieuDe(request.getTieuDe().trim())
                .duongDanSlug(slug)
                .moTaNgan(request.getMoTaNgan() != null ? request.getMoTaNgan().trim() : null)
                .noiDungHtml(request.getNoiDungHtml())
                .anhDaiDienUrl(urlAnh)
                .ngayXuatBan(ngayXuatBan)
                .build();

        return baiVietBlogRepository.save(blog);
    }

    /**
     * Cập nhật bài viết blog.
     */
    @Transactional
    public BaiVietBlog capNhat(Long id, TaoBaiVietBlogRequest request, MultipartFile fileAnh) {
        BaiVietBlog existing = layTheoId(id);

        // 1. Chuẩn hóa & kiểm tra slug SEO tránh trùng bài khác
        String slug = chuanHoaVaTaoSlug(request.getDuongDanSlug(), request.getTieuDe());
        if (baiVietBlogRepository.existsByDuongDanSlugIgnoreCaseAndIdNot(slug, id)) {
            throw new IllegalArgumentException("Đường dẫn slug [" + slug + "] đã được sử dụng bởi một bài viết khác!");
        }

        // 2. Xử lý ảnh đại diện
        String urlAnh = existing.getAnhDaiDienUrl();
        if (fileAnh != null && !fileAnh.isEmpty()) {
            urlAnh = luuFileAnh(fileAnh);
        } else if (request.getAnhDaiDienUrl() != null && !request.getAnhDaiDienUrl().isBlank()) {
            urlAnh = request.getAnhDaiDienUrl().trim();
        }

        // 3. Cập nhật dữ liệu
        existing.setChuyenMuc(request.getChuyenMuc().trim());
        existing.setTieuDe(request.getTieuDe().trim());
        existing.setDuongDanSlug(slug);
        existing.setMoTaNgan(request.getMoTaNgan() != null ? request.getMoTaNgan().trim() : null);
        existing.setNoiDungHtml(request.getNoiDungHtml());
        existing.setAnhDaiDienUrl(urlAnh);
        if (request.getNgayXuatBan() != null) {
            existing.setNgayXuatBan(request.getNgayXuatBan());
        }

        return baiVietBlogRepository.save(existing);
    }

    /**
     * Xóa bài viết blog.
     */
    @Transactional
    public void xoa(Long id) {
        BaiVietBlog existing = layTheoId(id);
        baiVietBlogRepository.delete(existing);
        log.info("Đã xóa bài viết blog ID [{}] thành công", id);
    }

    /**
     * Tiện ích sinh đường dẫn slug SEO chuẩn tiếng Việt không dấu.
     */
    public String taoSlugSeo(String input) {
        if (input == null || input.isBlank()) {
            return "";
        }
        String text = input.trim().toLowerCase(Locale.ROOT);
        // Thay ký tự đặc thù tiếng Việt
        text = text.replace("đ", "d").replace("Đ", "d");
        // Bỏ dấu thanh NFD
        String normalized = Normalizer.normalize(text, Normalizer.Form.NFD);
        String noAccent = DIACRITICAL_MARKS.matcher(normalized).replaceAll("");
        // Chỉ giữ chữ thường và số, thay thế ký tự còn lại bằng gạch ngang
        String slug = noAccent.replaceAll("[^a-z0-9]+", "-");
        // Xóa gạch ngang đầu/cuối và thu gọn gạch ngang kép
        slug = slug.replaceAll("^-+|-+$", "").replaceAll("-{2,}", "-");
        return slug;
    }

    /**
     * Chuẩn hóa slug nếu người dùng nhập hoặc tự sinh từ tiêu đề.
     */
    public String chuanHoaVaTaoSlug(String inputSlug, String tieuDe) {
        if (inputSlug != null && !inputSlug.isBlank()) {
            String slug = taoSlugSeo(inputSlug);
            if (!slug.isBlank()) {
                return slug;
            }
        }
        String slugFromTitle = taoSlugSeo(tieuDe);
        if (slugFromTitle.isBlank()) {
            slugFromTitle = "bai-viet-" + System.currentTimeMillis();
        }
        return slugFromTitle;
    }

    /**
     * Lưu file ảnh tải lên vào thư mục tĩnh uploads/blog.
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

            Path thuMucUpload = Paths.get(THU_MUC_UPLOAD_BLOG);
            if (!Files.exists(thuMucUpload)) {
                Files.createDirectories(thuMucUpload);
            }

            String tenFileMoi = UUID.randomUUID() + duoiFile;
            Path duongDanLuu = thuMucUpload.resolve(tenFileMoi);
            Files.copy(file.getInputStream(), duongDanLuu);

            return "/" + THU_MUC_UPLOAD_BLOG + "/" + tenFileMoi;
        } catch (IOException e) {
            log.error("Lỗi khi lưu file ảnh blog: {}", e.getMessage(), e);
            throw new RuntimeException("Không thể lưu file ảnh tải lên: " + e.getMessage());
        }
    }
}
