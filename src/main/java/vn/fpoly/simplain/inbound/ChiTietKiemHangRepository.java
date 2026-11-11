package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChiTietKiemHangRepository extends JpaRepository<ChiTietKiemHang, Long> {
    List<ChiTietKiemHang> findByChiTietDonMuaId(Long chiTietDonMuaId);
    List<ChiTietKiemHang> findByPhieuNhapKhoIdOrderById(Long phieuNhapId);
}
