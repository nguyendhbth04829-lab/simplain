package vn.fpoly.simplain.inbound;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BienTheSanPhamThuocTinh;
import vn.fpoly.simplain.entity.HinhAnh;
import vn.fpoly.simplain.entity.ThuocTinhBienThe;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.BienTheSanPhamThuocTinhRepository;
import vn.fpoly.simplain.repository.HinhAnhRepository;
import vn.fpoly.simplain.repository.KichThuocRepository;
import vn.fpoly.simplain.repository.MauSacRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;
import vn.fpoly.simplain.repository.ThuocTinhBienTheRepository;

import java.time.LocalDateTime;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class NhaCungCapSkuService {
    private final NhaCungCapRepository nhaCungCapRepository;
    private final BienTheSanPhamRepository bienTheRepository;
    private final NhaCungCapSkuRepository lienKetRepository;
    private final SanPhamRepository sanPhamRepository;
    private final HinhAnhRepository hinhAnhRepository;
    private final ThuocTinhBienTheRepository thuocTinhRepository;
    private final BienTheSanPhamThuocTinhRepository bienTheThuocTinhRepository;
    private final MauSacRepository mauSacRepository;
    private final KichThuocRepository kichThuocRepository;

    public NhaCungCapSkuService(NhaCungCapRepository nhaCungCapRepository,
                                BienTheSanPhamRepository bienTheRepository,
                                NhaCungCapSkuRepository lienKetRepository,
                                SanPhamRepository sanPhamRepository,
                                HinhAnhRepository hinhAnhRepository,
                                ThuocTinhBienTheRepository thuocTinhRepository,
                                BienTheSanPhamThuocTinhRepository bienTheThuocTinhRepository,
                                MauSacRepository mauSacRepository,
                                KichThuocRepository kichThuocRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.bienTheRepository = bienTheRepository;
        this.lienKetRepository = lienKetRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.hinhAnhRepository = hinhAnhRepository;
        this.thuocTinhRepository = thuocTinhRepository;
        this.bienTheThuocTinhRepository = bienTheThuocTinhRepository;
        this.mauSacRepository = mauSacRepository;
        this.kichThuocRepository = kichThuocRepository;
    }

    @Transactional(readOnly = true)
    public List<NhaCungCapSkuOption> layDanhSachSku() {
        var bienThes = bienTheRepository.findAll(org.springframework.data.domain.Sort.by(org.springframework.data.domain.Sort.Direction.ASC, "maSku"));
        var sanPhams = sanPhamRepository.findAll().stream()
                .collect(Collectors.toMap(vn.fpoly.simplain.entity.SanPham::getId, Function.identity(), (a, b) -> a));
        List<Long> sanPhamIds = bienThes.stream().map(vn.fpoly.simplain.entity.BienTheSanPham::getMaSanPhamId)
                .filter(java.util.Objects::nonNull).distinct().toList();
        List<Long> bienTheIds = bienThes.stream().map(vn.fpoly.simplain.entity.BienTheSanPham::getId)
                .filter(java.util.Objects::nonNull).toList();
        Map<Long, String> anhSanPhamTheoId = sanPhamIds.isEmpty() ? Map.of() : hinhAnhRepository
                .findByMaSanPhamIdInAndMaBienTheIdIsNullOrderByLaAnhDaiDienDescThuTuAscIdAsc(sanPhamIds).stream()
                .filter(anh -> anh.getDuongDanAnh() != null && !anh.getDuongDanAnh().isBlank())
                .collect(Collectors.toMap(HinhAnh::getMaSanPhamId, HinhAnh::getDuongDanAnh, (a, b) -> a,
                        java.util.LinkedHashMap::new));
        Map<Long, String> anhSkuTheoId = bienTheIds.isEmpty() ? Map.of() : hinhAnhRepository
                .findByMaBienTheIdInOrderByLaAnhDaiDienDescThuTuAscIdAsc(bienTheIds).stream()
                .filter(anh -> anh.getDuongDanAnh() != null && !anh.getDuongDanAnh().isBlank())
                .collect(Collectors.toMap(HinhAnh::getMaBienTheId, HinhAnh::getDuongDanAnh, (a, b) -> a,
                        java.util.LinkedHashMap::new));
        List<ThuocTinhBienThe> thuocTinhs = thuocTinhRepository.findByKichHoatTrueOrderByThuTuAscIdAsc();
        Map<Long, Map<Long, String>> giaTriTheoSku = new java.util.HashMap<>();
        if (!bienTheIds.isEmpty()) {
            for (BienTheSanPhamThuocTinh giaTri : bienTheThuocTinhRepository.findByBienThe_IdIn(bienTheIds)) {
                giaTriTheoSku.computeIfAbsent(giaTri.getBienThe().getId(), ignored -> new java.util.HashMap<>())
                        .put(giaTri.getThuocTinh().getId(), giaTri.getGiaTri());
            }
        }
        var mauSacs = mauSacRepository.findAll().stream()
                .collect(Collectors.toMap(vn.fpoly.simplain.entity.MauSac::getId, Function.identity(), (a, b) -> a));
        var kichThuocs = kichThuocRepository.findAll().stream()
                .collect(Collectors.toMap(vn.fpoly.simplain.entity.KichThuoc::getId, Function.identity(), (a, b) -> a));
        return bienThes.stream().map(bt -> {
                    var sanPham = sanPhams.get(bt.getMaSanPhamId());
                    Map<Long, String> values = giaTriTheoSku.getOrDefault(bt.getId(), Map.of());
                    List<NhaCungCapSkuThuocTinh> thuocTinh = thuocTinhs.stream()
                            .map(attribute -> new NhaCungCapSkuThuocTinh(attribute.getMaThuocTinh(),
                                    attribute.getTenThuocTinh(), layGiaTriThuocTinh(attribute, bt, values, mauSacs, kichThuocs)))
                            .toList();
                    return new NhaCungCapSkuOption(bt.getMaSanPhamId(), sanPham != null ? sanPham.getMaSanPham() : "",
                        sanPham != null ? sanPham.getTenSanPham() : "Sản phẩm",
                        anhSanPhamTheoId.get(bt.getMaSanPhamId()), bt.getMaSku(),
                        bt.getId(), anhSkuTheoId.getOrDefault(bt.getId(), anhSanPhamTheoId.get(bt.getMaSanPhamId())), thuocTinh,
                        bt.getSoLuong() == null ? 0 : bt.getSoLuong(), Boolean.TRUE.equals(bt.getKichHoat()));
                })
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ThuocTinhBienThe> layDanhSachThuocTinhBienThe() {
        return thuocTinhRepository.findByKichHoatTrueOrderByThuTuAscIdAsc();
    }

    private String layGiaTriThuocTinh(ThuocTinhBienThe attribute,
                                      vn.fpoly.simplain.entity.BienTheSanPham bienThe,
                                      Map<Long, String> values,
                                      Map<Long, vn.fpoly.simplain.entity.MauSac> mauSacs,
                                      Map<Long, vn.fpoly.simplain.entity.KichThuoc> kichThuocs) {
        if ("mauSac".equalsIgnoreCase(attribute.getMaThuocTinh())) {
            return mauSacs.containsKey(bienThe.getMaMauSacId())
                    ? mauSacs.get(bienThe.getMaMauSacId()).getTenMau() : "—";
        }
        if ("kichThuoc".equalsIgnoreCase(attribute.getMaThuocTinh())) {
            return kichThuocs.containsKey(bienThe.getMaKichThuocId())
                    ? kichThuocs.get(bienThe.getMaKichThuocId()).getTenKichThuoc() : "—";
        }
        return values.getOrDefault(attribute.getId(), "—");
    }

    @Transactional(readOnly = true)
    public List<String> layMaSku(Long nhaCungCapId) {
        kiemTraNhaCungCap(nhaCungCapId);
        return lienKetRepository.findByNhaCungCapId(nhaCungCapId).stream()
                .map(link -> link.getBienThe().getMaSku())
                .sorted(String.CASE_INSENSITIVE_ORDER)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NhaCungCapSkuOption> layDanhSachSkuDuocPhanCong(Long nhaCungCapId) {
        var nhaCungCap = nhaCungCapRepository.findById(nhaCungCapId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà cung cấp có ID: " + nhaCungCapId));
        if (nhaCungCap.getTrangThaiHopTac() != TrangThaiNhaCungCap.DANG_HOP_TAC) {
            throw new IllegalArgumentException("Chỉ nhà cung cấp đang hợp tác mới được tạo đơn mua");
        }
        var duocPhanCong = new LinkedHashSet<>(layMaSku(nhaCungCapId).stream()
                .map(sku -> sku.toUpperCase(Locale.ROOT)).toList());
        return layDanhSachSku().stream()
                .filter(NhaCungCapSkuOption::kichHoat)
                .filter(sku -> duocPhanCong.contains(sku.maSku().toUpperCase(Locale.ROOT)))
                .toList();
    }

    @Transactional
    public List<String> capNhatLienKet(Long nhaCungCapId, NhaCungCapSkuRequest request) {
        kiemTraNhaCungCap(nhaCungCapId);
        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(nhaCungCapId).orElseThrow();
        if (nhaCungCap.getTrangThaiHopTac() != TrangThaiNhaCungCap.DANG_HOP_TAC) {
            throw new IllegalArgumentException("Chỉ nhà cung cấp đang hợp tác mới được phân công SKU");
        }
        if (request == null || request.getMaSku() == null) {
            throw new IllegalArgumentException("Danh sách SKU không được để trống dữ liệu");
        }

        LinkedHashSet<String> skuCanCo = request.getMaSku().stream()
                .map(String::trim)
                .map(sku -> sku.toUpperCase(Locale.ROOT))
                .collect(Collectors.toCollection(LinkedHashSet::new));
        Map<String, NhaCungCapSku> dangLienKet = lienKetRepository.findByNhaCungCapId(nhaCungCapId).stream()
                .collect(Collectors.toMap(link -> link.getBienThe().getMaSku().toUpperCase(Locale.ROOT), Function.identity()));

        dangLienKet.forEach((sku, link) -> {
            if (!skuCanCo.contains(sku)) lienKetRepository.delete(link);
        });
        for (String sku : skuCanCo) {
            if (dangLienKet.containsKey(sku)) continue;
            var bienThe = bienTheRepository.findByMaSkuIgnoreCase(sku)
                    .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy SKU: " + sku));
            lienKetRepository.save(NhaCungCapSku.builder()
                    .nhaCungCap(nhaCungCapRepository.getReferenceById(nhaCungCapId))
                    .bienThe(bienThe)
                    .ngayLienKet(LocalDateTime.now())
                    .build());
        }
        return layMaSku(nhaCungCapId);
    }

    private void kiemTraNhaCungCap(Long id) {
        if (id == null || !nhaCungCapRepository.existsById(id)) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp có ID: " + id);
        }
    }
}
