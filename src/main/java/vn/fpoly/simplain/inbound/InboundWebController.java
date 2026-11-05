package vn.fpoly.simplain.inbound;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.KichThuoc;
import vn.fpoly.simplain.entity.MauSac;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.KichThuocRepository;
import vn.fpoly.simplain.repository.MauSacRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/lay-hang")
public class InboundWebController {

    private final NhaCungCapRepository nhaCungCapRepository;
    private final PhieuNhapKhoRepository phieuNhapKhoRepository;
    private final BienTheSanPhamRepository bienTheSanPhamRepository;
    private final SanPhamRepository sanPhamRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;

    @Autowired
    public InboundWebController(NhaCungCapRepository nhaCungCapRepository,
                                PhieuNhapKhoRepository phieuNhapKhoRepository,
                                BienTheSanPhamRepository bienTheSanPhamRepository,
                                SanPhamRepository sanPhamRepository,
                                MauSacRepository mauSacRepository,
                                KichThuocRepository kichThuocRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.phieuNhapKhoRepository = phieuNhapKhoRepository;
        this.bienTheSanPhamRepository = bienTheSanPhamRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
    }

    public record BienTheDetailDto(
            Long id,
            String maSku,
            Long sanPhamId,
            String tenSanPham,
            String tenMau,
            String tenKichThuoc,
            Integer soLuongTon
    ) {}

    @GetMapping({"", "/"})
    public String trangLayHang(Model model) {
        Map<Long, SanPham> spMap = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(SanPham::getId, sp -> sp, (a, b) -> a));
        Map<Long, MauSac> mauMap = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(MauSac::getId, m -> m, (a, b) -> a));
        Map<Long, KichThuoc> sizeMap = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(KichThuoc::getId, s -> s, (a, b) -> a));

        List<BienTheDetailDto> listBienThe = new ArrayList<>();
        for (BienTheSanPham bt : bienTheSanPhamRepository.findAll()) {
            SanPham sp = spMap.get(bt.getMaSanPhamId());
            MauSac mau = mauMap.get(bt.getMaMauSacId());
            KichThuoc size = sizeMap.get(bt.getMaKichThuocId());

            String tenSp = (sp != null) ? sp.getTenSanPham() : "Sản phẩm #" + bt.getMaSanPhamId();
            String tenMau = (mau != null) ? mau.getTenMau() : "N/A";
            String tenSize = (size != null) ? size.getTenKichThuoc() : "N/A";

            listBienThe.add(new BienTheDetailDto(
                    bt.getId(),
                    bt.getMaSku(),
                    bt.getMaSanPhamId(),
                    tenSp,
                    tenMau,
                    tenSize,
                    bt.getSoLuong() != null ? bt.getSoLuong() : 0
            ));
        }

        model.addAttribute("listNcc", nhaCungCapRepository.findAll());
        model.addAttribute("listBienThe", listBienThe);
        model.addAttribute("listPhieu", phieuNhapKhoRepository.findAll(Sort.by(Sort.Direction.DESC, "id")));
        return "admin/lay-hang/index";
    }
}
