package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LanGiaoHangRepository extends JpaRepository<LanGiaoHang, Long> {
    Optional<LanGiaoHang> findFirstByDonMuaHangIdAndTrangThaiOrderByLanThuDesc(Long donMuaId, TrangThaiLanGiaoHang trangThai);
    int countByDonMuaHangId(Long donMuaId);
}
