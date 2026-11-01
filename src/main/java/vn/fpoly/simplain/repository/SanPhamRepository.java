package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.SanPham;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Sản phẩm [san_pham] (US08, US10).
 */
@Repository
public interface SanPhamRepository extends JpaRepository<SanPham, Long> {

    /**
     * Tìm kiếm phân trang sản phẩm theo từ khóa (mã SP, tên SP, slug, chất liệu, form dáng),
     * lọc theo danh mục, bộ sưu tập, form dáng và trạng thái kinh doanh.
     */
    @Query("SELECT sp FROM SanPham sp WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(sp.duongDanSlug) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " (sp.chatLieu IS NOT NULL AND LOWER(sp.chatLieu) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) OR " +
           " (sp.formDang IS NOT NULL AND LOWER(sp.formDang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))) AND " +
           "(:maDanhMucId IS NULL OR sp.maDanhMucId = :maDanhMucId) AND " +
           "(:maBoSuuTapId IS NULL OR sp.maBoSuuTapId = :maBoSuuTapId) AND " +
           "(:formDang IS NULL OR :formDang = '' OR LOWER(sp.formDang) = LOWER(:formDang)) AND " +
           "(:kichHoat IS NULL OR sp.kichHoat = :kichHoat)")
    Page<SanPham> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                   @Param("maDanhMucId") Long maDanhMucId,
                                   @Param("maBoSuuTapId") Long maBoSuuTapId,
                                   @Param("formDang") String formDang,
                                   @Param("kichHoat") Boolean kichHoat,
                                   Pageable pageable);

    /**
     * Tìm kiếm và lọc danh sách sản phẩm trên Storefront cho Khách hàng (US10):
     * - Chỉ hiển thị sản phẩm đang mở bán (kichHoat = true).
     * - Tìm kiếm text (tuKhoa): khớp mã SP, tên SP, slug, chất liệu, form dáng, mô tả hoặc mã SKU.
     * - Bộ lọc theo Danh mục (hỗ trợ cả danh mục cha và danh mục con thông qua danhSachDanhMucId).
     * - Bộ lọc theo Bộ sưu tập (maBoSuuTapId).
     * - Bộ lọc theo Khoảng giá (giaTu, giaDen) kiểm tra trên giá cơ bản và giá bán biến thể.
     * - Bộ lọc theo Màu sắc (maMauSacId) và Kích thước/Size (maKichThuocId) từ bảng biến thể SKU.
     */
    @Query("SELECT sp FROM SanPham sp WHERE sp.kichHoat = true AND " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(sp.maSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(sp.tenSanPham) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(sp.duongDanSlug) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " (sp.chatLieu IS NOT NULL AND LOWER(sp.chatLieu) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) OR " +
           " (sp.formDang IS NOT NULL AND LOWER(sp.formDang) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) OR " +
           " (sp.moTaChiTiet IS NOT NULL AND sp.moTaChiTiet LIKE CONCAT('%', :tuKhoa, '%')) OR " +
           " EXISTS (SELECT 1 FROM BienTheSanPham btTk WHERE btTk.maSanPhamId = sp.id AND btTk.kichHoat = true AND LOWER(btTk.maSku) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))) AND " +
           "(:locTheoDanhMuc = false OR sp.maDanhMucId IN :danhSachDanhMucId) AND " +
           "(:maBoSuuTapId IS NULL OR sp.maBoSuuTapId = :maBoSuuTapId) AND " +
           "(:giaTu IS NULL OR (sp.giaCoBan >= :giaTu OR EXISTS (SELECT 1 FROM BienTheSanPham btMin WHERE btMin.maSanPhamId = sp.id AND btMin.kichHoat = true AND btMin.giaBan >= :giaTu))) AND " +
           "(:giaDen IS NULL OR (sp.giaCoBan <= :giaDen OR EXISTS (SELECT 1 FROM BienTheSanPham btMax WHERE btMax.maSanPhamId = sp.id AND btMax.kichHoat = true AND btMax.giaBan <= :giaDen))) AND " +
           "(:maMauSacId IS NULL OR EXISTS (SELECT 1 FROM BienTheSanPham btMau WHERE btMau.maSanPhamId = sp.id AND btMau.kichHoat = true AND btMau.maMauSacId = :maMauSacId)) AND " +
           "(:maKichThuocId IS NULL OR EXISTS (SELECT 1 FROM BienTheSanPham btSize WHERE btSize.maSanPhamId = sp.id AND btSize.kichHoat = true AND btSize.maKichThuocId = :maKichThuocId))")
    Page<SanPham> timKiemStorefront(@Param("tuKhoa") String tuKhoa,
                                    @Param("locTheoDanhMuc") boolean locTheoDanhMuc,
                                    @Param("danhSachDanhMucId") List<Long> danhSachDanhMucId,
                                    @Param("maBoSuuTapId") Long maBoSuuTapId,
                                    @Param("giaTu") BigDecimal giaTu,
                                    @Param("giaDen") BigDecimal giaDen,
                                    @Param("maMauSacId") Long maMauSacId,
                                    @Param("maKichThuocId") Long maKichThuocId,
                                    Pageable pageable);

    /**
     * Tìm sản phẩm theo mã sản phẩm (không phân biệt hoa thường).
     */
    Optional<SanPham> findByMaSanPhamIgnoreCase(String maSanPham);

    /**
     * Tìm sản phẩm theo đường dẫn slug.
     */
    Optional<SanPham> findByDuongDanSlug(String duongDanSlug);

    /**
     * Tìm sản phẩm theo mã sản phẩm.
     */
    Optional<SanPham> findByMaSanPham(String maSanPham);

    /**
     * Kiểm tra mã sản phẩm đã tồn tại hay chưa.
     */
    boolean existsByMaSanPhamIgnoreCase(String maSanPham);

    /**
     * Kiểm tra mã sản phẩm đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaSanPhamIgnoreCaseAndIdNot(String maSanPham, Long id);

    /**
     * Kiểm tra tên sản phẩm đã tồn tại hay chưa.
     */
    boolean existsByTenSanPhamIgnoreCase(String tenSanPham);

    /**
     * Kiểm tra tên sản phẩm đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByTenSanPhamIgnoreCaseAndIdNot(String tenSanPham, Long id);

    /**
     * Kiểm tra đường dẫn slug đã tồn tại hay chưa.
     */
    boolean existsByDuongDanSlugIgnoreCase(String duongDanSlug);

    /**
     * Kiểm tra đường dẫn slug đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByDuongDanSlugIgnoreCaseAndIdNot(String duongDanSlug, Long id);

    /**
     * Đếm số lượng sản phẩm thuộc một danh mục.
     */
    long countByMaDanhMucId(Long maDanhMucId);

    /**
     * Đếm số lượng sản phẩm thuộc một bộ sưu tập.
     */
    long countByMaBoSuuTapId(Long maBoSuuTapId);

    /**
     * Tìm danh sách sản phẩm thuộc một bộ sưu tập.
     */
    List<SanPham> findByMaBoSuuTapIdOrderByIdDesc(Long maBoSuuTapId);

    /**
     * Tìm danh sách sản phẩm thuộc một bộ sưu tập và đang kích hoạt.
     */
    List<SanPham> findByMaBoSuuTapIdAndKichHoatTrueOrderByIdDesc(Long maBoSuuTapId);

    /**
     * Đếm số lượng sản phẩm đang kích hoạt kinh doanh.
     */
    long countByKichHoatTrue();

    /**
     * Đếm số lượng sản phẩm sử dụng một chất liệu vải.
     */
    long countByMaChatLieuId(Long maChatLieuId);

    /**
     * Đếm số lượng sản phẩm sử dụng một form dáng.
     */
    long countByMaFormDangId(Long maFormDangId);

    /**
     * Đếm số lượng sản phẩm được đánh dấu nổi bật (Best Seller).
     */
    long countByNoiBatTrue();
}
