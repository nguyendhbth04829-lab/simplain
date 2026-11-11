package vn.fpoly.simplain.inbound;

import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/admin/nha-cung-cap-sku")
public class NhaCungCapSkuWebController {

    private final NhaCungCapRepository nhaCungCapRepository;
    private final NhaCungCapSkuService nhaCungCapSkuService;

    public NhaCungCapSkuWebController(NhaCungCapRepository nhaCungCapRepository,
                                      NhaCungCapSkuService nhaCungCapSkuService) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.nhaCungCapSkuService = nhaCungCapSkuService;
    }

    @GetMapping({"", "/"})
    public String moTrongAdminCentre(@RequestParam(required = false) Long nccId, Model model) {
        model.addAttribute("listNcc", nhaCungCapRepository.findAll(
                Sort.by(Sort.Direction.ASC, "tenNcc").and(Sort.by(Sort.Direction.ASC, "id"))));
        model.addAttribute("skuOptions", nhaCungCapSkuService.layDanhSachSku());
        model.addAttribute("skuAttributeDefinitions", nhaCungCapSkuService.layDanhSachThuocTinhBienThe());
        model.addAttribute("nccMacDinhId", nccId);
        model.addAttribute("currentView", "ncc-sku");
        return "admin/nha-cung-cap-sku/index";
    }
}
