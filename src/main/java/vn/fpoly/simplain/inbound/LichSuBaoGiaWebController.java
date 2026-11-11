package vn.fpoly.simplain.inbound;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/admin/lich-su-bao-gia")
public class LichSuBaoGiaWebController {
    private final NhaCungCapRepository nhaCungCapRepository;

    public LichSuBaoGiaWebController(NhaCungCapRepository nhaCungCapRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
    }

    @GetMapping({"", "/"})
    public String hienThi(Model model) {
        model.addAttribute("listNcc", nhaCungCapRepository.findAll(
                Sort.by(Sort.Direction.ASC, "tenNcc").and(Sort.by(Sort.Direction.ASC, "id"))));
        model.addAttribute("currentView", "lich-su-bao-gia");
        return "admin/lich-su-bao-gia/index";
    }
}
