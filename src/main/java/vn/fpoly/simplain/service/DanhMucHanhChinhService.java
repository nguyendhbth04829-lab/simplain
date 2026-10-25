/**
 * Tác giả: Gia Minh
 * Chức năng: US13 - Dịch vụ quản lý danh mục phân cấp Tỉnh / Thành, Quận / Huyện, Phường / Xã nội bộ
 * Cập nhật xác nhận commit US13: 25/10/2026 - Gia Minh
 */
package vn.fpoly.simplain.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Dịch vụ cung cấp danh mục hành chính Việt Nam chạy offline không phụ thuộc internet.
 */
@Service
public class DanhMucHanhChinhService {

    private Map<String, Map<String, List<String>>> duLieuHanhChinh = new TreeMap<>();

    @PostConstruct
    public void khoiTaoDuLieu() {
        try {
            ClassPathResource resource = new ClassPathResource("data/danh-muc-hanh-chinh.json");
            if (resource.exists()) {
                ObjectMapper mapper = new ObjectMapper();
                try (InputStream is = resource.getInputStream()) {
                    Map<String, Map<String, List<String>>> loaded = mapper.readValue(
                            is,
                            new TypeReference<Map<String, Map<String, List<String>>>>() {}
                    );
                    if (loaded != null) {
                        this.duLieuHanhChinh = new TreeMap<>(loaded);
                    }
                }
            }
        } catch (Exception e) {
            // Nếu có lỗi nạp file, giữ danh mục rỗng hoặc fallback cơ bản
            this.duLieuHanhChinh = new TreeMap<>();
        }
    }

    /**
     * Lấy danh sách toàn bộ Tỉnh / Thành phố.
     *
     * @return danh sách tên tỉnh thành
     */
    public List<String> layDanhSachTinhThanh() {
        return new ArrayList<>(duLieuHanhChinh.keySet());
    }

    /**
     * Lấy danh sách Quận / Huyện thuộc Tỉnh / Thành phố chỉ định.
     *
     * @param tinhThanh tên Tỉnh / Thành phố
     * @return danh sách tên quận huyện
     */
    public List<String> layDanhSachQuanHuyen(String tinhThanh) {
        if (tinhThanh == null || !duLieuHanhChinh.containsKey(tinhThanh.trim())) {
            return Collections.emptyList();
        }
        Map<String, List<String>> quanHuyenMap = duLieuHanhChinh.get(tinhThanh.trim());
        return quanHuyenMap != null ? new ArrayList<>(quanHuyenMap.keySet()) : Collections.emptyList();
    }

    /**
     * Lấy danh sách Phường / Xã thuộc Quận / Huyện và Tỉnh / Thành chỉ định.
     *
     * @param tinhThanh tên Tỉnh / Thành phố
     * @param quanHuyen tên Quận / Huyện
     * @return danh sách tên phường xã
     */
    public List<String> layDanhSachPhuongXa(String tinhThanh, String quanHuyen) {
        if (tinhThanh == null || quanHuyen == null) {
            return Collections.emptyList();
        }
        Map<String, List<String>> quanHuyenMap = duLieuHanhChinh.get(tinhThanh.trim());
        if (quanHuyenMap == null || !quanHuyenMap.containsKey(quanHuyen.trim())) {
            return Collections.emptyList();
        }
        List<String> phuongXaList = quanHuyenMap.get(quanHuyen.trim());
        return phuongXaList != null ? new ArrayList<>(phuongXaList) : Collections.emptyList();
    }

    /**
     * Kiểm tra tính hợp lệ của phân cấp Tỉnh / Thành -> Quận / Huyện -> Phường / Xã.
     *
     * @param tinhThanh tên tỉnh thành
     * @param quanHuyen tên quận huyện
     * @param phuongXa tên phường xã
     * @return true nếu dữ liệu hợp lệ, false nếu không khớp phân cấp
     */
    public boolean kiemTraHopLe(String tinhThanh, String quanHuyen, String phuongXa) {
        if (tinhThanh == null || quanHuyen == null || phuongXa == null) {
            return false;
        }
        if (duLieuHanhChinh.isEmpty()) {
            return true; // Nếu dữ liệu chưa tải thì không chặn
        }
        String t = tinhThanh.trim();
        String q = quanHuyen.trim();
        String p = phuongXa.trim();

        if (!duLieuHanhChinh.containsKey(t)) {
            return false;
        }
        Map<String, List<String>> quanHuyenMap = duLieuHanhChinh.get(t);
        if (quanHuyenMap == null || !quanHuyenMap.containsKey(q)) {
            return false;
        }
        List<String> phuongXaList = quanHuyenMap.get(q);
        return phuongXaList != null && phuongXaList.contains(p);
    }
}
