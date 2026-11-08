package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Biến thể sản phẩm (SKU) [bien_the_san_pham] (US09).
 */
@Repository
public interface BienTheSanPhamRepository extends JpaRepository<BienTheSanPham, Long> {
//
    /**
     * Tìm kiếm phân trang biến thể SKU theo từ khóa (mã SKU, mã vạch barcode),
     * lọc theo sản phẩm gốc, màu sắc, kích thước, khoảng giá bán, trạng thái tồn kho và trạng thái kích hoạt.
     */
    @Query("SELECT bt FROM BienTheSanPham bt WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(bt.maSku) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " (bt.maVachBarcode IS NOT NULL AND LOWER(bt.maVachBarcode) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))) AND " +
           "(:maSanPhamId IS NULL OR bt.maSanPhamId = :maSanPhamId) AND " +
           "(:maMauSacId IS NULL OR bt.maMauSacId = :maMauSacId) AND " +
           "(:maKichThuocId IS NULL OR bt.maKichThuocId = :maKichThuocId) AND " +
           "(:giaTu IS NULL OR bt.giaBan >= :giaTu) AND " +
           "(:giaDen IS NULL OR bt.giaBan <= :giaDen) AND " +
           "(:tonKhoTrangThai IS NULL OR :tonKhoTrangThai = '' OR " +
           " (:tonKhoTrangThai = 'CON_HANG' AND bt.soLuong > 0) OR " +
           " (:tonKhoTrangThai = 'SAP_HET' AND bt.soLuong > 0 AND bt.soLuong <= 5) OR " +
           " (:tonKhoTrangThai = 'HET_HANG' AND (bt.soLuong IS NULL OR bt.soLuong <= 0))) AND " +
           "(:kichHoat IS NULL OR bt.kichHoat = :kichHoat)")
    Page<BienTheSanPham> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                          @Param("maSanPhamId") Long maSanPhamId,
                                          @Param("maMauSacId") Long maMauSacId,
                                          @Param("maKichThuocId") Long maKichThuocId,
                                          @Param("giaTu") java.math.BigDecimal giaTu,
                                          @Param("giaDen") java.math.BigDecimal giaDen,
                                          @Param("tonKhoTrangThai") String tonKhoTrangThai,
                                          @Param("kichHoat") Boolean kichHoat,
                                          Pageable pageable);

    default Page<BienTheSanPham> timKiemPhanTrang(String tuKhoa, Long maSanPhamId, Long maMauSacId, Long maKichThuocId, Boolean kichHoat, Pageable pageable) {
        return timKiemPhanTrang(tuKhoa, maSanPhamId, maMauSacId, maKichThuocId, null, null, null, kichHoat, pageable);
    }

    /**
     * Lấy danh sách tất cả biến thể của một sản phẩm.
     */
    List<BienTheSanPham> findByMaSanPhamIdOrderByIdDesc(Long maSanPhamId);

    /**
     * Lấy danh sách các biến thể của cùng một sản phẩm và cùng màu sắc (để dùng chung hình ảnh giữa các size).
     */
    List<BienTheSanPham> findByMaSanPhamIdAndMaMauSacId(Long maSanPhamId, Long maMauSacId);

    default List<BienTheSanPham> findByMaSanPhamId(Long maSanPhamId) {
        return findByMaSanPhamIdOrderByIdDesc(maSanPhamId);
    }

    long countByMaSanPhamId(Long maSanPhamId);

    Optional<BienTheSanPham> findByMaSanPhamIdAndMaMauSacIdAndMaKichThuocId(Long maSanPhamId, Long maMauSacId, Long maKichThuocId);

    default boolean existsByMaSku(String maSku) {
        return existsByMaSkuIgnoreCase(maSku);
    }

    /**
     * Tìm biến thể theo mã SKU (không phân biệt hoa thường).
     */
    Optional<BienTheSanPham> findByMaSkuIgnoreCase(String maSku);

    /**
     * Tìm biến thể theo mã vạch Barcode.
     */
    Optional<BienTheSanPham> findByMaVachBarcodeIgnoreCase(String maVachBarcode);

    /**
     * Kiểm tra mã SKU đã tồn tại hay chưa.
     */
    boolean existsByMaSkuIgnoreCase(String maSku);

    /**
     * Kiểm tra mã SKU đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaSkuIgnoreCaseAndIdNot(String maSku, Long id);

    /**
     * Kiểm tra mã vạch Barcode đã tồn tại hay chưa.
     */
    boolean existsByMaVachBarcodeIgnoreCase(String maVachBarcode);

    /**
     * Kiểm tra mã vạch Barcode đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaVachBarcodeIgnoreCaseAndIdNot(String maVachBarcode, Long id);

    /**
     * Kiểm tra cặp (Sản phẩm + Màu sắc + Kích thước) đã tồn tại hay chưa (Chặn tạo trùng biến thể).
     */
    boolean existsByMaSanPhamIdAndMaMauSacIdAndMaKichThuocId(Long maSanPhamId, Long maMauSacId, Long maKichThuocId);

    /**
     * Kiểm tra cặp (Sản phẩm + Màu sắc + Kích thước) đã tồn tại hay chưa (loại trừ chính ID đang cập nhật).
     */
    boolean existsByMaSanPhamIdAndMaMauSacIdAndMaKichThuocIdAndIdNot(
            Long maSanPhamId, Long maMauSacId, Long maKichThuocId, Long id);

    /**
     * Đếm số biến thể sắp hết hàng hoặc hết hàng (tồn kho <= ngưỡng).
     */
    long countBySoLuongLessThanEqual(Integer soLuong);

    /**
     * Trừ số lượng tồn kho nguyên tử (Atomic / Concurrency-safe) (US21 - Tiêu chí 2).
     * Chỉ trừ khi tồn kho hiện tại >= số lượng cần trừ, chống bán quá số lượng (Over-selling).
     *
     * @return 1 nếu trừ thành công, 0 nếu không đủ tồn kho hoặc không tìm thấy biến thể.
     */
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @Query("UPDATE BienTheSanPham bt SET bt.soLuong = bt.soLuong - :soLuongTru " +
           "WHERE bt.id = :id AND bt.soLuong >= :soLuongTru")
    int truTonKhoAtomic(@Param("id") Long id, @Param("soLuongTru") Integer soLuongTru);

    /**
     * Hoàn lại số lượng tồn kho (khi đơn hàng bị hủy) (US21).
     */
    @org.springframework.data.jpa.repository.Modifying(clearAutomatically = true)
    @Query("UPDATE BienTheSanPham bt SET bt.soLuong = bt.soLuong + :soLuongHoan " +
           "WHERE bt.id = :id")
    int hoanTonKhoAtomic(@Param("id") Long id, @Param("soLuongHoan") Integer soLuongHoan);

    /**
     * Tính tổng số lượng tồn kho của toàn bộ các SKU trên hệ thống.
     */
    @Query("SELECT COALESCE(SUM(bt.soLuong), 0) FROM BienTheSanPham bt")
    long tinhTongTonKho();

    /**
     * Lấy danh sách các biến thể sắp hết hàng hoặc hết hàng (tồn kho <= ngưỡng) cho Dashboard và cảnh báo nhập hàng.
     */
    @Query("SELECT bt FROM BienTheSanPham bt WHERE bt.kichHoat = true AND bt.soLuong <= :nguong ORDER BY bt.soLuong ASC")
    List<BienTheSanPham> layDanhSachSapHetHang(@Param("nguong") Integer nguong, Pageable pageable);

    /**
     * Đếm số biến thể sắp hết hàng của một sản phẩm cụ thể.
     */
    @Query("SELECT COUNT(bt) FROM BienTheSanPham bt WHERE bt.maSanPhamId = :maSanPhamId AND bt.soLuong <= :nguong")
    long demBienTheSapHetHangTheoSanPham(@Param("maSanPhamId") Long maSanPhamId, @Param("nguong") Integer nguong);

    /**
     * Tính tổng số lượng tồn kho của một sản phẩm cụ thể (tổng tất cả biến thể).
     */
    @Query("SELECT COALESCE(SUM(bt.soLuong), 0) FROM BienTheSanPham bt WHERE bt.maSanPhamId = :maSanPhamId")
    long tinhTongTonKhoTheoSanPham(@Param("maSanPhamId") Long maSanPhamId);

    /**
     * Tìm biến thể bằng cách quét mã vạch Barcode hoặc mã SKU (Phục vụ máy quét mã vạch khi nhập hàng).
     */
    @Query("SELECT bt FROM BienTheSanPham bt WHERE " +
           "LOWER(bt.maSku) = LOWER(:maQuet) OR " +
           "(bt.maVachBarcode IS NOT NULL AND LOWER(bt.maVachBarcode) = LOWER(:maQuet))")
    List<BienTheSanPham> timKiemTheoMaQuet(@Param("maQuet") String maQuet);

    /**
     * Tìm kiếm nhanh biến thể sản phẩm cho màn hình bán hàng tại quầy (POS) theo SKU hoặc Barcode.
     */
    @Query("SELECT bt FROM BienTheSanPham bt WHERE " +
           "bt.kichHoat = true AND " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(bt.maSku) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " (bt.maVachBarcode IS NOT NULL AND LOWER(bt.maVachBarcode) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))) " +
           "ORDER BY bt.id DESC")
    List<BienTheSanPham> timKiemBienTheChoPos(@Param("tuKhoa") String tuKhoa, Pageable pageable);
}
