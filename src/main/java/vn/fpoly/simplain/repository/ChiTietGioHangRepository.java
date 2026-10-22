package vn.fpoly.simplain.repository;

import vn.fpoly.simplain.entity.ChiTietGioHang;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Truy vấn dòng hàng trong giỏ.
 */
@Repository
public interface ChiTietGioHangRepository extends JpaRepository<ChiTietGioHang, Long> {
    List<ChiTietGioHang> findByMaGioHangIdOrderByNgayThemDesc(Long maGioHangId);

    List<ChiTietGioHang> findByMaGioHangIdAndNhomComboToken(Long maGioHangId, String nhomComboToken);

    void deleteByMaGioHangIdAndNhomComboToken(Long maGioHangId, String nhomComboToken);

    /**
     * Tìm dòng trùng để cộng dồn: cùng biến thể + cùng combo + cùng token.
     * So sánh null an toàn ở tầng Service.
     */
    List<ChiTietGioHang> findByMaGioHangIdAndMaBienTheId(Long maGioHangId, Long maBienTheId);

    /** Lấy thông tin thật của các biến thể đang có trong giỏ. */
    @Query(value = """
            SELECT bt.id AS maBienTheId,
                   sp.ten_san_pham AS tenSanPham,
                   ms.ten_mau AS tenMau,
                   kt.ten_kich_thuoc AS tenKichThuoc,
                   bt.gia_ban AS giaBan,
                   bt.gia_goc AS giaGoc,
                   anh.duong_dan_anh AS hinhAnhUrl
            FROM bien_the_san_pham bt
            JOIN san_pham sp ON sp.id = bt.ma_san_pham_id
            LEFT JOIN mau_sac ms ON ms.id = bt.ma_mau_sac_id
            LEFT JOIN kich_thuoc kt ON kt.id = bt.ma_kich_thuoc_id
            OUTER APPLY (
                SELECT TOP 1 ha.duong_dan_anh
                FROM hinh_anh ha
                WHERE ha.ma_bien_the_id = bt.id
                   OR (ha.ma_san_pham_id = sp.id AND ha.ma_bien_the_id IS NULL)
                ORDER BY CASE WHEN ha.ma_bien_the_id = bt.id THEN 0 ELSE 1 END,
                         ha.la_anh_dai_dien DESC, ha.thu_tu ASC
            ) anh
            WHERE bt.id IN (:maBienTheIds)
            """, nativeQuery = true)
    List<ThongTinSanPhamGioHang> timThongTinSanPhamTrongGio(@Param("maBienTheIds") List<Long> maBienTheIds);
}
