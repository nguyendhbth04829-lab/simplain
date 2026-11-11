package vn.fpoly.simplain.inbound;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Long> {
    Optional<NhaCungCap> findByMaNccIgnoreCase(String maNcc);
    boolean existsByMaNccIgnoreCase(String maNcc);
    boolean existsByTenNccIgnoreCase(String tenNcc);
    boolean existsByTenNccIgnoreCaseAndIdNot(String tenNcc, Long id);
}
