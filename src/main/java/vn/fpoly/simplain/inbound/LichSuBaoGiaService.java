package vn.fpoly.simplain.inbound;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.BienTheSanPham;
import vn.fpoly.simplain.entity.GiaBanBienTheLichSu;
import vn.fpoly.simplain.entity.SanPham;
import vn.fpoly.simplain.repository.BienTheSanPhamRepository;
import vn.fpoly.simplain.repository.GiaBanBienTheLichSuRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class LichSuBaoGiaService {
    private static final DateTimeFormatter DATE_TIME_FORMAT = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final NhaCungCapRepository nhaCungCapRepository;
    private final BienTheSanPhamRepository bienTheRepository;
    private final SanPhamRepository sanPhamRepository;
    private final NhaCungCapSkuRepository nhaCungCapSkuRepository;
    private final NhaCungCapSkuService nhaCungCapSkuService;
    private final BaoGiaNhaCungCapSkuRepository baoGiaRepository;
    private final GiaBanBienTheLichSuRepository giaBanRepository;

    public LichSuBaoGiaService(NhaCungCapRepository nhaCungCapRepository,
                               BienTheSanPhamRepository bienTheRepository,
                               SanPhamRepository sanPhamRepository,
                               NhaCungCapSkuRepository nhaCungCapSkuRepository,
                               NhaCungCapSkuService nhaCungCapSkuService,
                               BaoGiaNhaCungCapSkuRepository baoGiaRepository,
                               GiaBanBienTheLichSuRepository giaBanRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.bienTheRepository = bienTheRepository;
        this.sanPhamRepository = sanPhamRepository;
        this.nhaCungCapSkuRepository = nhaCungCapSkuRepository;
        this.nhaCungCapSkuService = nhaCungCapSkuService;
        this.baoGiaRepository = baoGiaRepository;
        this.giaBanRepository = giaBanRepository;
    }

    @Transactional(readOnly = true)
    public List<BangGiaResponse.SanPhamOption> laySanPham(Long nhaCungCapId) {
        kiemTraNhaCungCap(nhaCungCapId);
        Set<Long> sanPhamIds = new HashSet<>();
        nhaCungCapSkuRepository.findByNhaCungCapId(nhaCungCapId).stream()
                .map(link -> link.getBienThe().getMaSanPhamId()).forEach(sanPhamIds::add);
        sanPhamIds.addAll(baoGiaRepository.findDistinctSanPhamIdsByNhaCungCapId(nhaCungCapId));
        return sanPhamRepository.findAllById(sanPhamIds).stream()
                .sorted(java.util.Comparator.comparing(SanPham::getTenSanPham,
                        String.CASE_INSENSITIVE_ORDER).thenComparing(SanPham::getId))
                .map(sp -> new BangGiaResponse.SanPhamOption(sp.getId(), sp.getMaSanPham(), sp.getTenSanPham()))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<BangGiaResponse.SkuOption> laySku(Long nhaCungCapId, Long sanPhamId) {
        kiemTraNhaCungCap(nhaCungCapId);
        if (sanPhamId == null || !sanPhamRepository.existsById(sanPhamId)) {
            throw new IllegalArgumentException("Không tìm thấy sản phẩm được chọn");
        }
        java.util.Map<Long, BienTheSanPham> skus = new java.util.LinkedHashMap<>();
        nhaCungCapSkuRepository.findByNhaCungCapId(nhaCungCapId).stream()
                .map(NhaCungCapSku::getBienThe)
                .filter(bt -> sanPhamId.equals(bt.getMaSanPhamId()))
                .forEach(bt -> skus.put(bt.getId(), bt));
        baoGiaRepository.findBienTheByNhaCungCapIdAndSanPhamId(nhaCungCapId, sanPhamId)
                .forEach(bt -> skus.put(bt.getId(), bt));
        return skus.values().stream()
                .sorted(java.util.Comparator.comparing(BienTheSanPham::getMaSku,
                        String.CASE_INSENSITIVE_ORDER).thenComparing(BienTheSanPham::getId))
                .map(bt -> new BangGiaResponse.SkuOption(bt.getId(), bt.getMaSku(), Boolean.TRUE.equals(bt.getKichHoat())))
                .toList();
    }

    @Transactional(readOnly = true)
    public List<NhaCungCapSkuOption> laySkuDeChon(Long nhaCungCapId) {
        kiemTraNhaCungCap(nhaCungCapId);
        Set<String> maSkuDuocXem = new HashSet<>();
        nhaCungCapSkuRepository.findByNhaCungCapId(nhaCungCapId).stream()
                .map(link -> link.getBienThe().getMaSku())
                .filter(java.util.Objects::nonNull)
                .map(sku -> sku.toUpperCase(java.util.Locale.ROOT))
                .forEach(maSkuDuocXem::add);
        baoGiaRepository.findDistinctMaSkuByNhaCungCapId(nhaCungCapId).stream()
                .filter(java.util.Objects::nonNull)
                .map(sku -> sku.toUpperCase(java.util.Locale.ROOT))
                .forEach(maSkuDuocXem::add);
        return nhaCungCapSkuService.layDanhSachSku().stream()
                .filter(option -> maSkuDuocXem.contains(option.maSku().toUpperCase(java.util.Locale.ROOT)))
                .sorted(java.util.Comparator.comparing(NhaCungCapSkuOption::tenSanPham, String.CASE_INSENSITIVE_ORDER)
                        .thenComparing(NhaCungCapSkuOption::maSku, String.CASE_INSENSITIVE_ORDER))
                .toList();
    }

    @Transactional(readOnly = true)
    public BangGiaResponse layLichSu(Long nhaCungCapId, Long bienTheId) {
        kiemTraNhaCungCap(nhaCungCapId);
        BienTheSanPham bienThe = bienTheRepository.findById(bienTheId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy SKU được chọn"));
        if (!nhaCungCapSkuRepository.existsByNhaCungCapIdAndBienTheId(nhaCungCapId, bienTheId)
                && !baoGiaRepository.existsByNhaCungCap_IdAndBienThe_Id(nhaCungCapId, bienTheId)) {
            throw new IllegalArgumentException("SKU này chưa được phân công hoặc báo giá từ nhà cung cấp đã chọn");
        }
        List<BangGiaResponse.DiemBaoGia> giaMua = baoGiaRepository
                .findAllByNhaCungCap_IdAndBienThe_IdOrderByThoiDiemBaoGiaAscIdAsc(nhaCungCapId, bienTheId).stream()
                .map(gia -> new BangGiaResponse.DiemBaoGia(gia.getId(), gia.getThoiDiemBaoGia().format(DATE_TIME_FORMAT),
                        gia.getGiaBao(), gia.getGhiChu()))
                .toList();
        List<BangGiaResponse.DiemGiaBan> giaBan = giaBanRepository
                .findAllByBienThe_IdOrderByThoiDiemAscIdAsc(bienTheId).stream()
                .map(gia -> new BangGiaResponse.DiemGiaBan(gia.getId(), gia.getThoiDiem().format(DATE_TIME_FORMAT),
                        gia.getGiaBan(), gia.getLoaiSuKien()))
                .toList();
        return new BangGiaResponse(giaMua, giaBan);
    }

    @Transactional
    public BangGiaResponse.DiemBaoGia ghiNhanBaoGia(LichSuBaoGiaRequest request) {
        NhaCungCap nhaCungCap = nhaCungCapRepository.findById(request.nhaCungCapId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhà cung cấp được chọn"));
        BienTheSanPham bienThe = bienTheRepository.findById(request.bienTheId())
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy SKU được chọn"));
        if (request.thoiDiemBaoGia().isAfter(LocalDateTime.now())) {
            throw new IllegalArgumentException("Thời điểm nhận báo giá không được ở tương lai");
        }
        String ghiChu = request.ghiChu() == null || request.ghiChu().isBlank() ? null : request.ghiChu().trim();
        BaoGiaNhaCungCapSku baoGia = baoGiaRepository.save(BaoGiaNhaCungCapSku.builder()
                .nhaCungCap(nhaCungCap)
                .bienThe(bienThe)
                .giaBao(request.giaBao())
                .thoiDiemBaoGia(request.thoiDiemBaoGia())
                .ghiChu(ghiChu)
                .build());
        return new BangGiaResponse.DiemBaoGia(baoGia.getId(), baoGia.getThoiDiemBaoGia().format(DATE_TIME_FORMAT),
                baoGia.getGiaBao(), baoGia.getGhiChu());
    }

    @Transactional
    public void ghiNhanGiaThoaThuan(NhaCungCap nhaCungCap, BienTheSanPham bienThe,
                                    BigDecimal giaThoaThuan, String maDon) {
        if (nhaCungCap == null || nhaCungCap.getId() == null || bienThe == null || bienThe.getId() == null
                || giaThoaThuan == null || giaThoaThuan.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Không thể ghi lịch sử khi thông tin giá thỏa thuận chưa hợp lệ");
        }
        baoGiaRepository.save(BaoGiaNhaCungCapSku.builder()
                .nhaCungCap(nhaCungCap)
                .bienThe(bienThe)
                .giaBao(giaThoaThuan)
                .thoiDiemBaoGia(LocalDateTime.now())
                .ghiChu("Giá thỏa thuận từ đơn mua " + maDon)
                .build());
    }

    @Transactional
    public void ghiGiaBanBanDau(BienTheSanPham bienThe) {
        if (bienThe.getId() == null || bienThe.getGiaBan() == null) return;
        if (giaBanRepository.findAllByBienThe_IdOrderByThoiDiemAscIdAsc(bienThe.getId()).isEmpty()) {
            giaBanRepository.save(GiaBanBienTheLichSu.builder()
                    .bienThe(bienThe)
                    .giaBan(bienThe.getGiaBan())
                    .thoiDiem(LocalDateTime.now())
                    .loaiSuKien("KHOI_TAO")
                    .build());
        }
    }

    @Transactional
    public void ghiGiaBanNeuThayDoi(BienTheSanPham bienThe, BigDecimal giaBanCu) {
        BigDecimal giaBanMoi = bienThe.getGiaBan();
        if (bienThe.getId() == null || giaBanMoi == null || giaBanCu == null
                || giaBanCu.compareTo(giaBanMoi) == 0) return;
        giaBanRepository.save(GiaBanBienTheLichSu.builder()
                .bienThe(bienThe)
                .giaBan(giaBanMoi)
                .thoiDiem(LocalDateTime.now())
                .loaiSuKien("DIEU_CHINH")
                .build());
    }

    @Transactional
    public void ghiMocBanDauChoSkuHienCo() {
        Set<Long> daCoLichSu = new HashSet<>(giaBanRepository.findDistinctBienTheIds());
        LocalDateTime mocGhiNhan = LocalDateTime.now();
        List<GiaBanBienTheLichSu> mocMoi = bienTheRepository.findAll().stream()
                .filter(bt -> !daCoLichSu.contains(bt.getId()) && bt.getGiaBan() != null)
                .map(bt -> GiaBanBienTheLichSu.builder()
                        .bienThe(bt).giaBan(bt.getGiaBan()).thoiDiem(mocGhiNhan).loaiSuKien("MOC_BAN_DAU").build())
                .toList();
        if (!mocMoi.isEmpty()) giaBanRepository.saveAll(mocMoi);
    }

    private void kiemTraNhaCungCap(Long nhaCungCapId) {
        if (nhaCungCapId == null || !nhaCungCapRepository.existsById(nhaCungCapId)) {
            throw new IllegalArgumentException("Không tìm thấy nhà cung cấp được chọn");
        }
    }
}
