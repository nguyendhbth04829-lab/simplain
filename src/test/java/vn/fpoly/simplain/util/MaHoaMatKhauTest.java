/**
 * Tác giả: Gia Minh
 * Chức năng: US12 - Kiểm thử tiện ích băm mật khẩu
 */
package vn.fpoly.simplain.util;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Kiểm thử tiện ích băm mật khẩu MaHoaMatKhau")
class MaHoaMatKhauTest {

    private MaHoaMatKhau maHoaMatKhau;

    @BeforeEach
    void khoiTao() {
        maHoaMatKhau = new MaHoaMatKhau();
    }

    @Test
    @DisplayName("maHoa() tạo chuỗi băm hex SHA-256 đúng 64 ký tự")
    void maHoa_TaoChuoiBamDungDinhDang() {
        String matKhauTho = "MatKhau123@";
        String chuoiBam = maHoaMatKhau.maHoa(matKhauTho);

        assertNotNull(chuoiBam);
        assertEquals(64, chuoiBam.length());
        assertTrue(chuoiBam.matches("^[a-f0-9]{64}$"));
    }

    @Test
    @DisplayName("kiemTra() trả về true khi mật khẩu thô khớp với chuỗi băm")
    void kiemTra_TraVeTrueKhiMatKhauDung() {
        String matKhauTho = "MatKhauBiMat999@";
        String chuoiBam = maHoaMatKhau.maHoa(matKhauTho);

        assertTrue(maHoaMatKhau.kiemTra(matKhauTho, chuoiBam));
    }

    @Test
    @DisplayName("kiemTra() trả về false khi mật khẩu sai")
    void kiemTra_TraVeFalseKhiMatKhauSai() {
        String matKhauDung = "MatKhauBiMat999@";
        String matKhauSai = "MatKhauSaiRoi!";
        String chuoiBam = maHoaMatKhau.maHoa(matKhauDung);

        assertFalse(maHoaMatKhau.kiemTra(matKhauSai, chuoiBam));
    }

    @Test
    @DisplayName("kiemTra() trả về false khi truyền tham số null")
    void kiemTra_TraVeFalseKhiThamSoNull() {
        assertFalse(maHoaMatKhau.kiemTra(null, "hash"));
        assertFalse(maHoaMatKhau.kiemTra("pass", null));
    }
}
