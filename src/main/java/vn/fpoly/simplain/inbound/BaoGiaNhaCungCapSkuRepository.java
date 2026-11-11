package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import vn.fpoly.simplain.entity.BienTheSanPham;

import java.util.List;

public interface BaoGiaNhaCungCapSkuRepository extends JpaRepository<BaoGiaNhaCungCapSku, Long> {
    List<BaoGiaNhaCungCapSku> findAllByNhaCungCap_IdAndBienThe_IdOrderByThoiDiemBaoGiaAscIdAsc(
            Long nhaCungCapId, Long bienTheId);

    @Query("select distinct bg.bienThe.maSanPhamId from BaoGiaNhaCungCapSku bg where bg.nhaCungCap.id = :nccId")
    List<Long> findDistinctSanPhamIdsByNhaCungCapId(@Param("nccId") Long nhaCungCapId);

    @Query("select distinct bg.bienThe.maSku from BaoGiaNhaCungCapSku bg where bg.nhaCungCap.id = :nccId")
    List<String> findDistinctMaSkuByNhaCungCapId(@Param("nccId") Long nhaCungCapId);

    @Query("select bg.bienThe from BaoGiaNhaCungCapSku bg where bg.nhaCungCap.id = :nccId and bg.bienThe.maSanPhamId = :sanPhamId")
    List<BienTheSanPham> findBienTheByNhaCungCapIdAndSanPhamId(@Param("nccId") Long nhaCungCapId,
                                                               @Param("sanPhamId") Long sanPhamId);

    boolean existsByNhaCungCap_IdAndBienThe_Id(Long nhaCungCapId, Long bienTheId);
}
