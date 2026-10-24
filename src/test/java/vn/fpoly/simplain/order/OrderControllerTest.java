package vn.fpoly.simplain.order;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.LichSuTrangThaiDonHang;
import vn.fpoly.simplain.order.dto.CreateOrderItemRequest;
import vn.fpoly.simplain.order.dto.CreateOrderRequest;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.LichSuTrangThaiDonHangRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("Kiểm thử Module 5 - US21: Tạo mới Đơn hàng và Trừ tồn kho")
class OrderControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    private BienTheSanPham bienTheMau;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        // Xóa sạch dữ liệu liên quan trước mỗi test
        chiTietDonHangRepository.deleteAll();
        lichSuTrangThaiDonHangRepository.deleteAll();
        donHangRepository.deleteAll();
        bienTheSanPhamRepository.deleteAll();

        // Tạo sẵn biến thể sản phẩm mẫu với tồn kho ban đầu = 10
        bienTheMau = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSku("AO-POLO-DEN-M")
                .soLuong(10)
                .giaBan(BigDecimal.valueOf(250000))
                .giaGoc(BigDecimal.valueOf(180000))
                .kichHoat(true)
                .build());
    }

    @Test
    @DisplayName("US21: Tạo đơn hàng thành công và tự động trừ tồn kho thật")
    void testCreateOrder_Success_AndStockDeducted() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .tenNguoiNhan("Nguyễn Văn Hưng")
                .soDienThoaiNhan("0912345678")
                .diaChiGiaoHang("Số 45 Trịnh Văn Bô, Nam Từ Liêm, Hà Nội")
                .ghiChu("Giao sau 5h chiều")
                .tongTienHang(BigDecimal.valueOf(500000))
                .tienGiamGia(BigDecimal.valueOf(50000))
                .phiVanChuyen(BigDecimal.valueOf(30000))
                .tongThanhToan(BigDecimal.valueOf(480000))
                .items(List.of(
                        CreateOrderItemRequest.builder()
                                .bienTheId(bienTheMau.getId())
                                .maSku("AO-POLO-DEN-M")
                                .tenSanPham("Áo Polo Đen Form Vừa")
                                .donGia(BigDecimal.valueOf(250000))
                                .soLuong(2)
                                .build()
                ))
                .build();

        String responseContent = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maDonHang").exists())
                .andExpect(jsonPath("$.maTrangThai").value("CHO_XAC_NHAN"))
                .andExpect(jsonPath("$.tenNguoiNhan").value("Nguyễn Văn Hưng"))
                .andExpect(jsonPath("$.tongThanhToan").value(480000))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].soLuong").value(2))
                .andReturn()
                .getResponse()
                .getContentAsString();

        // Kiểm tra tồn kho sau khi tạo đơn hàng: 10 - 2 = 8
        BienTheSanPham bienTheSauKhiMua = bienTheSanPhamRepository.findById(bienTheMau.getId()).orElseThrow();
        assertThat(bienTheSauKhiMua.getSoLuong()).isEqualTo(8);

        // Kiểm tra đơn hàng trong DB
        List<DonHang> danhSachDon = donHangRepository.findAll();
        assertThat(danhSachDon).hasSize(1);
        DonHang donHang = danhSachDon.get(0);
        assertThat(donHang.getTenNguoiNhan()).isEqualTo("Nguyễn Văn Hưng");

        // Kiểm tra lịch sử trạng thái được ghi tự động
        List<LichSuTrangThaiDonHang> lichSuList = lichSuTrangThaiDonHangRepository.findByDonHangIdOrderByNgayChuyenDesc(donHang.getId());
        assertThat(lichSuList).isNotEmpty();
        assertThat(lichSuList.get(0).getTrangThaiMoi().getMaTrangThai()).isEqualTo("CHO_XAC_NHAN");
    }

    @Test
    @DisplayName("US21: Báo lỗi và rollback khi số lượng mua vượt quá tồn kho thực tế")
    void testCreateOrder_Fail_WhenInsufficientStock() throws Exception {
        CreateOrderRequest request = CreateOrderRequest.builder()
                .tenNguoiNhan("Khách Hàng Mua Nhiều")
                .soDienThoaiNhan("0988888888")
                .diaChiGiaoHang("123 Cầu Giấy, Hà Nội")
                .tongTienHang(BigDecimal.valueOf(3750000))
                .tongThanhToan(BigDecimal.valueOf(3750000))
                .items(List.of(
                        CreateOrderItemRequest.builder()
                                .bienTheId(bienTheMau.getId())
                                .maSku("AO-POLO-DEN-M")
                                .tenSanPham("Áo Polo Đen Form Vừa")
                                .donGia(BigDecimal.valueOf(250000))
                                .soLuong(15) // Yêu cầu 15 trong khi kho chỉ có 10
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());

        // Kiểm tra tồn kho vẫn nguyên vẹn = 10, không bị trừ dở dang
        BienTheSanPham bienTheKhongDoi = bienTheSanPhamRepository.findById(bienTheMau.getId()).orElseThrow();
        assertThat(bienTheKhongDoi.getSoLuong()).isEqualTo(10);

        // Không có đơn hàng nào được tạo
        assertThat(donHangRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("US21: Báo lỗi 400 Bad Request khi dữ liệu đầu vào thiếu trường bắt buộc")
    void testCreateOrder_Fail_WhenValidationFails() throws Exception {
        // Thiếu tên người nhận, SĐT, địa chỉ và danh sách items rỗng
        CreateOrderRequest invalidRequest = CreateOrderRequest.builder()
                .tongTienHang(BigDecimal.valueOf(100000))
                .tongThanhToan(BigDecimal.valueOf(100000))
                .items(List.of())
                .build();

        mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("US23: Tra cứu thông tin đơn hàng theo mã đơn hàng")
    void testGetOrderByCode_Success() throws Exception {
        // Tạo đơn hàng trước
        CreateOrderRequest request = CreateOrderRequest.builder()
                .tenNguoiNhan("Trần Thị Mai")
                .soDienThoaiNhan("0977112233")
                .diaChiGiaoHang("Hà Đông, Hà Nội")
                .tongTienHang(BigDecimal.valueOf(250000))
                .tongThanhToan(BigDecimal.valueOf(250000))
                .items(List.of(
                        CreateOrderItemRequest.builder()
                                .bienTheId(bienTheMau.getId())
                                .maSku("AO-POLO-DEN-M")
                                .tenSanPham("Áo Polo Đen Form Vừa")
                                .donGia(BigDecimal.valueOf(250000))
                                .soLuong(1)
                                .build()
                ))
                .build();

        String res = mockMvc.perform(post("/api/orders")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String maDonHang = objectMapper.readTree(res).get("maDonHang").asText();

        // Gọi GET theo mã đơn hàng
        mockMvc.perform(get("/api/orders/{maDonHang}", maDonHang))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.maDonHang").value(maDonHang))
                .andExpect(jsonPath("$.tenNguoiNhan").value("Trần Thị Mai"))
                .andExpect(jsonPath("$.items[0].maSku").value("AO-POLO-DEN-M"));
    }
}
