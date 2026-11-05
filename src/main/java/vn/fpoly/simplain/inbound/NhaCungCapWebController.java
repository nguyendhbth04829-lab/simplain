package vn.fpoly.simplain.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
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

    @Autowired
    public NhaCungCapWebController(NhaCungCapRepository nhaCungCapRepository,
                                  PhieuNhapKhoRepository phieuNhapKhoRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
    }

    @GetMapping({"", "/"})
    public String trangDanhSach(Model model) {
        List<NhaCungCap> listNcc = nhaCungCapRepository.findAll(Sort.by(Sort.Direction.ASC, "maNcc"));
        List<PhieuNhapKho> listPhieu = phieuNhapKhoRepository.findAllWithNhaCungCap();

        int tongNcc = listNcc.size();
        long nccDangHoatDong = listNcc.stream().filter(n -> Boolean.TRUE.equals(n.getKichHoat())).count();
        long nccTamNgung = tongNcc - nccDangHoatDong;

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
                Long nccId = p.getNhaCungCap().getId();
                soPhieuTheoNcc.merge(nccId, 1, Integer::sum);
                tongTienTheoNcc.merge(nccId, p.getTongTien() != null ? p.getTongTien() : BigDecimal.ZERO, BigDecimal::add);
            }
        }

        model.addAttribute("listNcc", listNcc);
        model.addAttribute("listPhieu", listPhieu);
        model.addAttribute("tongNcc", tongNcc);
        model.addAttribute("nccDangHoatDong", nccDangHoatDong);
        model.addAttribute("nccTamNgung", nccTamNgung);
        model.addAttribute("tongPhieuNhap", tongPhieuNhap);
        model.addAttribute("tongGiaTriNhap", tongGiaTriNhap);
        model.addAttribute("giaTriNhapBinhQuan", giaTriNhapBinhQuan);
        model.addAttribute("soPhieuTheoNcc", soPhieuTheoNcc);
        model.addAttribute("tongTienTheoNcc", tongTienTheoNcc);

        return "admin/nha-cung-cap/index";
    }
}
