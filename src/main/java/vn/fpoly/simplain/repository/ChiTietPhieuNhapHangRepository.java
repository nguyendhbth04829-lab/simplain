package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChiTietPhieuNhapHang;

import java.util.List;

@Repository
public interface ChiTietPhieuNhapHangRepository extends JpaRepository<ChiTietPhieuNhapHang, Long> {

    List<ChiTietPhieuNhapHang> findByMaPhieuNhapId(Long maPhieuNhapId);

    void deleteByMaPhieuNhapId(Long maPhieuNhapId);
}
