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

    /**
     * Tìm kiếm phân trang biến thể SKU theo từ khóa (mã SKU, mã vạch barcode),
     * lọc theo sản phẩm gốc, màu sắc, kích thước và trạng thái kích hoạt.
     */
    @Query("SELECT bt FROM BienTheSanPham bt WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(bt.maSku) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " (bt.maVachBarcode IS NOT NULL AND LOWER(bt.maVachBarcode) LIKE LOWER(CONCAT('%', :tuKhoa, '%')))) AND " +
           "(:maSanPhamId IS NULL OR bt.maSanPhamId = :maSanPhamId) AND " +
           "(:maMauSacId IS NULL OR bt.maMauSacId = :maMauSacId) AND " +
           "(:maKichThuocId IS NULL OR bt.maKichThuocId = :maKichThuocId) AND " +
           "(:kichHoat IS NULL OR bt.kichHoat = :kichHoat)")
    Page<BienTheSanPham> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                          @Param("maSanPhamId") Long maSanPhamId,
                                          @Param("maMauSacId") Long maMauSacId,
                                          @Param("maKichThuocId") Long maKichThuocId,
                                          @Param("kichHoat") Boolean kichHoat,
                                          Pageable pageable);

    /**
     * Lấy danh sách tất cả biến thể của một sản phẩm.
     */
    List<BienTheSanPham> findByMaSanPhamIdOrderByIdDesc(Long maSanPhamId);

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
