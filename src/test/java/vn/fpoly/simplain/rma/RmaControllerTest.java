package vn.fpoly.simplain.rma;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.TrangThaiDonHang;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.ChiTietSanPhamDoiTraRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.LichSuTrangThaiDonHangRepository;
import vn.fpoly.simplain.repository.TrangThaiDonHangRepository;
import vn.fpoly.simplain.repository.YeuCauDoiTraRepository;
import vn.fpoly.simplain.rma.dto.CreateRmaItemRequest;
import vn.fpoly.simplain.rma.dto.CreateRmaRequest;
import vn.fpoly.simplain.rma.dto.ReviewRmaRequest;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@DisplayName("Kiểm thử Module 5 - US24 & US25: Quản lý Yêu cầu Đổi/Trả hàng (RMA)")
class RmaControllerTest {

    private MockMvc mockMvc;

    @Autowired
    private WebApplicationContext webApplicationContext;

    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Autowired
    private ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository;

    @Autowired
    private YeuCauDoiTraRepository yeuCauDoiTraRepository;

    @Autowired
    private ChiTietDonHangRepository chiTietDonHangRepository;

    @Autowired
    private LichSuTrangThaiDonHangRepository lichSuTrangThaiDonHangRepository;

    @Autowired
    private DonHangRepository donHangRepository;

    @Autowired
    private KhachHangRepository khachHangRepository;

    @Autowired
    private BienTheSanPhamRepository bienTheSanPhamRepository;

    @Autowired
    private TrangThaiDonHangRepository trangThaiDonHangRepository;

    private KhachHang khachHangMau;
    private DonHang donHangMau;
    private ChiTietDonHang chiTietMau;

    @BeforeEach
    void setUp() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(webApplicationContext).build();

        chiTietSanPhamDoiTraRepository.deleteAll();
        yeuCauDoiTraRepository.deleteAll();
        chiTietDonHangRepository.deleteAll();
        lichSuTrangThaiDonHangRepository.deleteAll();
        donHangRepository.deleteAll();
        khachHangRepository.deleteAll();
        bienTheSanPhamRepository.deleteAll();

        // 1. Tạo Khách hàng
        khachHangMau = khachHangRepository.save(KhachHang.builder()
                .maKhachHang("KH-RMA-01")
                .hoTen("Trần Hùng RMA")
                .soDienThoai("0988776655")
                .email("rma_test@example.com")
                .build());

        // 2. Tạo Biến thể sản phẩm
        BienTheSanPham bienThe = bienTheSanPhamRepository.save(BienTheSanPham.builder()
                .maSku("AO-HOODIE-XAM-L")
                .soLuong(20)
                .giaBan(BigDecimal.valueOf(350000))
                .kichHoat(true)
                .build());

        // 3. Tạo Trạng thái đơn hàng
        TrangThaiDonHang trangThai = trangThaiDonHangRepository.findByMaTrangThai("DA_GIAO")
                .orElseGet(() -> trangThaiDonHangRepository.save(TrangThaiDonHang.builder()
                        .maTrangThai("DA_GIAO")
                        .tenTrangThai("Đã giao hàng")
                        .maMauHex("#10B981")
                        .build()));

        // 4. Tạo Đơn hàng đã giao
        donHangMau = donHangRepository.save(DonHang.builder()
                .maDonHang("DH-RMA-TEST")
                .khachHang(khachHangMau)
                .trangThai(trangThai)
                .tenNguoiNhan("Trần Hùng RMA")
                .soDienThoaiNhan("0988776655")
                .diaChiGiaoHang("Hà Nội")
                .tongTienHang(BigDecimal.valueOf(350000))
                .tongThanhToan(BigDecimal.valueOf(350000))
                .build());

        // 5. Tạo Chi tiết đơn hàng
        chiTietMau = chiTietDonHangRepository.save(ChiTietDonHang.builder()
                .donHang(donHangMau)
                .bienThe(bienThe)
                .tenSanPham("Áo Hoodie Nỉ Xám")
                .maSku("AO-HOODIE-XAM-L")
                .donGia(BigDecimal.valueOf(350000))
                .soLuong(1)
                .thanhTien(BigDecimal.valueOf(350000))
                .build());
    }

    @Test
    @DisplayName("US24: Khách hàng tạo yêu cầu Đổi Hàng online (DOI_HANG) thành công")
    void testCreateRma_Success_DoiHang() throws Exception {
        CreateRmaRequest request = CreateRmaRequest.builder()
                .donHangId(donHangMau.getId())
                .khachHangId(khachHangMau.getId())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Áo bị chật, muốn đổi size XL")
                .items(List.of(
                        CreateRmaItemRequest.builder()
                                .chiTietDonHangId(chiTietMau.getId())
                                .soLuongTra(1)
                                .maBienTheMoiId(99L)
                                .tinhTrangSanPham("Còn nguyên tem mác, chỉ bị chật vai")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/rma")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.maYeuCauRma").exists())
                .andExpect(jsonPath("$.loaiDoiTra").value("DOI_HANG"))
                .andExpect(jsonPath("$.trangThai").value("CHO_DUYET"))
                .andExpect(jsonPath("$.items").isArray())
                .andExpect(jsonPath("$.items[0].tinhTrangSanPham").value("Còn nguyên tem mác, chỉ bị chật vai"));

        assertThat(yeuCauDoiTraRepository.count()).isEqualTo(1);
        assertThat(chiTietSanPhamDoiTraRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("US24: Khách hàng tạo yêu cầu Trả Hàng Hoàn Tiền (TRA_HANG_HOAN_TIEN) thành công")
    void testCreateRma_Success_HoanTien() throws Exception {
        CreateRmaRequest request = CreateRmaRequest.builder()
                .donHangId(donHangMau.getId())
                .khachHangId(khachHangMau.getId())
                .loaiDoiTra("TRA_HANG_HOAN_TIEN")
                .lyDo("Hàng bị rách chỉ may ở cổ tay áo")
                .soTienHoan(BigDecimal.valueOf(350000))
                .thongTinNganHang("MB Bank - 0988776655 - TRAN HUNG")
                .items(List.of(
                        CreateRmaItemRequest.builder()
                                .chiTietDonHangId(chiTietMau.getId())
                                .soLuongTra(1)
                                .tinhTrangSanPham("Rách chỉ may")
                                .build()
                ))
                .build();

        mockMvc.perform(post("/api/rma")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.loaiDoiTra").value("TRA_HANG_HOAN_TIEN"))
                .andExpect(jsonPath("$.soTienHoan").value(350000))
                .andExpect(jsonPath("$.thongTinNganHang").value("MB Bank - 0988776655 - TRAN HUNG"));
    }

    @Test
    @DisplayName("US25: CSKH phê duyệt (DA_DUYET) yêu cầu RMA và bổ sung ghi chú kiểm định hàng về kho")
    void testReviewRma_Approve_Success() throws Exception {
        // Tạo trước RMA
        CreateRmaRequest request = CreateRmaRequest.builder()
                .donHangId(donHangMau.getId())
                .khachHangId(khachHangMau.getId())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Đổi size")
                .items(List.of(
                        CreateRmaItemRequest.builder()
                                .chiTietDonHangId(chiTietMau.getId())
                                .soLuongTra(1)
                                .tinhTrangSanPham("Mới 100%")
                                .build()
                ))
                .build();

        String res = mockMvc.perform(post("/api/rma")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String maYeuCauRma = objectMapper.readTree(res).get("maYeuCauRma").asText();

        // CSKH duyệt và bổ sung ghi chú kiểm định
        ReviewRmaRequest reviewReq = ReviewRmaRequest.builder()
                .trangThai("DA_DUYET")
                .nguoiDuyetId(10L)
                .ghiChuKiemDinh("Hàng gửi về kho đạt chuẩn tem mác, duyệt xuất sản phẩm size mới cho khách")
                .build();

        mockMvc.perform(put("/api/rma/{maYeuCauRma}/review", maYeuCauRma)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("DA_DUYET"))
                .andExpect(jsonPath("$.nguoiDuyetId").value(10))
                .andExpect(jsonPath("$.ghiChuKiemDinh").value("Hàng gửi về kho đạt chuẩn tem mác, duyệt xuất sản phẩm size mới cho khách"));
    }

    @Test
    @DisplayName("US25: CSKH từ chối (TU_CHOI) yêu cầu RMA với lý do vi phạm chính sách")
    void testReviewRma_Reject_Success() throws Exception {
        // Tạo trước RMA
        CreateRmaRequest request = CreateRmaRequest.builder()
                .donHangId(donHangMau.getId())
                .khachHangId(khachHangMau.getId())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Không thích màu")
                .items(List.of(
                        CreateRmaItemRequest.builder()
                                .chiTietDonHangId(chiTietMau.getId())
                                .soLuongTra(1)
                                .tinhTrangSanPham("Đã cắt tem")
                                .build()
                ))
                .build();

        String res = mockMvc.perform(post("/api/rma")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        String maYeuCauRma = objectMapper.readTree(res).get("maYeuCauRma").asText();

        // CSKH từ chối
        ReviewRmaRequest reviewReq = ReviewRmaRequest.builder()
                .trangThai("TU_CHOI")
                .nguoiDuyetId(10L)
                .ghiChuKiemDinh("Sản phẩm đã bị cắt tem mác và giặt, vi phạm chính sách đổi trả")
                .build();

        mockMvc.perform(put("/api/rma/{maYeuCauRma}/review", maYeuCauRma)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reviewReq)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("TU_CHOI"));
    }

    @Test
    @DisplayName("US25: CSKH tra cứu danh sách yêu cầu RMA có lọc theo trạng thái")
    void testGetAllRma_FilterByStatus() throws Exception {
        CreateRmaRequest req = CreateRmaRequest.builder()
                .donHangId(donHangMau.getId())
                .khachHangId(khachHangMau.getId())
                .loaiDoiTra("DOI_HANG")
                .lyDo("Thử đồ")
                .items(List.of(CreateRmaItemRequest.builder()
                        .chiTietDonHangId(chiTietMau.getId()).soLuongTra(1).tinhTrangSanPham("Mới").build()))
                .build();

        mockMvc.perform(post("/api/rma").contentType(MediaType.APPLICATION_JSON).content(objectMapper.writeValueAsString(req)))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/api/rma").param("trangThai", "CHO_DUYET"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.length()").value(1));
    }
}
