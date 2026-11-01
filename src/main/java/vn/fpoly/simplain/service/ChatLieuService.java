package vn.fpoly.simplain.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.fpoly.simplain.entity.ChatLieu;
import vn.fpoly.simplain.repository.ChatLieuRepository;
import vn.fpoly.simplain.repository.SanPhamRepository;

import java.util.List;

/**
 * Service xử lý nghiệp vụ Quản lý Chất liệu vải [chat_lieu].
 * Hỗ trợ CRUD đầy đủ, Tìm kiếm từ khóa, Lọc trạng thái, Phân trang và kiểm tra ràng buộc toàn vẹn.
 */
@Service
public class ChatLieuService {

    private final ChatLieuRepository chatLieuRepository;
    private final SanPhamRepository sanPhamRepository;

    @Autowired
    public ChatLieuService(ChatLieuRepository chatLieuRepository,
                           SanPhamRepository sanPhamRepository) {
        this.chatLieuRepository = chatLieuRepository;
        this.sanPhamRepository = sanPhamRepository;
    }

    @Transactional(readOnly = true)
    public Page<ChatLieu> layDanhSachPhanTrang(String tuKhoa, Boolean kichHoat, Pageable pageable) {
        String tuKhoaChuan = (tuKhoa != null && !tuKhoa.isBlank()) ? tuKhoa.trim() : null;
        Page<ChatLieu> page = chatLieuRepository.timKiemPhanTrang(tuKhoaChuan, kichHoat, pageable);
        page.getContent().forEach(cl -> {
            cl.setSoSanPhamSuDung(sanPhamRepository.countByMaChatLieuId(cl.getId()));
        });
        return page;
    }

    @Transactional(readOnly = true)
    public List<ChatLieu> layTatCa() {
        List<ChatLieu> list = chatLieuRepository.findAll(Sort.by("id").descending());
        list.forEach(cl -> cl.setSoSanPhamSuDung(sanPhamRepository.countByMaChatLieuId(cl.getId())));
        return list;
    }

    @Transactional(readOnly = true)
    public List<ChatLieu> layTatCaKichHoat() {
        return chatLieuRepository.findByKichHoatTrue(Sort.by("tenChatLieu").ascending());
    }

    @Transactional(readOnly = true)
    public ChatLieu layTheoId(Long id) {
        ChatLieu cl = chatLieuRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Không tìm thấy chất liệu vải với ID: " + id));
        cl.setSoSanPhamSuDung(sanPhamRepository.countByMaChatLieuId(cl.getId()));
        return cl;
    }

    @Transactional
    public ChatLieu them(ChatLieu chatLieu) {
        if (chatLieu.getTenChatLieu() == null || chatLieu.getTenChatLieu().isBlank()) {
            throw new IllegalArgumentException("Tên chất liệu vải không được để trống!");
        }
        String tenChuan = chatLieu.getTenChatLieu().trim();
        if (chatLieuRepository.existsByTenChatLieuIgnoreCase(tenChuan)) {
            throw new IllegalArgumentException("Tên chất liệu vải [" + tenChuan + "] đã tồn tại trên hệ thống!");
        }
        chatLieu.setTenChatLieu(tenChuan);
        if (chatLieu.getMoTa() != null) {
            chatLieu.setMoTa(chatLieu.getMoTa().trim());
        }
        if (chatLieu.getKichHoat() == null) {
            chatLieu.setKichHoat(true);
        }
        return chatLieuRepository.save(chatLieu);
    }

    @Transactional
    public ChatLieu sua(Long id, ChatLieu duLieuMoi) {
        ChatLieu hienTai = layTheoId(id);
        if (duLieuMoi.getTenChatLieu() == null || duLieuMoi.getTenChatLieu().isBlank()) {
            throw new IllegalArgumentException("Tên chất liệu vải không được để trống!");
        }
        String tenChuan = duLieuMoi.getTenChatLieu().trim();
        if (chatLieuRepository.existsByTenChatLieuIgnoreCaseAndIdNot(tenChuan, id)) {
            throw new IllegalArgumentException("Tên chất liệu vải [" + tenChuan + "] đã được sử dụng ở bản ghi khác!");
        }
        hienTai.setTenChatLieu(tenChuan);
        hienTai.setMoTa(duLieuMoi.getMoTa() != null ? duLieuMoi.getMoTa().trim() : null);
        if (duLieuMoi.getKichHoat() != null) {
            hienTai.setKichHoat(duLieuMoi.getKichHoat());
        }
        return chatLieuRepository.save(hienTai);
    }

    @Transactional
    public ChatLieu doiTrangThai(Long id) {
        ChatLieu cl = layTheoId(id);
        cl.setKichHoat(!Boolean.TRUE.equals(cl.getKichHoat()));
        return chatLieuRepository.save(cl);
    }

    @Transactional
    public ChatLieu capNhatTrangThai(Long id, Boolean kichHoat) {
        ChatLieu cl = layTheoId(id);
        cl.setKichHoat(kichHoat);
        return chatLieuRepository.save(cl);
    }

    @Transactional
    public void xoa(Long id) {
        ChatLieu cl = layTheoId(id);
        long soSanPham = sanPhamRepository.countByMaChatLieuId(id);
        if (soSanPham > 0) {
            throw new IllegalStateException("Không thể xóa chất liệu vải [" + cl.getTenChatLieu() + "] vì đang có " + soSanPham + " sản phẩm liên kết!");
        }
        chatLieuRepository.delete(cl);
    }

    @Transactional(readOnly = true)
    public long demTongSo() {
        return chatLieuRepository.count();
    }
}
