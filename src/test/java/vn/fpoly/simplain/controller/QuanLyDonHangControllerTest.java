package vn.fpoly.simplain.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.*;
import vn.fpoly.simplain.repository.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Kiểm thử tự động Web MVC cho Quản lý trạng thái vận đơn & Ghi log lịch sử (US22 - Mod 5 - Hưng).
 */
@SpringBootTest
@AutoConfigureMockMvc
@Transactional
@DisplayName("Kiểm thử Web MVC Quản lý trạng thái vận đơn & Ghi log lịch sử (US22)")
class QuanLyDonHangControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    @Autowired
    private PhuongThucThanhToanRepository phuongThucThanhToanRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Autowired
    private NhanVienRepository nhanVienRepository;

    @Autowired
    private VaiTroRepository vaiTroRepository;

    private DonHang donHangMau;
    private TrangThaiDonHang ttChoXacNhan;
    private TrangThaiDonHang ttDangGiao;
    private TrangThaiDonHang ttHoanThanh;
    private NhanVien adminActor;

    @BeforeEach
    void setUp() {
        ttChoXacNhan = trangThaiDonHangRepository.findByMaTrangThai("CHO_XAC_NHAN")
                .orElseGet(() -> {
                    TrangThaiDonHang tt = new TrangThaiDonHang();
                    tt.setMaTrangThai("CHO_XAC_NHAN");
                    tt.setTenTrangThai("Chờ xác nhận");
                    return trangThaiDonHangRepository.save(tt);
                });

        ttDangGiao = trangThaiDonHangRepository.findByMaTrangThai("DANG_GIAO")
                .orElseGet(() -> {
                    TrangThaiDonHang tt = new TrangThaiDonHang();
                    tt.setMaTrangThai("DANG_GIAO");
                    tt.setTenTrangThai("Đang giao hàng");
                    return trangThaiDonHangRepository.save(tt);
                });

        ttHoanThanh = trangThaiDonHangRepository.findByMaTrangThai("HOAN_THANH")
                .orElseGet(() -> {
                    TrangThaiDonHang tt = new TrangThaiDonHang();
                    tt.setMaTrangThai("HOAN_THANH");
                    tt.setTenTrangThai("Hoàn thành");
                    return trangThaiDonHangRepository.save(tt);
                });

        VaiTro vaiTro = vaiTroRepository.findByMaVaiTro("ADMIN")
                .orElseGet(() -> {
                    VaiTro vt = new VaiTro();
                    vt.setMaVaiTro("ADMIN");
                    vt.setTenVaiTro("Quản trị viên");
                    vt.setTrangThai(1);
                    return vaiTroRepository.save(vt);
                });

        adminActor = nhanVienRepository.findAll().stream().findFirst().orElseGet(() -> {
            NhanVien nv = new NhanVien();
            nv.setMaNhanVien("NV-TEST-01");
            nv.setTenDangNhap("admin_test");
            nv.setMatKhauHash("secret");
            nv.setHoTen("Admin Test");
            nv.setEmail("admintest@simplain.vn");
            nv.setVaiTro(vaiTro);
            nv.setTrangThai(1);
            return nhanVienRepository.save(nv);
        });

        KhachHang khachHang = khachHangRepository.findAll().stream().findFirst().orElseGet(() -> {
            KhachHang kh = new KhachHang();
            kh.setMaKhachHang("KH-TEST-01");
            kh.setHoTen("Khách hàng Test");
            kh.setEmail("khtest@gmail.com");
            kh.setSoDienThoai("0987654321");
            kh.setTrangThai(1);
            return khachHangRepository.save(kh);
        });

        PhuongThucThanhToan pttt = phuongThucThanhToanRepository.findAll().stream().findFirst().orElseGet(() -> {
            PhuongThucThanhToan pt = new PhuongThucThanhToan();
            pt.setMaPhuongThuc("COD");
            pt.setTenPhuongThuc("Thanh toán khi nhận hàng");
            pt.setKichHoat(true);
            return phuongThucThanhToanRepository.save(pt);
        });

        BienTheSanPham sku = bienTheSanPhamRepository.findAll().stream().findFirst().orElseGet(() -> {
            BienTheSanPham b = new BienTheSanPham();
            b.setMaSku("SKU-TEST-01");
            b.setGiaBan(BigDecimal.valueOf(250000));
            b.setSoLuong(50);
            b.setKichHoat(true);
            return bienTheSanPhamRepository.save(b);
        });

        donHangMau = new DonHang();
        donHangMau.setMaDonHang("DH-QLDH-" + System.currentTimeMillis());
        donHangMau.setKhachHang(khachHang);
        donHangMau.setPhuongThucThanhToan(pttt);
        donHangMau.setTrangThaiDonHang(ttChoXacNhan);
        donHangMau.setTongTienHang(BigDecimal.valueOf(500000));
        donHangMau.setTongThanhToan(BigDecimal.valueOf(500000));
        donHangMau.setNgayTao(LocalDateTime.now());
        donHangMau.setTenNguoiNhan("Người nhận Test");
        donHangMau.setSoDienThoaiNhan("0912345678");
        donHangMau.setDiaChiGiaoHang("Hà Nội");
        donHangMau = donHangRepository.save(donHangMau);

        ChiTietDonHang ct = new ChiTietDonHang();
        ct.setMaDonHangId(donHangMau.getId());
        ct.setMaBienTheId(sku.getId() != null ? sku.getId() : 1L);
        ct.setTenSanPham("Áo Thun Test");
        ct.setMaSku(sku.getMaSku());
        ct.setSoLuong(2);
        ct.setDonGia(BigDecimal.valueOf(250000));
        ct.setThanhTien(BigDecimal.valueOf(500000));
        chiTietDonHangRepository.save(ct);
    }

    @Test
    @DisplayName("US22 - Màn hình danh sách đơn hàng Admin hiển thị thành công")
    void testHienThiDanhSach() throws Exception {
        mockMvc.perform(get("/admin/don-hang/hien-thi"))
                .andExpect(status().isOk())
                .andExpect(view().name("admin/don-hang/hien-thi"))
                .andExpect(model().attributeExists("trangDonHang"))
                .andExpect(model().attributeExists("dsTrangThai"))
                .andExpect(model().attributeExists("tongDonHang"));
    }

    @Test
    @DisplayName("US22 - API lấy chi tiết các mặt hàng của đơn hàng thành công")
    void testLayChiTietDonHang() throws Exception {
        mockMvc.perform(get("/admin/don-hang/api/chi-tiet/" + donHangMau.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$[0].tenSanPham", notNullValue()));
    }

    @Test
    @DisplayName("US22 - Cập nhật trạng thái vận đơn qua Form MVC và ghi log lịch sử")
    void testCapNhatTrangThaiForm() throws Exception {
        mockMvc.perform(post("/admin/don-hang/cap-nhat-trang-thai/" + donHangMau.getId())
                        .param("maTrangThai", "DANG_GIAO")
                        .param("maVanDon", "VD-VNPOST-123456")
                        .param("ghiChu", "Đã bàn giao cho bưu tá giao hàng"))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl("/admin/don-hang/hien-thi"))
                .andExpect(flash().attributeExists("thongBaoThanhCong"));

        DonHang dh = donHangRepository.findById(donHangMau.getId()).orElseThrow();
        assertEquals("DANG_GIAO", dh.getTrangThaiDonHang().getMaTrangThai());
        assertEquals("VD-VNPOST-123456", dh.getMaVanDon());

        var lichSu = lichSuTrangThaiDonHangRepository.findByDonHangIdOrderByNgayChuyenDesc(dh.getId());
        assertFalse(lichSu.isEmpty());
        assertEquals("DANG_GIAO", lichSu.get(0).getTrangThaiMoi().getMaTrangThai());
    }

    @Test
    @DisplayName("US22 - API AJAX lấy lịch sử trạng thái vận chuyển")
    void testLayLichSuDonHang() throws Exception {
        mockMvc.perform(get("/admin/don-hang/api/lich-su/" + donHangMau.getId()))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
    }
}
