package vn.fpoly.simplain.repository;

import vn.fpoly.simplain.dto.VoucherCheckoutItem;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class VoucherCheckoutRepository {
    private static final String SQL_TIM_VOUCHER_HOP_LE = """
            SELECT v.id AS id,
                   v.ma_code AS maCode,
                   v.loai_giam_gia AS loaiGiamGia,
                   v.gia_tri_giam AS giaTriGiam,
                   v.giam_toi_da AS giamToiDa,
                   v.gia_tri_don_toi_thieu AS giaTriDonToiThieu
            FROM ma_giam_gia v
            JOIN voucher_so_huu vs ON vs.ma_giam_gia_id = v.id
            WHERE vs.ma_khach_hang_id = ?
              AND UPPER(v.ma_code) = UPPER(?)
              AND vs.da_su_dung = 0
              AND v.kich_hoat = 1
              AND v.ngay_bat_dau <= ?
              AND v.ngay_ket_thuc >= ?
              AND (v.tong_so_luong IS NULL OR v.so_luong_da_dung < v.tong_so_luong)
            """;

    private final JdbcTemplate jdbcTemplate;

    public VoucherCheckoutRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<VoucherCheckoutInfo> timVoucherHopLe(Long maKhachHangId, String maCode, LocalDateTime thoiGian) {
        return jdbcTemplate.query(SQL_TIM_VOUCHER_HOP_LE, (resultSet, rowNum) -> new VoucherCheckoutInfo(
                        resultSet.getLong("id"),
                        resultSet.getString("maCode"),
                        resultSet.getString("loaiGiamGia"),
                        resultSet.getBigDecimal("giaTriGiam"),
                        resultSet.getBigDecimal("giamToiDa"),
                        resultSet.getBigDecimal("giaTriDonToiThieu")),
                maKhachHangId, maCode, thoiGian, thoiGian).stream().findFirst();
    }

    public List<VoucherCheckoutItem> layVoucherChuaDung(Long maKhachHangId, LocalDateTime thoiGian) {
        String sql = """
                SELECT v.ma_code AS maCode,
                       v.ten_voucher AS tenVoucher,
                       v.loai_giam_gia AS loaiGiamGia,
                       v.gia_tri_giam AS giaTriGiam,
                       v.giam_toi_da AS giamToiDa,
                       v.gia_tri_don_toi_thieu AS giaTriDonToiThieu,
                       v.ngay_ket_thuc AS ngayKetThuc
                FROM voucher_so_huu vs
                JOIN ma_giam_gia v ON v.id = vs.ma_giam_gia_id
                WHERE vs.ma_khach_hang_id = ?
                  AND vs.da_su_dung = 0
                  AND v.kich_hoat = 1
                  AND v.ngay_bat_dau <= ?
                  AND v.ngay_ket_thuc >= ?
                  AND (v.tong_so_luong IS NULL OR v.so_luong_da_dung < v.tong_so_luong)
                ORDER BY v.ngay_ket_thuc ASC, v.ma_code ASC
                """;
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> new VoucherCheckoutItem(
                        resultSet.getString("maCode"),
                        resultSet.getString("tenVoucher"),
                        resultSet.getString("loaiGiamGia"),
                        resultSet.getBigDecimal("giaTriGiam"),
                        resultSet.getBigDecimal("giamToiDa"),
                        resultSet.getBigDecimal("giaTriDonToiThieu"),
                        resultSet.getTimestamp("ngayKetThuc").toLocalDateTime()),
                maKhachHangId, thoiGian, thoiGian);
    }
}
