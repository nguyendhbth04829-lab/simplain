package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PhieuNhapKhoRepository extends JpaRepository<PhieuNhapKho, Long> {
    Optional<PhieuNhapKho> findByMaPhieu(String maPhieu);

    @Query("SELECT DISTINCT p FROM PhieuNhapKho p LEFT JOIN FETCH p.nhaCungCap LEFT JOIN FETCH p.donMuaHang ORDER BY p.ngayNhap DESC")
    List<PhieuNhapKho> findAllWithNhaCungCap();

    @Query("SELECT DISTINCT p FROM PhieuNhapKho p LEFT JOIN FETCH p.nhaCungCap LEFT JOIN FETCH p.chiTietList ct LEFT JOIN FETCH ct.bienThe bt WHERE p.id = :id")
    Optional<PhieuNhapKho> findWithDetailsById(@Param("id") Long id);
}
