package vn.fpoly.simplain.inbound;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/lay-hang")
public class InboundWebController {

    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;

    public InboundWebController(NhaCungCapRepository nhaCungCapRepository,
                                PhieuNhapKhoRepository phieuNhapKhoRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
    }

    @GetMapping({"", "/"})
    public String trangTaoDon(@RequestParam(required = false) String nccId, Model model) {
        var nhaCungCaps = nhaCungCapRepository.findAll().stream()
                .filter(ncc -> ncc.getTrangThaiHopTac() == TrangThaiNhaCungCap.DANG_HOP_TAC)
                .sorted(java.util.Comparator.comparing(NhaCungCap::getTenNcc, String.CASE_INSENSITIVE_ORDER)).toList();
        Long nccMacDinhId = null;
        if (nccId != null && !nccId.isBlank()) {
            NhaCungCap ncc = nhaCungCapRepository.findByMaNccIgnoreCase(nccId.trim()).orElseGet(() -> {
                try { return nhaCungCapRepository.findById(Long.valueOf(nccId.trim())).orElse(null); }
                catch (NumberFormatException ignored) { return null; }
            });
            if (ncc != null && ncc.getTrangThaiHopTac() == TrangThaiNhaCungCap.DANG_HOP_TAC) nccMacDinhId = ncc.getId();
        }
        model.addAttribute("listNcc", nhaCungCaps);
        model.addAttribute("tongPhieuNhap", phieuNhapKhoRepository.count());
        model.addAttribute("nccMacDinhId", nccMacDinhId);
        model.addAttribute("menuActive", "lay-hang");
        return "admin/lay-hang/index";
    }
}
