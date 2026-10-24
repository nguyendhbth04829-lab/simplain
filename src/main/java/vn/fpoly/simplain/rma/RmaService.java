package vn.fpoly.simplain.rma;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import vn.fpoly.simplain.entity.ChiTietDonHang;
import vn.fpoly.simplain.entity.ChiTietSanPhamDoiTra;
import vn.fpoly.simplain.entity.DonHang;
import vn.fpoly.simplain.entity.KhachHang;
import vn.fpoly.simplain.entity.YeuCauDoiTra;
import vn.fpoly.simplain.repository.ChiTietDonHangRepository;
import vn.fpoly.simplain.repository.ChiTietSanPhamDoiTraRepository;
import vn.fpoly.simplain.repository.DonHangRepository;
import vn.fpoly.simplain.repository.KhachHangRepository;
import vn.fpoly.simplain.repository.YeuCauDoiTraRepository;
import vn.fpoly.simplain.rma.dto.CreateRmaItemRequest;
import vn.fpoly.simplain.rma.dto.CreateRmaRequest;
import vn.fpoly.simplain.rma.dto.ReviewRmaRequest;
import vn.fpoly.simplain.rma.dto.RmaItemResponse;
import vn.fpoly.simplain.rma.dto.RmaResponse;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * Service xử lý nghiệp vụ Đổi/Trả hàng (RMA) thuộc Module 5 (US24, US25).
 */
@Service
public class RmaService {

    private final YeuCauDoiTraRepository yeuCauDoiTraRepository;
    private final ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository;
    private final DonHangRepository donHangRepository;
    private final ChiTietDonHangRepository chiTietDonHangRepository;
    private final KhachHangRepository khachHangRepository;

    public RmaService(YeuCauDoiTraRepository yeuCauDoiTraRepository,
                      ChiTietSanPhamDoiTraRepository chiTietSanPhamDoiTraRepository,
                      DonHangRepository donHangRepository,
                      ChiTietDonHangRepository chiTietDonHangRepository,
                      KhachHangRepository khachHangRepository) {
        this.yeuCauDoiTraRepository = yeuCauDoiTraRepository;
        this.chiTietSanPhamDoiTraRepository = chiTietSanPhamDoiTraRepository;
        this.donHangRepository = donHangRepository;
        this.chiTietDonHangRepository = chiTietDonHangRepository;
        this.khachHangRepository = khachHangRepository;
    }

    /**
     * Khách hàng tạo mới yêu cầu Đổi/Trả hàng (RMA) online (US24).
     */
    @Transactional
    public RmaResponse createRma(CreateRmaRequest request) {
        DonHang donHang = donHangRepository.findById(request.getDonHangId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy đơn hàng có ID: " + request.getDonHangId()));

        KhachHang khachHang = khachHangRepository.findById(request.getKhachHangId())
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy khách hàng có ID: " + request.getKhachHangId()));

        // Sinh mã yêu cầu RMA: RMA-yyMMdd-XXXXXX
        String maYeuCauRma = generateRmaCode();

        YeuCauDoiTra rma = YeuCauDoiTra.builder()
                .maYeuCauRma(maYeuCauRma)
                .donHang(donHang)
                .khachHang(khachHang)
                .loaiDoiTra(request.getLoaiDoiTra().trim().toUpperCase())
                .lyDo(request.getLyDo().trim())
                .soTienHoan(request.getSoTienHoan())
                .thongTinNganHang(request.getThongTinNganHang())
                .trangThai("CHO_DUYET")
                .ngayTao(LocalDateTime.now())
                .build();

        rma = yeuCauDoiTraRepository.save(rma);

        List<ChiTietSanPhamDoiTra> chiTietRmaList = new ArrayList<>();
        for (CreateRmaItemRequest itemReq : request.getItems()) {
            ChiTietDonHang chiTietDonHang = chiTietDonHangRepository.findById(itemReq.getChiTietDonHangId())
                    .orElseThrow(() -> new ResponseStatusException(
                            HttpStatus.NOT_FOUND,
                            "Không tìm thấy dòng chi tiết đơn hàng ID: " + itemReq.getChiTietDonHangId()));

            if (!chiTietDonHang.getDonHang().getId().equals(donHang.getId())) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Sản phẩm ID " + itemReq.getChiTietDonHangId() + " không thuộc về đơn hàng " + donHang.getMaDonHang());
            }

            if (itemReq.getSoLuongTra() > chiTietDonHang.getSoLuong()) {
                throw new ResponseStatusException(
                        HttpStatus.BAD_REQUEST,
                        "Số lượng đổi/trả (" + itemReq.getSoLuongTra() + ") vượt quá số lượng đã mua (" + chiTietDonHang.getSoLuong() + ")");
            }

            ChiTietSanPhamDoiTra chiTietRma = ChiTietSanPhamDoiTra.builder()
                    .yeuCauDoiTra(rma)
                    .chiTietDonHang(chiTietDonHang)
                    .soLuongTra(itemReq.getSoLuongTra())
                    .maBienTheMoiId(itemReq.getMaBienTheMoiId())
                    .tinhTrangSanPham(itemReq.getTinhTrangSanPham())
                    .build();

            chiTietRmaList.add(chiTietRma);
        }

        chiTietSanPhamDoiTraRepository.saveAll(chiTietRmaList);

        return toRmaResponse(rma);
    }

    /**
     * CSKH duyệt hoặc từ chối yêu cầu Đổi/Trả hàng (US25).
     */
    @Transactional
    public RmaResponse reviewRma(String maYeuCauRma, ReviewRmaRequest request) {
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaYeuCauRma(maYeuCauRma)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu RMA: " + maYeuCauRma));

        rma.setTrangThai(request.getTrangThai().trim().toUpperCase());
        rma.setNguoiDuyetId(request.getNguoiDuyetId());

        if (request.getGhiChuKiemDinh() != null && !request.getGhiChuKiemDinh().trim().isEmpty()) {
            rma.setGhiChuKiemDinh(request.getGhiChuKiemDinh().trim());
        }

        rma = yeuCauDoiTraRepository.save(rma);

        return toRmaResponse(rma);
    }

    /**
     * Xem chi tiết một yêu cầu RMA theo mã.
     */
    @Transactional(readOnly = true)
    public RmaResponse getRmaByCode(String maYeuCauRma) {
        YeuCauDoiTra rma = yeuCauDoiTraRepository.findByMaYeuCauRma(maYeuCauRma)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Không tìm thấy yêu cầu RMA: " + maYeuCauRma));
        return toRmaResponse(rma);
    }

    /**
     * Lấy danh sách yêu cầu RMA theo khách hàng (US24).
     */
    @Transactional(readOnly = true)
    public List<RmaResponse> getRmaByCustomer(Long khachHangId) {
        return yeuCauDoiTraRepository.findByKhachHangIdOrderByNgayTaoDesc(khachHangId)
                .stream()
                .map(this::toRmaResponse)
                .toList();
    }

    /**
     * Lấy toàn bộ danh sách yêu cầu RMA cho CSKH xem xét (US25).
     */
    @Transactional(readOnly = true)
    public List<RmaResponse> getAllRma(String trangThai) {
        List<YeuCauDoiTra> list;
        if (trangThai != null && !trangThai.trim().isEmpty()) {
            list = yeuCauDoiTraRepository.findByTrangThaiOrderByNgayTaoDesc(trangThai.trim().toUpperCase());
        } else {
            list = yeuCauDoiTraRepository.findAllByOrderByNgayTaoDesc();
        }
        return list.stream().map(this::toRmaResponse).toList();
    }

    /**
     * Chuyển đổi entity YeuCauDoiTra sang DTO RmaResponse.
     */
    private RmaResponse toRmaResponse(YeuCauDoiTra rma) {
        List<ChiTietSanPhamDoiTra> chiTiets = chiTietSanPhamDoiTraRepository.findByYeuCauDoiTraId(rma.getId());
        List<RmaItemResponse> itemResponses = chiTiets.stream().map(ct -> RmaItemResponse.builder()
                .id(ct.getId())
                .chiTietDonHangId(ct.getChiTietDonHang() != null ? ct.getChiTietDonHang().getId() : null)
                .tenSanPham(ct.getChiTietDonHang() != null ? ct.getChiTietDonHang().getTenSanPham() : null)
                .maSku(ct.getChiTietDonHang() != null ? ct.getChiTietDonHang().getMaSku() : null)
                .donGia(ct.getChiTietDonHang() != null ? ct.getChiTietDonHang().getDonGia() : null)
                .soLuongTra(ct.getSoLuongTra())
                .maBienTheMoiId(ct.getMaBienTheMoiId())
                .tinhTrangSanPham(ct.getTinhTrangSanPham())
                .build()).toList();

        return RmaResponse.builder()
                .id(rma.getId())
                .maYeuCauRma(rma.getMaYeuCauRma())
                .donHangId(rma.getDonHang() != null ? rma.getDonHang().getId() : null)
                .maDonHang(rma.getDonHang() != null ? rma.getDonHang().getMaDonHang() : null)
                .khachHangId(rma.getKhachHang() != null ? rma.getKhachHang().getId() : null)
                .tenKhachHang(rma.getKhachHang() != null ? rma.getKhachHang().getHoTen() : null)
                .loaiDoiTra(rma.getLoaiDoiTra())
                .lyDo(rma.getLyDo())
                .soTienHoan(rma.getSoTienHoan())
                .thongTinNganHang(rma.getThongTinNganHang())
                .trangThai(rma.getTrangThai())
                .nguoiDuyetId(rma.getNguoiDuyetId())
                .ghiChuKiemDinh(rma.getGhiChuKiemDinh())
                .ngayTao(rma.getNgayTao())
                .items(itemResponses)
                .build();
    }

    private String generateRmaCode() {
        String datePart = LocalDate.now().format(DateTimeFormatter.ofPattern("yyMMdd"));
        String randomPart = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        return "RMA" + datePart + "-" + randomPart;
    }
}
