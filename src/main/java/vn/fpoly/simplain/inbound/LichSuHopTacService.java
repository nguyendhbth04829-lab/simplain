package vn.fpoly.simplain.inbound;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class LichSuHopTacService {

    private final NhaCungCapRepository nhaCungCapRepository;
    private final LichSuHopTacNhaCungCapRepository lichSuRepository;

    public LichSuHopTacService(NhaCungCapRepository nhaCungCapRepository,
                               LichSuHopTacNhaCungCapRepository lichSuRepository) {
        this.nhaCungCapRepository = nhaCungCapRepository;
        this.lichSuRepository = lichSuRepository;
    }

    @Transactional
    public LichSuHopTacNhaCungCap ghiNhanTrangThai(NhaCungCap ncc,
                                                  TrangThaiNhaCungCap trangThaiCu,
                                                  TrangThaiNhaCungCap trangThaiMoi,
                                                  Long nhanVienId,
                                                  String tenNhanVien,
                                                  String ghiChu) {
        return lichSuRepository.save(LichSuHopTacNhaCungCap.builder()
                .nhaCungCap(ncc)
                .loaiHoatDong(trangThaiCu == null
                        ? LoaiHoatDongHopTac.TAO_HO_SO
                        : LoaiHoatDongHopTac.THAY_DOI_TRANG_THAI)
                .trangThaiTruoc(trangThaiCu)
                .trangThaiMoi(trangThaiMoi)
                .ghiChu(ghiChu)
                .maNhanVienId(nhanVienId)
                .tenNhanVien(chuanHoa(tenNhanVien))
                .build());
    }

    @Transactional
    public LichSuHopTacNhaCungCap themHoatDong(Long nccId, LichSuHopTacRequest request,
                                               Long nhanVienId, String tenNhanVien) {
        if (request == null || request.getLoaiHoatDong() == null
                || !request.getLoaiHoatDong().laHoatDongThuCong()) {
            throw new IllegalArgumentException("Loại hoạt động hợp tác không hợp lệ");
        }
        String ghiChu = chuanHoa(request.getGhiChu());
        String ketQua = chuanHoa(request.getKetQua());
        if (ketQua != null && ketQua.length() > 100) {
            throw new IllegalArgumentException("Kết quả không được vượt quá 100 ký tự");
        }
        if (ghiChu == null && ketQua == null) {
            throw new IllegalArgumentException("Hãy nhập ghi chú hoặc kết quả hoạt động");
        }

        NhaCungCap ncc = nhaCungCapRepository.findById(nccId)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy Nhà cung cấp có ID: " + nccId));
        return lichSuRepository.save(LichSuHopTacNhaCungCap.builder()
                .nhaCungCap(ncc)
                .loaiHoatDong(request.getLoaiHoatDong())
                .ghiChu(ghiChu)
                .ketQua(ketQua)
                .maNhanVienId(nhanVienId)
                .tenNhanVien(chuanHoa(tenNhanVien))
                .build());
    }

    @Transactional(readOnly = true)
    public Optional<LichSuHopTacNhaCungCap> layMoiNhatTheoNhaCungCap(Long nccId) {
        return lichSuRepository.findFirstByNhaCungCap_IdOrderByNgayTaoDescIdDesc(nccId);
    }

    private String chuanHoa(String value) {
        if (value == null) return null;
        String result = value.trim();
        return result.isEmpty() ? null : result;
    }
}
