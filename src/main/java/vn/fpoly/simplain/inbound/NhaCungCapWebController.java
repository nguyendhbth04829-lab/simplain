package vn.fpoly.simplain.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin/nha-cung-cap")
public class NhaCungCapWebController {

    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final LichSuHopTacNhaCungCapRepository lichSuHopTacRepository;

    @Autowired
    public NhaCungCapWebController(NhaCungCapRepository nhaCungCapRepository,
                                  PhieuNhapKhoRepository phieuNhapKhoRepository,
                                  LichSuHopTacNhaCungCapRepository lichSuHopTacRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
        this.lichSuHopTacRepository = lichSuHopTacRepository;
    }

    @GetMapping({"", "/"})
    public String trangDanhSach(@RequestParam(required = false) String view,
                                @RequestParam(required = false) Long selectedNccId,
                                @RequestParam(required = false) Long nccId,
                                Model model) {
        if ("ncc-sku".equals(view)) {
            Long supplierId = selectedNccId != null ? selectedNccId : nccId;
            return "redirect:/admin/nha-cung-cap-sku" + (supplierId == null ? "" : "?nccId=" + supplierId);
        }
        List<NhaCungCap> listNcc = nhaCungCapRepository.findAll(Sort.by(Sort.Direction.ASC, "maNcc"));
        List<PhieuNhapKho> listPhieu = phieuNhapKhoRepository.findAllWithNhaCungCap();
        List<LichSuHopTacNhaCungCap> listLichSuHopTac = lichSuHopTacRepository.findAll(
                Sort.by(Sort.Order.desc("ngayTao"), Sort.Order.desc("id")));
        List<LoaiHoatDongHopTac> loaiHoatDongHopTac = List.of(
                LoaiHoatDongHopTac.GOI_DIEN,
                LoaiHoatDongHopTac.GUI_EMAIL,
                LoaiHoatDongHopTac.THUONG_LUONG,
                LoaiHoatDongHopTac.TRAO_DOI_BAO_GIA,
                LoaiHoatDongHopTac.GHI_CHU,
                LoaiHoatDongHopTac.KHAC);

        int tongNcc = listNcc.size();
        long nccTiemNang = listNcc.stream()
                .filter(n -> n.getTrangThaiHopTac() == TrangThaiNhaCungCap.TIEM_NANG).count();
        long nccDangThoaThuan = listNcc.stream()
                .filter(n -> n.getTrangThaiHopTac() == TrangThaiNhaCungCap.DANG_THOA_THUAN).count();
        long nccDangHopTac = listNcc.stream()
                .filter(n -> n.getTrangThaiHopTac() == TrangThaiNhaCungCap.DANG_HOP_TAC).count();
        long nccTamNgung = listNcc.stream()
                .filter(n -> n.getTrangThaiHopTac() == TrangThaiNhaCungCap.TAM_NGUNG).count();
        long nccNgungHopTac = listNcc.stream()
                .filter(n -> n.getTrangThaiHopTac() == TrangThaiNhaCungCap.NGUNG_HOP_TAC).count();
        long nccThoaThuanThatBai = listNcc.stream()
                .filter(n -> n.getTrangThaiHopTac() == TrangThaiNhaCungCap.THOA_THUAN_THAT_BAI).count();

        int tongPhieuNhap = listPhieu.size();
        BigDecimal tongGiaTriNhap = listPhieu.stream()
                .map(p -> p.getTongTien() != null ? p.getTongTien() : BigDecimal.ZERO)
                .reduce(BigDecimal.ZERO, BigDecimal::add);

        BigDecimal giaTriNhapBinhQuan = tongPhieuNhap > 0
                ? tongGiaTriNhap.divide(BigDecimal.valueOf(tongPhieuNhap), 0, java.math.RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Gom thống kê số đợt nhập & tổng tiền theo từng nhà cung cấp
        Map<Long, Integer> soPhieuTheoNcc = new HashMap<>();
        Map<Long, BigDecimal> tongTienTheoNcc = new HashMap<>();

        for (PhieuNhapKho p : listPhieu) {
            if (p.getNhaCungCap() != null && p.getNhaCungCap().getId() != null) {
                Long supplierIdForStats = p.getNhaCungCap().getId();
                soPhieuTheoNcc.merge(supplierIdForStats, 1, Integer::sum);
                tongTienTheoNcc.merge(supplierIdForStats, p.getTongTien() != null ? p.getTongTien() : BigDecimal.ZERO, BigDecimal::add);
            }
        }

        model.addAttribute("listNcc", listNcc);
        model.addAttribute("listLichSuHopTac", listLichSuHopTac);
        model.addAttribute("loaiHoatDongHopTac", loaiHoatDongHopTac);
        model.addAttribute("tongNcc", tongNcc);
        model.addAttribute("nccTiemNang", nccTiemNang);
        model.addAttribute("nccDangThoaThuan", nccDangThoaThuan);
        model.addAttribute("nccDangHopTac", nccDangHopTac);
        model.addAttribute("nccTamNgung", nccTamNgung);
        model.addAttribute("nccNgungHopTac", nccNgungHopTac);
        model.addAttribute("nccThoaThuanThatBai", nccThoaThuanThatBai);
        model.addAttribute("tongPhieuNhap", tongPhieuNhap);
        model.addAttribute("tongGiaTriNhap", tongGiaTriNhap);
        model.addAttribute("giaTriNhapBinhQuan", giaTriNhapBinhQuan);
        model.addAttribute("soPhieuTheoNcc", soPhieuTheoNcc);
        model.addAttribute("tongTienTheoNcc", tongTienTheoNcc);
        model.addAttribute("currentView", "overview");

        return "admin/nha-cung-cap/index";
    }
}
