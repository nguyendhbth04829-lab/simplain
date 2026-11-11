package vn.fpoly.simplain.inbound;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.GiaBanBienTheLichSuRepository;
import vn.fpoly.simplain.service.BienTheSanPhamService;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
class LichSuBaoGiaIntegrationTest {
    @Autowired private MockMvc mockMvc;
    @Autowired private NhaCungCapRepository nhaCungCapRepository;
    @Autowired private BienTheSanPhamRepository bienTheRepository;
    @Autowired private GiaBanBienTheLichSuRepository giaBanRepository;
    @Autowired private BienTheSanPhamService bienTheService;
    @Autowired private LichSuBaoGiaService baoGiaService;
    private final ObjectMapper objectMapper = new ObjectMapper().findAndRegisterModules();

    @Test
    void trangBaoGiaDungChungAdminCentreVaChoChonTungCap() throws Exception {
        mockMvc.perform(get("/admin/lich-su-bao-gia"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("class=\"q-history-heading\">Lịch sử báo giá</strong>")))
                .andExpect(content().string(containsString("grid-template-rows:44px auto")))
                .andExpect(content().string(containsString("margin-top:-.9em")))
                .andExpect(content().string(containsString("transform:translateY(.4em)")))
                .andExpect(content().string(containsString("id=\"historySkuLabel\" class=\"q-history-subtitle\"")))
                .andExpect(content().string(containsString("q-back-button")))
                .andExpect(content().string(not(containsString("Biến động giá theo thời gian"))))
                .andExpect(content().string(not(containsString("Hai chuỗi dùng chung trục thời gian"))))
                .andExpect(content().string(containsString("cf-shell")))
                .andExpect(content().string(containsString("supplierSelect")))
                .andExpect(content().string(containsString("quotePicker")))
                .andExpect(content().string(containsString("productList")))
                .andExpect(content().string(containsString("quoteHistory")))
                .andExpect(content().string(containsString("chartTooltip")))
                .andExpect(content().string(containsString("data-range=\"1y\"")))
                .andExpect(content().string(containsString("function taoMocTrucThoiGian")))
                .andExpect(content().string(containsString("Array.from({length:12}")))
                .andExpect(content().string(containsString("const showMonthYear = chartRangeMode === '1y'")))
                .andExpect(content().string(containsString("function gomCumDiem")))
                .andExpect(content().string(containsString("data-tip-items")))
                .andExpect(content().string(containsString("customRangePanel")))
                .andExpect(content().string(containsString("eventsFooter")))
                .andExpect(content().string(not(containsString("addQuoteButton"))));
    }

    @Test
    void donMuaCanGiuaModalVaChoMoChiTietTuHang() throws Exception {
        mockMvc.perform(get("/admin/don-mua"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("dialog{position:fixed;inset:0;margin:auto}")))
                .andExpect(content().string(containsString("class=\"order-row\" onclick=\"moChiTiet(${o.id})\"")))
                .andExpect(content().string(containsString("event.stopPropagation();moChiTiet(${o.id})")));
    }

    @Test
    void baoGiaDocLapDonHangVaGiaBanChiGhiKhiThayDoi() throws Exception {
        var nhaCungCap = nhaCungCapRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("Cần ít nhất một nhà cung cấp để kiểm thử"));
        BienTheSanPham sku = bienTheRepository.findAll().stream().findFirst()
                .orElseThrow(() -> new AssertionError("Cần ít nhất một SKU để kiểm thử"));
        BigDecimal giaBanBanDau = sku.getGiaBan();
        long soMocGiaBan = giaBanRepository.findAllByBienThe_IdOrderByThoiDiemAscIdAsc(sku.getId()).size();
        assertTrue(soMocGiaBan > 0, "SKU có mốc giá niêm yết ban đầu từ lúc bật theo dõi lịch sử");

        mockMvc.perform(post("/api/inbound/lich-su-bao-gia")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of(
                                "nhaCungCapId", nhaCungCap.getId(),
                                "bienTheId", sku.getId(),
                                "giaBao", 123456.78,
                                "thoiDiemBaoGia", LocalDateTime.now().minusMinutes(1)
                                        .withSecond(0).withNano(0).format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm")),
                                "ghiChu", "Báo giá kiểm thử"
                        ))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.gia").value(123456.78));

        assertEquals(0, giaBanBanDau.compareTo(bienTheRepository.findById(sku.getId()).orElseThrow().getGiaBan()));
        assertEquals(soMocGiaBan, giaBanRepository.findAllByBienThe_IdOrderByThoiDiemAscIdAsc(sku.getId()).size());
        assertTrue(baoGiaService.laySanPham(nhaCungCap.getId()).stream()
                .anyMatch(product -> product.id().equals(sku.getMaSanPhamId())));
        assertTrue(baoGiaService.laySku(nhaCungCap.getId(), sku.getMaSanPhamId()).stream()
                .anyMatch(option -> option.id().equals(sku.getId())));
        assertTrue(baoGiaService.laySkuDeChon(nhaCungCap.getId()).stream()
                .anyMatch(option -> option.bienTheId().equals(sku.getId())));
        mockMvc.perform(get("/api/inbound/lich-su-bao-gia/nha-cung-cap/{nccId}/sku/{skuId}", nhaCungCap.getId(), sku.getId()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.giaMua.length()").value(1))
                .andExpect(jsonPath("$.giaMua[0].gia").value(123456.78))
                .andExpect(jsonPath("$.giaBan.length()").value((int) soMocGiaBan));

        bienTheService.capNhatNhanhTonKhoVaGia(sku.getId(), sku.getSoLuong(), giaBanBanDau, sku.getGiaGoc());
        assertEquals(soMocGiaBan, giaBanRepository.findAllByBienThe_IdOrderByThoiDiemAscIdAsc(sku.getId()).size(),
                "Lưu lại cùng giá không tạo một mốc giá mới");
        BigDecimal giaBanMoi = giaBanBanDau.compareTo(new BigDecimal("999999998.00")) >= 0
                ? giaBanBanDau.subtract(new BigDecimal("1.00")) : giaBanBanDau.add(new BigDecimal("1.00"));
        bienTheService.capNhatNhanhTonKhoVaGia(sku.getId(), sku.getSoLuong(), giaBanMoi, sku.getGiaGoc());
        var giaBanLichSu = giaBanRepository.findAllByBienThe_IdOrderByThoiDiemAscIdAsc(sku.getId());
        assertEquals(soMocGiaBan + 1, giaBanLichSu.size());
        assertEquals(0, giaBanMoi.compareTo(giaBanLichSu.get(giaBanLichSu.size() - 1).getGiaBan()));
        assertTrue(giaBanLichSu.get(giaBanLichSu.size() - 1).getThoiDiem() != null);
    }
}
