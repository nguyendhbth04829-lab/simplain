package vn.fpoly.simplain.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.web.servlet.MockMvc;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.BienTheSanPhamThuocTinh;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.entity.ThuocTinhBienThe;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.BienTheSanPhamThuocTinhRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.ThuocTinhBienTheRepository;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.multipart;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
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

    @Autowired
    private NhaCungCapRepository nhaCungCapRepository;

    @Autowired
    private DonMuaHangRepository donMuaHangRepository;

    @Autowired
    private NhaCungCapSkuRepository nhaCungCapSkuRepository;

    @Autowired
    private BaoGiaNhaCungCapSkuRepository baoGiaNhaCungCapSkuRepository;

    @Autowired
    private PhieuNhapKhoRepository phieuNhapKhoRepository;

    @Autowired
    private LichSuHopTacNhaCungCapRepository lichSuHopTacRepository;

    @Autowired
    private ThuocTinhBienTheRepository thuocTinhBienTheRepository;

    @Autowired
    private BienTheSanPhamThuocTinhRepository bienTheSanPhamThuocTinhRepository;

    @Autowired
    private NhaCungCapSkuService nhaCungCapSkuService;

    @Autowired
    private jakarta.validation.Validator validator;

    @Test
    @DisplayName("Từ chối nhập kho trực tiếp không qua đơn mua")
    void tuChoiNhapKhoTrucTiepKhongQuaDonMua() throws Exception {
        String skuTarget = "SP-POLO-EXCOOL-01-DEN-M";
        BienTheSanPham bienThe = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget)
                .orElseThrow(() -> new IllegalStateException("Không tìm thấy SKU: " + skuTarget));

        int tonTruoc = bienThe.getSoLuong();
        System.out.println("========== [TRƯỚC KHI GỌI ENDPOINT] ==========");
        System.out.println("SKU: " + skuTarget);
        System.out.println("Tồn kho hiện tại: " + tonTruoc);

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-MAY-001")
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
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.thongBao").value(containsString("đơn mua")));

        BienTheSanPham sauKhiNhap = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget).orElseThrow();
        assertEquals(tonTruoc, sauKhiNhap.getSoLuong());
    }

    @Test
    @DisplayName("Nhập kho trực tiếp bị chặn và không thay đổi tồn ở hai kênh")
    void nhapKhoTrucTiepKhongThayDoiTonHaiKenh() throws Exception {
        String skuTarget = "SP-POLO-EXCOOL-01-DEN-M";
        BienTheSanPham truocNhap = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget).orElseThrow();
        int tonTruoc = truocNhap.getSoLuong();
        int soLuongNhap = 7;

        NhapKhoRequest request = NhapKhoRequest.builder()
                .maNhaCungCap("NCC-MAY-001")
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
                .andExpect(status().isConflict());

        BienTheSanPham sauNhap = bienTheSanPhamRepository.findByMaSkuIgnoreCase(skuTarget).orElseThrow();
        SanPham sanPham = sanPhamRepository.findById(sauNhap.getMaSanPhamId()).orElseThrow();
        PhanBoTonKho phanBo = phanBoTonKhoService.phanBo(sauNhap, Boolean.TRUE.equals(sanPham.getKichHoat()));

        int tonMongDoi = tonTruoc;
        assertEquals(tonMongDoi, sauNhap.getSoLuong());
        assertEquals(tonMongDoi, phanBo.tonKhoVatLy());
        assertEquals(tonMongDoi, phanBo.soLuongKeOffline());
        assertEquals(tonMongDoi, phanBo.soLuongWebOnline());
        assertEquals(PhanBoTonKho.NGUON_DUNG_CHUNG, phanBo.nguonTonKho());
    }

    @Test
    @DisplayName("Trang nhập hàng là form tạo đơn mua nháp, không nhập kho trực tiếp")
    void trangNhapHangLaFormTaoDonMuaNhap() throws Exception {
        mockMvc.perform(get("/admin/lay-hang"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("TẠO ĐƠN MUA NHÁP")))
                .andExpect(content().string(containsString("/sku/options")))
                .andExpect(content().string(containsString("/admin/don-mua?view=phieu-nhap")))
                .andExpect(content().string(containsString("Lưu đơn mua nháp")))
                .andExpect(content().string(containsString("skuImageModal")))
                .andExpect(content().string(containsString("function moAnhLon")))
                .andExpect(content().string(not(containsString("oBatchDonGia"))))
                .andExpect(content().string(not(containsString("input-table-price"))))
                .andExpect(content().string(not(containsString("Giá thỏa thuận / cái"))))
                .andExpect(content().string(not(containsString("drawerLichSu"))))
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
                .andExpect(content().string(containsString("Tiềm năng")))
                .andExpect(content().string(containsString("Đang thỏa thuận")))
                .andExpect(content().string(containsString("Thỏa thuận thất bại")))
                .andExpect(content().string(containsString("Lịch sử hợp tác")))
                .andExpect(content().string(containsString("Ghi nhận hoạt động mới")))
                .andExpect(content().string(containsString("Xem lịch sử hợp tác")))
                .andExpect(content().string(containsString("moLichSuHopTacChoNcc(")))
                .andExpect(content().string(containsString("function capNhatNccTrenGiaoDien")))
                .andExpect(content().string(containsString("Tất cả nhà cung cấp")))
                .andExpect(content().string(containsString("maxlength=\"100\"")))
                .andExpect(content().string(containsString("capNhatGioiHanDongGhiChu")))
                .andExpect(content().string(containsString("value=\"TIEM_NANG\"")))
                .andExpect(content().string(containsString("Ngừng hợp tác")))
                .andExpect(content().string(containsString("workspace")))
                .andExpect(content().string(containsString("supplier-grid")))
                .andExpect(content().string(containsString("supplier-card")))
                .andExpect(content().string(containsString("detail-panel")))
                .andExpect(content().string(containsString("drawer-overlay")))
                .andExpect(content().string(containsString("btn-primary-lg")))
                .andExpect(content().string(not(containsString("Lịch sử nhập kho"))))
                .andExpect(content().string(not(containsString("id=\"vungLichSu\""))))
                .andExpect(content().string(not(containsString("tabBtnLichSu\""))))
                .andExpect(content().string(not(containsString("command-bar"))))
                .andExpect(content().string(not(containsString("Đến bước 2"))))
                .andExpect(content().string(not(containsString("topbar-right"))));
    }

    @Test
    @DisplayName("Trang nhà cung cấp render lịch sử hợp tác có trạng thái và ghi chú")
    void trangNhaCungCapRenderLichSuHopTac() throws Exception {
        NhaCungCap ncc = nhaCungCapRepository.findByMaNccIgnoreCase("NCC-MAY-001").orElseThrow();
        lichSuHopTacRepository.save(LichSuHopTacNhaCungCap.builder()
                .nhaCungCap(ncc)
                .loaiHoatDong(LoaiHoatDongHopTac.THAY_DOI_TRANG_THAI)
                .trangThaiTruoc(TrangThaiNhaCungCap.TIEM_NANG)
                .trangThaiMoi(TrangThaiNhaCungCap.DANG_THOA_THUAN)
                .ghiChu("Đã bắt đầu thương lượng")
                .build());

        mockMvc.perform(get("/admin/nha-cung-cap"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Đã bắt đầu thương lượng")))
                .andExpect(content().string(containsString("history-note-preview")))
                .andExpect(content().string(containsString("modalLichSuGhiChu")))
                .andExpect(content().string(containsString("SO_DONG_LICH_SU_MOI_TRANG = 5")))
                .andExpect(content().string(containsString("history-table-scroll")))
                .andExpect(content().string(containsString("Tiềm năng")))
                .andExpect(content().string(containsString("Đang thỏa thuận")));
    }

    @Test
    @DisplayName("Tạo và cập nhật logo nhà cung cấp qua multipart, giữ logo nếu không tải ảnh mới")
    void logoNhaCungCapDuocLuuVaGiuKhiKhongChonAnhMoi() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        NhaCungCapRequest request = NhaCungCapRequest.builder()
                .maNcc("NCC-LOGO-" + suffix)
                .tenNcc("NCC logo kiểm thử " + suffix)
                .build();
        MockMultipartFile requestPart = new MockMultipartFile("request", "request.json",
                MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(request));
        MockMultipartFile firstLogo = new MockMultipartFile("logo", "logo.png", "image/png",
                new byte[]{(byte) 0x89, 'P', 'N', 'G'});
        String firstPath = null;
        String secondPath = null;
        try {
            String createdJson = mockMvc.perform(multipart("/api/inbound/nha-cung-cap")
                            .file(requestPart).file(firstLogo))
                    .andExpect(status().isCreated())
                    .andExpect(jsonPath("$.anhLogo").value(containsString("/uploads/nha-cung-cap/")))
                    .andReturn().getResponse().getContentAsString();
            var created = objectMapper.readTree(createdJson);
            long id = created.get("id").asLong();
            firstPath = created.get("anhLogo").asText();

            NhaCungCapRequest edit = NhaCungCapRequest.builder()
                    .tenNcc("NCC logo đã cập nhật " + suffix)
                    .build();
            mockMvc.perform(put("/api/inbound/nha-cung-cap/{id}", id)
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(edit)))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.anhLogo").value(firstPath));

            MockMultipartFile updateRequest = new MockMultipartFile("request", "request.json",
                    MediaType.APPLICATION_JSON_VALUE, objectMapper.writeValueAsBytes(edit));
            MockMultipartFile secondLogo = new MockMultipartFile("logo", "logo.webp", "image/webp",
                    new byte[]{'R', 'I', 'F', 'F'});
            String updatedJson = mockMvc.perform(multipart("/api/inbound/nha-cung-cap/{id}", id)
                            .file(updateRequest).file(secondLogo).with(servletRequest -> { servletRequest.setMethod("PUT"); return servletRequest; }))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.anhLogo").value(containsString(".webp")))
                    .andReturn().getResponse().getContentAsString();
            secondPath = objectMapper.readTree(updatedJson).get("anhLogo").asText();
        } finally {
            xoaLogoTaiDuongDan(firstPath);
            xoaLogoTaiDuongDan(secondPath);
        }
    }

    @Test
    @DisplayName("API từ chối kết quả hoạt động dài hơn 100 ký tự")
    void apiTuChoiKetQuaVuot100KyTu() throws Exception {
        LichSuHopTacRequest request = LichSuHopTacRequest.builder()
                .loaiHoatDong(LoaiHoatDongHopTac.GOI_DIEN)
                .ketQua("A".repeat(101))
                .build();

        mockMvc.perform(post("/api/inbound/nha-cung-cap/1/lich-su-hop-tac")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Tạo hoạt động trả về bản ghi để cập nhật tab mà không tải lại trang")
    void apiTaoHoatDongTraVeBanGhiMoiNhat() throws Exception {
        NhaCungCap ncc = nhaCungCapRepository.findByMaNccIgnoreCase("NCC-MAY-001").orElseThrow();
        LichSuHopTacRequest request = LichSuHopTacRequest.builder()
                .loaiHoatDong(LoaiHoatDongHopTac.GOI_DIEN)
                .ghiChu("Trao đổi lịch giao mẫu")
                .ketQua("Hẹn phản hồi trong ngày")
                .build();
        String lichSuUrl = "/api/inbound/nha-cung-cap/" + ncc.getId() + "/lich-su-hop-tac";

        mockMvc.perform(post(lichSuUrl)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.ghiChu").value("Trao đổi lịch giao mẫu"));

        mockMvc.perform(get(lichSuUrl + "/moi-nhat"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ketQua").value("Hẹn phản hồi trong ngày"));
    }

    @Test
    @DisplayName("Quản lý đơn mua, giao lại trên cùng đơn và chỉ nhập tồn phần đạt")
    void donMuaGiaoLaiVaKiemHangChiCongTonPhanDat() throws Exception {
        String maNcc = "NCC-PO-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        NhaCungCap ncc = nhaCungCapRepository.save(NhaCungCap.builder()
                .maNcc(maNcc).tenNcc("NCC kiểm thử đơn mua " + maNcc)
                .trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC).build());
        String sku = "SP-POLO-EXCOOL-01-DEN-M";
        BienTheSanPham truoc = bienTheSanPhamRepository.findByMaSkuIgnoreCase(sku).orElseThrow();
        nhaCungCapSkuRepository.save(NhaCungCapSku.builder().nhaCungCap(ncc).bienThe(truoc).build());
        int tonTruoc = truoc.getSoLuong();

        mockMvc.perform(post("/api/inbound/don-mua")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nhaCungCapId":%d,"danhSachHang":[{"maSku":"%s","soLuongDat":4,"donGiaChot":50000}]}
                                """.formatted(ncc.getId(), sku)))
                .andExpect(status().isBadRequest());

        String json = mockMvc.perform(post("/api/inbound/don-mua")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nhaCungCapId":%d,"ghiChu":"Đợt hàng kiểm thử","danhSachHang":[
                                  {"maSku":"%s","soLuongDat":4,"donGiaChot":null}
                                ]}
                                """.formatted(ncc.getId(), sku)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trangThai").value("NHAP"))
                .andReturn().getResponse().getContentAsString();
        long donId = objectMapper.readTree(json).get("id").asLong();
        assertTrue(baoGiaNhaCungCapSkuRepository
                .findAllByNhaCungCap_IdAndBienThe_IdOrderByThoiDiemBaoGiaAscIdAsc(ncc.getId(), truoc.getId()).isEmpty(),
                "Tạo đơn nháp không ghi giá vào lịch sử báo giá");

        mockMvc.perform(put("/api/inbound/don-mua/{id}", donId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nhaCungCapId":%d,"danhSachHang":[{"maSku":"%s","soLuongDat":4,"donGiaChot":50000}]}
                                """.formatted(ncc.getId(), sku)))
                .andExpect(status().isBadRequest());

        mockMvc.perform(put("/api/inbound/don-mua/{id}", donId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nhaCungCapId":%d,"ghiChu":"Đơn nháp đã chỉnh sửa","danhSachHang":[
                                  {"maSku":"%s","soLuongDat":4,"donGiaChot":null}
                                ]}
                                """.formatted(ncc.getId(), sku)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chiTiet[0].soLuongDat").value(4));

        mockMvc.perform(put("/api/inbound/don-mua/{id}/trang-thai", donId)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"trangThai":"DA_CHOT_DON"}
                                """))
                .andExpect(status().isConflict());

        DonMuaHang legacyDraft = donMuaHangRepository.findById(donId).orElseThrow();
        legacyDraft.getChiTietList().get(0).setDonGiaChot(new BigDecimal("50000"));
        legacyDraft.getChiTietList().get(0).setThanhTien(new BigDecimal("200000"));
        legacyDraft.setTongTien(new BigDecimal("200000"));
        donMuaHangRepository.saveAndFlush(legacyDraft);
        mockMvc.perform(put("/api/inbound/don-mua/{id}/trang-thai", donId)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"trangThai":"CHO_NCC_PHAN_HOI"}
                                """))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.thongBao").value(containsString("Đơn nháp cũ")));
        legacyDraft.getChiTietList().get(0).setDonGiaChot(BigDecimal.ZERO);
        legacyDraft.getChiTietList().get(0).setThanhTien(BigDecimal.ZERO);
        legacyDraft.setTongTien(BigDecimal.ZERO);
        donMuaHangRepository.saveAndFlush(legacyDraft);

        doiTrangThaiDonMua(donId, "CHO_NCC_PHAN_HOI");
        mockMvc.perform(put("/api/inbound/don-mua/{id}/trang-thai", donId)
                        .contentType(MediaType.APPLICATION_JSON).content("""
                                {"trangThai":"DA_CHOT_DON"}
                                """))
                .andExpect(status().isConflict());
        mockMvc.perform(put("/api/inbound/don-mua/{id}/gia-thoa-thuan", donId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nhaCungCapId":%d,"danhSachHang":[{"maSku":"%s","soLuongDat":4,"donGiaChot":50000}]}
                                """.formatted(ncc.getId(), sku)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.chiTiet[0].donGiaChot").value(50000));
        var savedQuote = baoGiaNhaCungCapSkuRepository
                .findAllByNhaCungCap_IdAndBienThe_IdOrderByThoiDiemBaoGiaAscIdAsc(ncc.getId(), truoc.getId());
        assertEquals(1, savedQuote.size(), "Lưu giá thỏa thuận tự ghi đúng một mốc báo giá");
        assertEquals(0, new BigDecimal("50000").compareTo(savedQuote.get(0).getGiaBao()));
        assertTrue(savedQuote.get(0).getGhiChu().contains(objectMapper.readTree(json).get("maDon").asText()));
        mockMvc.perform(get("/api/inbound/lich-su-bao-gia/nha-cung-cap/{nccId}/sku/{skuId}", ncc.getId(), truoc.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.giaMua.length()").value(1))
                .andExpect(jsonPath("$.giaMua[0].gia").value(50000));
        doiTrangThaiDonMua(donId, "DA_CHOT_DON");
        doiTrangThaiDonMua(donId, "DANG_CHUAN_BI_HANG");
        assertTrue(nhaCungCapSkuRepository.existsByNhaCungCapIdAndBienTheId(ncc.getId(), truoc.getId()));
        String lanDau = mockMvc.perform(post("/api/inbound/don-mua/{id}/lan-giao", donId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long lanDauId = objectMapper.readTree(lanDau).get("lanGiao").get(0).get("id").asLong();
        mockMvc.perform(put("/api/inbound/don-mua/{id}/lan-giao/{lanGiaoId}/that-bai", donId, lanDauId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"lyDo":"Xe giao hàng gặp sự cố","benChiuTrachNhiem":"BEN_VAN_CHUYEN","bangChung":"BB-001"}
                                """))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("CHO_GIAO_BU"));

        String lanHai = mockMvc.perform(post("/api/inbound/don-mua/{id}/lan-giao", donId))
                .andExpect(status().isOk()).andReturn().getResponse().getContentAsString();
        long lanHaiId = objectMapper.readTree(lanHai).get("lanGiao").get(1).get("id").asLong();
        mockMvc.perform(put("/api/inbound/don-mua/{id}/lan-giao/{lanGiaoId}/da-giao", donId, lanHaiId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("CHO_KIEM_HANG"));

        String nhap = mockMvc.perform(post("/api/inbound/don-mua/{id}/nhan-hang", donId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"ghiChu":"Một sản phẩm bị rách","danhSachHang":[
                                  {"maSku":"%s","soLuongDaGiao":2,"soLuongDat":1,"soLuongTuChoi":1,"soLuongDaTra":1,"lyDoTuChoi":"Rách đường may","donGiaNhapThucTe":52000}
                                ]}
                                """.formatted(sku)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.trangThai").value("THANH_CONG"))
                .andExpect(jsonPath("$.tongTien").value(52000))
                .andReturn().getResponse().getContentAsString();
        BienTheSanPham sau = bienTheSanPhamRepository.findByMaSkuIgnoreCase(sku).orElseThrow();
        assertEquals(tonTruoc + 1, sau.getSoLuong(), "Chỉ số lượng đạt được cộng vào tồn");

        mockMvc.perform(get("/api/inbound/don-mua/{id}", donId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.trangThai").value("CHO_GIAO_BU"))
                .andExpect(jsonPath("$.chiTiet[0].soLuongDaNhan").value(1))
                .andExpect(jsonPath("$.lanGiao[0].trangThai").value("THAT_BAI"))
                .andExpect(jsonPath("$.lanGiao[1].lanThu").value(2));

        String maPhieu = objectMapper.readTree(nhap).get("maPhieu").asText();
        long phieuId = phieuNhapKhoRepository.findByMaPhieu(maPhieu).orElseThrow().getId();
        mockMvc.perform(get("/api/inbound/phieu-nhap-kho"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").exists());
        mockMvc.perform(get("/api/inbound/phieu-nhap-kho/{id}", phieuId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.ketQuaKiemHang").value("Chấp nhận một phần"))
                .andExpect(jsonPath("$.lanGiaoThu").value(2))
                .andExpect(jsonPath("$.chiTietKiemHang[0].soLuongDaGiao").value(2))
                .andExpect(jsonPath("$.chiTietKiemHang[0].soLuongTuChoi").value(1));
    }

    @Test
    @DisplayName("Trang đơn mua hiển thị quản lý quy trình và điều kiện giao")
    void trangDonMuaCoQuanLyQuyTrinh() throws Exception {
        mockMvc.perform(get("/admin/don-mua"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Đơn mua hàng")))
                .andExpect(content().string(containsString("cf-shell")))
                .andExpect(content().string(containsString("cf-sidebar")))
                .andExpect(content().string(containsString("active\">Đơn mua")))
                .andExpect(content().string(containsString("Phiếu nhập kho")))
                .andExpect(content().string(containsString("Lịch sử nhập kho")))
                .andExpect(content().string(containsString("ledgerDrawer")))
                .andExpect(content().string(containsString("ledger-summary")))
                .andExpect(content().string(containsString("ledger-table")))
                .andExpect(content().string(containsString("filterReceiptSupplier")))
                .andExpect(content().string(containsString("href=\"/admin/don-mua\"")))
                .andExpect(content().string(not(containsString("line-price"))))
                .andExpect(content().string(not(containsString("Giá chốt / đơn vị"))))
                .andExpect(content().string(containsString("Ghi nhận lần giao tiếp theo")))
                .andExpect(content().string(containsString("Nhập giá thỏa thuận")))
                .andExpect(content().string(containsString("qcReasonDialog")))
                .andExpect(content().string(containsString("popover=\"manual\"")))
                .andExpect(content().string(containsString("#detailDialog{width:min(1500px")))
                .andExpect(content().string(containsString("chỉ số đạt mới được cộng tồn")));
    }

    @Test
    @DisplayName("Lý do từ chối được giới hạn theo dung lượng cột 1.000 ký tự")
    void lyDoTuChoiVuotGioiHanBiTuChoi() {
        TiepNhanDonMuaRequest request = TiepNhanDonMuaRequest.builder()
                .danhSachHang(List.of(TiepNhanDonMuaRequest.ChiTiet.builder()
                        .maSku("SKU-TEST")
                        .soLuongDaGiao(1).soLuongDat(0).soLuongTuChoi(1).soLuongDaTra(0)
                        .lyDoTuChoi("L".repeat(1001))
                        .build()))
                .build();
        assertTrue(validator.validate(request).stream()
                .anyMatch(error -> error.getPropertyPath().toString().contains("lyDoTuChoi")));
    }

    @Test
    @DisplayName("Một SKU có thể liên kết với nhiều nhà cung cấp và thay danh sách không ảnh hưởng NCC khác")
    void quanLyLienKetNhaCungCapVaSkuNhieuNhieu() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        NhaCungCap nccA = nhaCungCapRepository.save(NhaCungCap.builder().maNcc("NCC-LK-A-" + suffix)
                .tenNcc("NCC liên kết A " + suffix).trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC).build());
        NhaCungCap nccB = nhaCungCapRepository.save(NhaCungCap.builder().maNcc("NCC-LK-B-" + suffix)
                .tenNcc("NCC liên kết B " + suffix).trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC).build());
        List<String> skus = bienTheSanPhamRepository.findAll().stream().filter(bt -> Boolean.TRUE.equals(bt.getKichHoat()))
                .map(BienTheSanPham::getMaSku).distinct().limit(2).toList();
        assertEquals(2, skus.size());

        mockMvc.perform(put("/api/inbound/nha-cung-cap/{id}/sku", nccA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NhaCungCapSkuRequest(skus))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").exists());
        mockMvc.perform(put("/api/inbound/nha-cung-cap/{id}/sku", nccB.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NhaCungCapSkuRequest(List.of(skus.get(0))))))
                .andExpect(status().isOk());

        mockMvc.perform(put("/api/inbound/nha-cung-cap/{id}/sku", nccA.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new NhaCungCapSkuRequest(List.of(skus.get(1))))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(skus.get(1)))
                .andExpect(jsonPath("$[1]").doesNotExist());
        mockMvc.perform(get("/api/inbound/nha-cung-cap/{id}/sku", nccB.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0]").value(skus.get(0)));
        mockMvc.perform(get("/api/inbound/nha-cung-cap/{id}/sku/options", nccA.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].maSku").value(skus.get(1)));

        mockMvc.perform(get("/admin/nha-cung-cap-sku").param("nccId", nccA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("cf-shell")))
                .andExpect(content().string(containsString("cf-sidebar")))
                .andExpect(content().string(containsString(".cf-action-btn {")))
                .andExpect(content().string(containsString(".btn-primary-lg {")))
                .andExpect(content().string(containsString("Kho &amp; Nguồn Hàng")))
                .andExpect(content().string(containsString("Danh sách nhà cung cấp")))
                .andExpect(content().string(containsString("Phân công nhà cung cấp – SKU")))
                .andExpect(content().string(containsString("ncc-sku-product-list")))
                .andExpect(content().string(containsString("nccSkuAttributeFilters")))
                .andExpect(content().string(containsString("nccSkuFilterPanel")))
                .andExpect(content().string(containsString("nccSkuFilterToggle")))
                .andExpect(content().string(containsString("nccSkuImageModal")))
                .andExpect(content().string(containsString("xemAnhNccSku")))
                .andExpect(content().string(containsString("ncc-sku-product-image ncc-sku-photo-preview")))
                .andExpect(content().string(containsString("ncc-sku-variant-image ncc-sku-photo-preview")))
                .andExpect(content().string(containsString("sku.anhSkuUrl")))
                .andExpect(content().string(containsString("sku.thuocTinh")))
                .andExpect(content().string(containsString("Chọn tất cả SKU của")))
                .andExpect(content().string(containsString("Chưa có ảnh cho")))
                .andExpect(content().string(containsString("Chọn SKU đang hiển thị")))
                .andExpect(content().string(containsString("Lưu phân công")));

        mockMvc.perform(get("/admin/nha-cung-cap").param("view", "ncc-sku").param("nccId", nccA.getId().toString()))
                .andExpect(status().is3xxRedirection())
                .andExpect(header().string("Location", containsString("/admin/nha-cung-cap-sku?nccId=")));

        mockMvc.perform(get("/admin/nha-cung-cap-sku").param("nccId", nccA.getId().toString()))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("Phân công nhà cung cấp – SKU")))
                .andExpect(content().string(containsString("cf-shell")));
    }

    @Test
    @DisplayName("Đơn nháp chỉ nhận SKU đã phân công cho đúng nhà cung cấp")
    void donMuaTuChoiSkuChuaDuocPhanCong() throws Exception {
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        NhaCungCap ncc = nhaCungCapRepository.save(NhaCungCap.builder().maNcc("NCC-DRAFT-" + suffix)
                .tenNcc("NCC tạo nháp kiểm thử " + suffix).trangThaiHopTac(TrangThaiNhaCungCap.DANG_HOP_TAC).build());
        BienTheSanPham sku = bienTheSanPhamRepository.findAll().stream()
                .filter(bt -> Boolean.TRUE.equals(bt.getKichHoat())).findFirst().orElseThrow();
        assertFalse(nhaCungCapSkuRepository.existsByNhaCungCapIdAndBienTheId(ncc.getId(), sku.getId()));

        mockMvc.perform(post("/api/inbound/don-mua")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nhaCungCapId":%d,"danhSachHang":[{"maSku":"%s","soLuongDat":2,"donGiaChot":null}]}
                                """.formatted(ncc.getId(), sku.getMaSku())))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.thongBao").value(containsString("chưa được phân công")));
    }

    @Test
    @DisplayName("Thuộc tính SKU khai báo trong CSDL tự xuất hiện trên trang phân công")
    void thuocTinhBienTheMoiTuCSDLTuDongXuatHien() throws Exception {
        BienTheSanPham sku = bienTheSanPhamRepository.findAll().stream().findFirst().orElseThrow();
        String suffix = java.util.UUID.randomUUID().toString().substring(0, 8);
        ThuocTinhBienThe chatLieu = thuocTinhBienTheRepository.save(ThuocTinhBienThe.builder()
                .maThuocTinh("chatLieu_" + suffix)
                .tenThuocTinh("Chất liệu kiểm thử " + suffix)
                .kichHoat(true)
                .thuTu(90)
                .build());
        ThuocTinhBienThe form = thuocTinhBienTheRepository.save(ThuocTinhBienThe.builder()
                .maThuocTinh("form_" + suffix)
                .tenThuocTinh("Form kiểm thử " + suffix)
                .kichHoat(true)
                .thuTu(91)
                .build());
        bienTheSanPhamThuocTinhRepository.save(BienTheSanPhamThuocTinh.builder()
                .bienThe(sku).thuocTinh(chatLieu).giaTri("Cotton").build());

        NhaCungCapSkuOption skuOption = nhaCungCapSkuService.layDanhSachSku().stream()
                .filter(option -> option.maSku().equalsIgnoreCase(sku.getMaSku()))
                .findFirst().orElseThrow();
        assertTrue(skuOption.thuocTinh().stream().anyMatch(attribute ->
                attribute.maThuocTinh().equals(chatLieu.getMaThuocTinh()) && attribute.giaTri().equals("Cotton")));
        assertTrue(skuOption.thuocTinh().stream().anyMatch(attribute ->
                attribute.maThuocTinh().equals(form.getMaThuocTinh()) && attribute.giaTri().equals("—")));

        mockMvc.perform(get("/admin/nha-cung-cap-sku"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString(chatLieu.getMaThuocTinh())))
                .andExpect(content().string(containsString(form.getMaThuocTinh())))
                .andExpect(content().string(containsString("Cotton")))
                .andExpect(content().string(containsString("nccSkuAttributeDefinitions")));
    }

    private void doiTrangThaiDonMua(long donId, String trangThai) throws Exception {
        mockMvc.perform(put("/api/inbound/don-mua/{id}/trang-thai", donId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"trangThai\":\"" + trangThai + "\"}"))
                .andExpect(status().isOk());
    }

    private void xoaLogoTaiDuongDan(String url) throws Exception {
        if (url == null || !url.startsWith("/uploads/nha-cung-cap/")) return;
        Files.deleteIfExists(Paths.get("uploads").resolve(url.substring("/uploads/".length())));
    }
}
