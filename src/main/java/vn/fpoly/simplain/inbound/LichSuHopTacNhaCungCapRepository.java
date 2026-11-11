package vn.fpoly.simplain.inbound;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface LichSuHopTacNhaCungCapRepository extends JpaRepository<LichSuHopTacNhaCungCap, Long> {

    @Override
    @EntityGraph(attributePaths = {"nhaCungCap"})
    List<LichSuHopTacNhaCungCap> findAll(Sort sort);

    @EntityGraph(attributePaths = {"nhaCungCap"})
    Optional<LichSuHopTacNhaCungCap> findFirstByNhaCungCap_IdOrderByNgayTaoDescIdDesc(Long nhaCungCapId);
}
