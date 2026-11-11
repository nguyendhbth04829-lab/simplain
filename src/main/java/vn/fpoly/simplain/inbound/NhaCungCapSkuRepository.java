package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface NhaCungCapSkuRepository extends JpaRepository<NhaCungCapSku, Long> {
    boolean existsByNhaCungCapIdAndBienTheId(Long nhaCungCapId, Long bienTheId);

    @Query("select l from NhaCungCapSku l join fetch l.bienThe where l.nhaCungCap.id = :nccId")
    List<NhaCungCapSku> findByNhaCungCapId(@Param("nccId") Long nhaCungCapId);
}
