package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface LichSuDonMuaHangRepository extends JpaRepository<LichSuDonMuaHang, Long> {
    List<LichSuDonMuaHang> findByDonMuaHangIdOrderByNgayTaoDesc(Long donMuaId);
}
