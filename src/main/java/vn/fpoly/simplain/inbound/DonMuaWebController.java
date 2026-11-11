package vn.fpoly.simplain.inbound;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/admin/don-mua")
public class DonMuaWebController {
    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;

    public DonMuaWebController(NhaCungCapRepository nhaCungCapRepository,
                               PhieuNhapKhoRepository phieuNhapKhoRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
    }

    @GetMapping({"", "/"})
    public String hienThi(@RequestParam(required = false) Long nccId, Model model) {
        model.addAttribute("listNcc", nhaCungCapRepository.findAll(org.springframework.data.domain.Sort.by(
                org.springframework.data.domain.Sort.Direction.ASC, "tenNcc")));
        model.addAttribute("nhaCungCapHopTac", nhaCungCapRepository.findAll().stream()
                .filter(ncc -> ncc.getTrangThaiHopTac() == TrangThaiNhaCungCap.DANG_HOP_TAC)
                .sorted(java.util.Comparator.comparing(NhaCungCap::getTenNcc, String.CASE_INSENSITIVE_ORDER)).toList());
        model.addAttribute("tongPhieuNhap", phieuNhapKhoRepository.count());
        model.addAttribute("nccMacDinhId", nccId);
        model.addAttribute("currentView", "don-mua");
        return "admin/don-mua/index";
    }
}
