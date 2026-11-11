package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.dto.DangNhapNhanVienRequest;
import vn.fpoly.simplain.dto.NhanVienRequest;
import vn.fpoly.simplain.dto.XacThucNhanVienResponse;
import vn.fpoly.simplain.entity.NhanVien;
import vn.fpoly.simplain.entity.VaiTro;
import vn.fpoly.simplain.entity.VaiTroQuyenHan;
import vn.fpoly.simplain.enums.ChucNangHeThong;
import vn.fpoly.simplain.repository.NhanVienRepository;
import vn.fpoly.simplain.repository.VaiTroQuyenHanRepository;
import vn.fpoly.simplain.repository.VaiTroRepository;
import vn.fpoly.simplain.util.JwtUtil;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Service xử lý các nghiệp vụ Xác thực Nhân viên CMS (US02) và Quản lý danh sách Nhân sự CRUD (US04).
 */
@Service
public class NhanVienService {

    private final NhanVienRepository nhanVienRepository;
    private final VaiTroRepository vaiTroRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final JdbcTemplate jdbcTemplate;
    private final VaiTroQuyenHanRepository vaiTroQuyenHanRepository;

    @Autowired
    public NhanVienService(NhanVienRepository nhanVienRepository,
                           VaiTroRepository vaiTroRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           JdbcTemplate jdbcTemplate,
                           @Autowired(required = false) VaiTroQuyenHanRepository vaiTroQuyenHanRepository) {
        this.nhanVienRepository = nhanVienRepository;
        this.vaiTroRepository = vaiTroRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
        this.jdbcTemplate = jdbcTemplate;
        this.vaiTroQuyenHanRepository = vaiTroQuyenHanRepository;
    }

    public NhanVienService(NhanVienRepository nhanVienRepository,
                           VaiTroRepository vaiTroRepository,
                           PasswordEncoder passwordEncoder,
                           JwtUtil jwtUtil,
                           JdbcTemplate jdbcTemplate) {
        this(nhanVienRepository, vaiTroRepository, passwordEncoder, jwtUtil, jdbcTemplate, null);
    }

    // =========================================================================
    // PHẦN 1: NGHIỆP VỤ XÁC THỰC VÀ TOKEN NHÂN VIÊN CMS (US02)
    // =========================================================================

    /**
     * Nghiệp vụ đăng nhập vào hệ thống quản trị CMS dành cho Nhân viên.
     *
     * <p>Tiêu chí chấp nhận (Acceptance Criteria):
     * 1. Check phân quyền (Role-based).
     * 2. Từ chối đăng nhập nếu trạng thái nhân viên bị khóa.
     *
     * @param request dữ liệu đăng nhập của nhân viên
     * @return kết quả xác thực kèm JWT Token và vai trò
     */
    public XacThucNhanVienResponse dangNhap(DangNhapNhanVienRequest request) {
        String input = request.getTenDangNhap().trim();

        // 1. Kiểm tra tài khoản nhân viên có tồn tại trong hệ thống không (hỗ trợ cả username, mã nhân viên và email)
        Optional<NhanVien> nhanVienOpt = nhanVienRepository.findByTenDangNhap(input);
        if (nhanVienOpt.isEmpty()) {
            nhanVienOpt = nhanVienRepository.findByTenDangNhapIgnoreCase(input);
        }
        if (nhanVienOpt.isEmpty()) {
            nhanVienOpt = nhanVienRepository.findByMaNhanVienIgnoreCase(input);
        }
        if (nhanVienOpt.isEmpty()) {
            nhanVienOpt = nhanVienRepository.findByEmailIgnoreCase(input);
        }

        if (nhanVienOpt.isEmpty()) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        NhanVien nhanVien = nhanVienOpt.get();

        // 2. Tiêu chí cốt lõi 2 (US02 & US04): Từ chối đăng nhập nếu trạng thái nhân viên bị khóa
        if (nhanVien.getTrangThai() != null && nhanVien.getTrangThai() == 0) {
            throw new IllegalStateException("Tài khoản nhân viên đang bị khóa hoặc ngừng hoạt động. Vui lòng liên hệ Quản trị viên!");
        }

        // 3. Tiêu chí cốt lõi 1: Check phân quyền (Role-based)
        VaiTro vaiTro = nhanVien.getVaiTro();
        if (vaiTro == null) {
            throw new IllegalStateException("Tài khoản nhân viên chưa được gán vai trò hợp lệ trong hệ thống");
        }
        if (vaiTro.getTrangThai() != null && vaiTro.getTrangThai() == 0) {
            throw new IllegalStateException("Vai trò [" + vaiTro.getTenVaiTro() + "] của nhân viên đã bị vô hiệu hóa");
        }

        // 4. So khớp mật khẩu (Hỗ trợ cả BCrypt mã hóa và chuỗi băm sẵn)
        boolean khopMatKhau = false;
        String matKhauHash = nhanVien.getMatKhauHash();
        if (matKhauHash != null && (matKhauHash.startsWith("$2a$") || matKhauHash.startsWith("$2b$") || matKhauHash.startsWith("$2y$"))) {
            khopMatKhau = passwordEncoder.matches(request.getMatKhau(), matKhauHash);
        } else {
            khopMatKhau = request.getMatKhau().equals(matKhauHash);
        }

        if (!khopMatKhau) {
            throw new IllegalArgumentException("Tên đăng nhập hoặc mật khẩu không chính xác");
        }

        // 5. Cấp phát JWT Token chứa định danh và Role của nhân viên
        String token = jwtUtil.generateToken(nhanVien);
        List<String> danhSachQuyen = layDanhSachQuyenTheoVaiTro(vaiTro);

        return XacThucNhanVienResponse.builder()
                .token(token)
                .loaiToken("Bearer")
                .id(nhanVien.getId())
                .maNhanVien(nhanVien.getMaNhanVien())
                .hoTen(nhanVien.getHoTen())
                .tenDangNhap(nhanVien.getTenDangNhap())
                .maVaiTro(vaiTro.getMaVaiTro())
                .tenVaiTro(vaiTro.getTenVaiTro())
                .email(nhanVien.getEmail())
                .soDienThoai(nhanVien.getSoDienThoai())
                .trangThai(nhanVien.getTrangThai())
                .danhSachQuyen(danhSachQuyen)
                .thongBao("Đăng nhập CMS thành công với quyền [" + vaiTro.getTenVaiTro() + "]!")
                .build();
    }

    /**
     * Xác minh Token của nhân viên và lấy thông tin hồ sơ quyền hạn hiện tại.
     *
     * @param rawToken chuỗi token hoặc Bearer token
     * @return hồ sơ quyền hạn nhân viên
     */
    public XacThucNhanVienResponse layThongTinNhanVienTuToken(String rawToken) {
        String token = chuanHoaToken(rawToken);
        if (!jwtUtil.validateToken(token)) {
            throw new IllegalArgumentException("JWT Token quản trị không hợp lệ hoặc đã hết hạn");
        }

        String username = jwtUtil.extractSubject(token);
        NhanVien nhanVien = nhanVienRepository.findByTenDangNhap(username)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy thông tin nhân viên từ token"));

        if (nhanVien.getTrangThai() != null && nhanVien.getTrangThai() == 0) {
            throw new IllegalStateException("Tài khoản nhân viên hiện đang bị khóa");
        }

        VaiTro vaiTro = nhanVien.getVaiTro();
        List<String> danhSachQuyen = layDanhSachQuyenTheoVaiTro(vaiTro);

        return XacThucNhanVienResponse.builder()
                .token(token)
                .loaiToken("Bearer")
                .id(nhanVien.getId())
                .maNhanVien(nhanVien.getMaNhanVien())
                .hoTen(nhanVien.getHoTen())
                .tenDangNhap(nhanVien.getTenDangNhap())
                .maVaiTro(vaiTro != null ? vaiTro.getMaVaiTro() : "ROLE_NHAN_VIEN")
                .tenVaiTro(vaiTro != null ? vaiTro.getTenVaiTro() : "Nhân viên")
                .email(nhanVien.getEmail())
                .soDienThoai(nhanVien.getSoDienThoai())
                .trangThai(nhanVien.getTrangThai())
                .danhSachQuyen(danhSachQuyen)
                .thongBao("Token nhân viên hợp lệ")
                .build();
    }

    /**
     * Lấy danh sách mã quyền hạn theo vai trò (Dynamic RBAC).
     */
    public List<String> layDanhSachQuyenTheoVaiTro(VaiTro vaiTro) {
        if (vaiTro == null) return new ArrayList<>();
        if ("ADMIN".equalsIgnoreCase(vaiTro.getMaVaiTro())) {
            return Arrays.stream(ChucNangHeThong.values()).map(ChucNangHeThong::getMaQuyen).collect(Collectors.toList());
        }
        if (vaiTroQuyenHanRepository != null && vaiTro.getId() != null) {
            List<VaiTroQuyenHan> list = vaiTroQuyenHanRepository.findByMaVaiTroId(vaiTro.getId());
            if (!list.isEmpty()) {
                return list.stream().map(VaiTroQuyenHan::getMaQuyen).collect(Collectors.toList());
            }
        }
        // Fallback mặc định an toàn cho các vai trò chuẩn nếu chưa có cấu hình trong bảng vai_tro_quyen_han
        if ("BAN_HANG".equalsIgnoreCase(vaiTro.getMaVaiTro())) {
            return List.of("DASHBOARD", "BAN_HANG_POS", "DON_HANG", "SAN_PHAM", "KHUYEN_MAI");
        }
        if ("CSKH".equalsIgnoreCase(vaiTro.getMaVaiTro())) {
            return List.of("DASHBOARD", "DOI_TRA", "DON_HANG", "KHACH_HANG", "SAN_PHAM");
        }
        return new ArrayList<>();
    }

    /**
     * Chuẩn hóa chuỗi Token, loại bỏ tiền tố Bearer nếu có.
     */
    public String chuanHoaToken(String rawToken) {
        if (rawToken == null) {
            throw new IllegalArgumentException("Token không được để trống");
        }
        String token = rawToken.trim();
        if (token.startsWith("Bearer ") || token.startsWith("bearer ")) {
            token = token.substring(7).trim();
        }
        return token;
    }

    // =========================================================================
    // PHẦN 2: QUẢN LÝ DANH SÁCH NHÂN VIÊN CRUD & GÁN VAI TRÒ & ACTIVE/DEACTIVE (US04)
    // =========================================================================

    /**
     * Lấy danh sách nhân viên có phân trang, tìm kiếm và bộ lọc theo Vai trò, Trạng thái.
     *
     * @param tuKhoa từ khóa tìm kiếm (họ tên, mã, tên đăng nhập, email, sđt)
     * @param vaiTroId lọc theo ID vai trò (tùy chọn)
     * @param trangThai lọc theo trạng thái (tùy chọn)
     * @param pageable đối tượng phân trang
     * @return Trang danh sách nhân viên
     */
    public Page<NhanVien> layDanhSach(String tuKhoa, Long vaiTroId, Integer trangThai, Pageable pageable) {
        String kw = (tuKhoa != null && !tuKhoa.trim().isEmpty()) ? tuKhoa.trim() : null;
        return nhanVienRepository.timKiemPhanTrang(kw, vaiTroId, trangThai, pageable);
    }

    /**
     * Lấy thông tin chi tiết một nhân viên theo ID.
     *
     * @param id ID của nhân viên
     * @return Đối tượng NhanVien
     */
    public NhanVien layTheoId(Long id) {
        return nhanVienRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy nhân viên có ID = " + id));
    }

    /**
     * Thêm mới nhân viên, gán vai trò và mã hóa mật khẩu BCrypt.
     *
     * @param request dữ liệu nhân viên mới
     * @return NhanVien sau khi lưu
     */
    @Transactional
    public NhanVien them(NhanVienRequest request) {
        String maNV = request.getMaNhanVien().trim().toUpperCase();
        String tenDN = request.getTenDangNhap().trim().toLowerCase();

        // 1. Kiểm tra trùng lặp mã nhân viên
        if (nhanVienRepository.existsByMaNhanVien(maNV)) {
            throw new IllegalArgumentException("Mã nhân viên '" + maNV + "' đã tồn tại trên hệ thống");
        }

        // 2. Kiểm tra trùng lặp tên đăng nhập
        if (nhanVienRepository.existsByTenDangNhap(tenDN)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + tenDN + "' đã có người sử dụng");
        }

        // 3. Kiểm tra mật khẩu bắt buộc khi thêm mới
        if (request.getMatKhau() == null || request.getMatKhau().trim().length() < 6) {
            throw new IllegalArgumentException("Mật khẩu tạo mới cho nhân viên phải có ít nhất 6 ký tự");
        }

        // 4. Kiểm tra trùng lặp email nếu có nhập
        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty()) ? request.getEmail().trim() : null;
        if (email != null && nhanVienRepository.existsByEmail(email)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được sử dụng bởi nhân viên khác");
        }

        // 5. Kiểm tra trùng lặp số điện thoại nếu có nhập
        String sdt = (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty())
                ? request.getSoDienThoai().replaceAll("[\\s.-]", "").trim()
                : null;
        if (sdt != null && nhanVienRepository.existsBySoDienThoai(sdt)) {
            throw new IllegalArgumentException("Số điện thoại '" + sdt + "' đã được sử dụng bởi nhân viên khác");
        }

        // 6. Tiêu chí 2: Gán vai trò cho nhân viên
        VaiTro vaiTro = vaiTroRepository.findById(request.getMaVaiTroId())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò được chọn không tồn tại trong hệ thống (ID: " + request.getMaVaiTroId() + ")"));

        NhanVien nhanVien = NhanVien.builder()
                .maNhanVien(maNV)
                .hoTen(request.getHoTen().trim())
                .tenDangNhap(tenDN)
                .matKhauHash(passwordEncoder.encode(request.getMatKhau().trim()))
                .soDienThoai(sdt)
                .email(email)
                .vaiTro(vaiTro)
                .trangThai(request.getTrangThai() != null ? request.getTrangThai() : 1)
                .ngayTao(LocalDateTime.now())
                .build();

        return nhanVienRepository.save(nhanVien);
    }

    /**
     * Cập nhật thông tin nhân viên, đổi vai trò và đổi mật khẩu nếu có yêu cầu.
     *
     * @param id ID của nhân viên cần sửa
     * @param request dữ liệu cập nhật
     * @return NhanVien sau khi cập nhật
     */
    @Transactional
    public NhanVien sua(Long id, NhanVienRequest request) {
        NhanVien nhanVien = layTheoId(id);

        String maNV = request.getMaNhanVien().trim().toUpperCase();
        String tenDN = request.getTenDangNhap().trim().toLowerCase();

        // 1. Kiểm tra trùng lặp mã nhân viên (ngoại trừ ID chính mình)
        if (nhanVienRepository.existsByMaNhanVienAndIdNot(maNV, id)) {
            throw new IllegalArgumentException("Mã nhân viên '" + maNV + "' đã được sử dụng bởi nhân viên khác");
        }

        // 2. Kiểm tra trùng lặp tên đăng nhập (ngoại trừ ID chính mình)
        if (nhanVienRepository.existsByTenDangNhapAndIdNot(tenDN, id)) {
            throw new IllegalArgumentException("Tên đăng nhập '" + tenDN + "' đã có người sử dụng");
        }

        // 3. Kiểm tra email (ngoại trừ ID chính mình)
        String email = (request.getEmail() != null && !request.getEmail().trim().isEmpty()) ? request.getEmail().trim() : null;
        if (email != null && nhanVienRepository.existsByEmailAndIdNot(email, id)) {
            throw new IllegalArgumentException("Email '" + email + "' đã được sử dụng bởi nhân viên khác");
        }

        // 4. Kiểm tra số điện thoại (ngoại trừ ID chính mình)
        String sdt = (request.getSoDienThoai() != null && !request.getSoDienThoai().trim().isEmpty())
                ? request.getSoDienThoai().replaceAll("[\\s.-]", "").trim()
                : null;
        if (sdt != null && nhanVienRepository.existsBySoDienThoaiAndIdNot(sdt, id)) {
            throw new IllegalArgumentException("Số điện thoại '" + sdt + "' đã được sử dụng bởi nhân viên khác");
        }

        // 5. Tiêu chí 2: Cập nhật / Gán lại vai trò
        VaiTro vaiTro = vaiTroRepository.findById(request.getMaVaiTroId())
                .orElseThrow(() -> new IllegalArgumentException("Vai trò được chọn không tồn tại trong hệ thống"));

        // 6. Cập nhật mật khẩu nếu có nhập mới
        if (request.getMatKhau() != null && !request.getMatKhau().trim().isEmpty()) {
            if (request.getMatKhau().trim().length() < 6) {
                throw new IllegalArgumentException("Mật khẩu mới phải có ít nhất 6 ký tự");
            }
            nhanVien.setMatKhauHash(passwordEncoder.encode(request.getMatKhau().trim()));
        }

        nhanVien.setMaNhanVien(maNV);
        nhanVien.setHoTen(request.getHoTen().trim());
        nhanVien.setTenDangNhap(tenDN);
        nhanVien.setSoDienThoai(sdt);
        nhanVien.setEmail(email);
        nhanVien.setVaiTro(vaiTro);
        if (request.getTrangThai() != null) {
            nhanVien.setTrangThai(request.getTrangThai());
        }

        return nhanVienRepository.save(nhanVien);
    }

    /**
     * Tiêu chí 3 (US04): Active / Deactive (Khóa / Kích hoạt nhanh) tài khoản nhân sự.
     *
     * @param id ID của nhân viên cần chuyển trạng thái
     * @param tenDangNhapHienTai tên đăng nhập của nhân viên đang thao tác (chặn tự khóa chính mình)
     * @return NhanVien sau khi đổi trạng thái
     */
    @Transactional
    public NhanVien doiTrangThai(Long id, String tenDangNhapHienTai) {
        NhanVien nhanVien = layTheoId(id);

        // Bảo vệ tài khoản Super Admin root
        if ("superadmin".equalsIgnoreCase(nhanVien.getTenDangNhap())) {
            throw new IllegalStateException("Không được phép khóa tài khoản Quản trị viên tối cao (superadmin)!");
        }

        // Chặn admin tự khóa chính mình
        if (tenDangNhapHienTai != null && tenDangNhapHienTai.equalsIgnoreCase(nhanVien.getTenDangNhap())) {
            throw new IllegalStateException("Bạn không thể tự khóa/vô hiệu hóa tài khoản của chính mình!");
        }

        int trangThaiMoi = (nhanVien.getTrangThai() != null && nhanVien.getTrangThai() == 1) ? 0 : 1;
        nhanVien.setTrangThai(trangThaiMoi);

        return nhanVienRepository.save(nhanVien);
    }

    /**
     * Xóa nhân viên khỏi hệ thống (có kiểm tra an toàn và bảo toàn dữ liệu liên quan).
     *
     * @param id ID của nhân viên cần xóa
     * @param tenDangNhapHienTai tên đăng nhập đang đăng nhập (chặn tự xóa)
     */
    @Transactional
    public void xoa(Long id, String tenDangNhapHienTai) {
        NhanVien nhanVien = layTheoId(id);

        // 1. Không cho phép xóa Quản trị viên tối cao
        if ("superadmin".equalsIgnoreCase(nhanVien.getTenDangNhap())) {
            throw new IllegalStateException("Không được phép xóa tài khoản Quản trị viên tối cao (superadmin)!");
        }

        // 2. Chặn tự xóa chính mình
        if (tenDangNhapHienTai != null && tenDangNhapHienTai.equalsIgnoreCase(nhanVien.getTenDangNhap())) {
            throw new IllegalStateException("Bạn không thể tự xóa tài khoản của chính mình khi đang đăng nhập!");
        }

        // 3. Kiểm tra ràng buộc khóa ngoại trong các bảng thực tế (đơn hàng, đổi trả RMA, blog)
        int countLichSu = 0;
        int countRMA = 0;
        int countBlog = 0;

        try {
            Integer c1 = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [lich_su_trang_thai_don_hang] WHERE [nguoi_thuc_hien_id] = ?",
                    Integer.class, id);
            if (c1 != null) countLichSu = c1;
        } catch (Exception ignored) {}

        try {
            Integer c2 = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [yeu_cau_doi_tra] WHERE [nguoi_duyet_id] = ?",
                    Integer.class, id);
            if (c2 != null) countRMA = c2;
        } catch (Exception ignored) {}

        try {
            Integer c3 = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM [bai_viet_blog] WHERE [ma_tac_gia_id] = ?",
                    Integer.class, id);
            if (c3 != null) countBlog = c3;
        } catch (Exception ignored) {}

        int tongRangBuoc = countLichSu + countRMA + countBlog;
        if (tongRangBuoc > 0) {
            throw new IllegalStateException("Không thể xóa nhân viên [" + nhanVien.getHoTen() +
                    "] vì đã có " + tongRangBuoc + " dữ liệu lịch sử liên kết (" +
                    countLichSu + " lịch sử đơn hàng, " + countRMA + " duyệt đổi trả RMA, " + countBlog + " bài viết blog). " +
                    "Để bảo toàn toàn vẹn dữ liệu, vui lòng chọn chức năng Khóa (Deactivate) thay thế!");
        }

        nhanVienRepository.delete(nhanVien);
    }

    /**
     * Đếm tổng số lượng nhân viên trong hệ thống (dùng cho thống kê Dashboard).
     */
    public long demTongNhanVien() {
        return nhanVienRepository.count();
    }
}
