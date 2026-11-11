package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NhatKyKhoRepository extends JpaRepository<NhatKyKho, Long> {
    List<NhatKyKho> findByMaBienTheIdOrderByNgayTaoDesc(Long maBienTheId);
}
