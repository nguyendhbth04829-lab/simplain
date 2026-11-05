package vn.fpoly.simplain.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@org.springframework.transaction.annotation.Transactional
@DisplayName("Kiểm thử Deterministic Inbound Pipeline")
class InboundPipelineTest {

    @Autowired
    private MockMvc mockMvc;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private NhatKyKhoRepository nhatKyKhoRepository;

    @Autowired
    private SanPhamRepository sanPhamRepository;

    @Autowired
    private PhanBoTonKhoService phanBoTonKhoService;

    @Test
    @DisplayName("Thực hiện nhập kho thành công: Tồn kho tăng từ 0 lên 50 và ghi sổ cái")
    void testNhapKhoThanhCong() throws Exception {
        String skuTarget = "SP-POLO-EXCOOL-01-DEN-M";
        BienTheSanPham bienThe = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy SKU: " + skuTarget));

        int tonTruoc = bienThe.getSoLuong();
        System.out.println("========== [TRƯỚC KHI GỌI ENDPOINT] ==========");
        System.out.println("SKU: " + skuTarget);
        System.out.println("Tồn kho hiện tại: " + tonTruoc);

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-HUNGPAT")
                .ghiChu("Kiện hàng mẫu từ Xưởng Hưng Phát")
                .danhSachHang(List.of(
                        NhapKhoRequest.ChiTietNhapItem.builder()
                                .maSku(skuTarget)
                                .soLuongNhap(50)
                                .donGiaNhap(new BigDecimal("85000"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/inbound/nhap-kho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trangThai").value("THANH_CONG"))
                .andExpect(jsonPath("$.chiTiet[0].maSku").value(skuTarget))
                .andExpect(jsonPath("$.chiTiet[0].soLuongNhap").value(50))
                .andExpect(jsonPath("$.chiTiet[0].tonSau").value(tonTruoc + 50));

        BienTheSanPham sauKhiNhap = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget).orElseThrow();
        System.out.println("========== [SAU KHI GỌI ENDPOINT THÀNH CÔNG] ==========");
        System.out.println("SKU: " + skuTarget);
        System.out.println("Tồn kho mới trong DB: " + sauKhiNhap.getSoLuong());
        System.out.println("Giá gốc vốn mới: " + sauKhiNhap.getGiaGoc());

        assertEquals(tonTruoc + 50, sauKhiNhap.getSoLuong());
        assertFalse(nhatKyKhoRepository.findByMaBienTheIdOrderByNgayTaoDesc(sauKhiNhap.getId()).isEmpty());
        System.out.println("Sổ cái nhat_ky_kho đã ghi nhận giao dịch thành công!");
    }

    @Test
    @DisplayName("Nhập kho xong: Kệ Offline và Web Online thấy cùng một lượng tồn mới")
    void nhapKhoXongPhanBoNhatQuanHaiKenh() throws Exception {
        String skuTarget = "SP-POLO-EXCOOL-01-DEN-M";
        BienTheSanPham truocNhap = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget).orElseThrow();
        int tonTruoc = truocNhap.getSoLuong();
        int soLuongNhap = 7;

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-HUNGPAT")
                .ghiChu("Kiểm thử phân bổ hai kênh sau nhập kho")
                .danhSachHang(List.of(
                        NhapKhoRequest.ChiTietNhapItem.builder()
                                .maSku(skuTarget)
                                .soLuongNhap(soLuongNhap)
                                .donGiaNhap(new BigDecimal("85000"))
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/inbound/nhap-kho")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trangThai").value("THANH_CONG"));

        BienTheSanPham sauNhap = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget).orElseThrow();
        SanPham sanPham = sanPhamRepository.findById(sauNhap.getMaSanPhamId()).orElseThrow();
        PhanBoTonKho phanBo = phanBoTonKhoService.phanBo(sauNhap, Boolean.TRUE.equals(sanPham.getKichHoat()));

        int tonMongDoi = tonTruoc + soLuongNhap;
        assertEquals(tonMongDoi, sauNhap.getSoLuong());
        assertEquals(tonMongDoi, phanBo.tonKhoVatLy());
        assertEquals(tonMongDoi, phanBo.soLuongKeOffline());
        assertEquals(tonMongDoi, phanBo.soLuongWebOnline());
        assertEquals(PhanBoTonKho.NGUON_DUNG_CHUNG, phanBo.nguonTonKho());
    }

    @Test
    @DisplayName("Trang nhập kho render đúng trạng thái enum và không còn nhãn bước trên giao diện")
    void trangNhapKhoKhongConNhanBuoc() throws Exception {
        mockMvc.perform(get("/admin/lay-hang"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("NHẬP HÀNG VÀO KHO")))
                .andExpect(content().string(containsString("Nhà cung cấp")))
                .andExpect(content().string(not(containsString("Bước 1"))))
                .andExpect(content().string(not(containsString("BƯỚC 1"))));
    }

    @Test
    @DisplayName("Trang nhà cung cấp dẫn sang phân hệ nhập kho bằng nhãn nghiệp vụ")
    void trangNhaCungCapKhongConNhanBuoc() throws Exception {
        mockMvc.perform(get("/admin/nha-cung-cap"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("page-header")))
                .andExpect(content().string(containsString("page-title")))
                .andExpect(content().string(containsString("Nhà cung cấp")))
                .andExpect(content().string(containsString("workspace")))
                .andExpect(content().string(containsString("supplier-grid")))
                .andExpect(content().string(containsString("supplier-card")))
                .andExpect(content().string(containsString("detail-panel")))
                .andExpect(content().string(containsString("drawer-overlay")))
                .andExpect(content().string(containsString("btn-primary-lg")))
                .andExpect(content().string(not(containsString("command-bar"))))
                .andExpect(content().string(not(containsString("Đến bước 2"))))
                .andExpect(content().string(not(containsString("topbar-right"))));
    }
}
