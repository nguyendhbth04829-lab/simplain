package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.NhaCungCap;

import java.util.List;
import java.util.Optional;

@Repository
public interface NhaCungCapRepository extends JpaRepository<NhaCungCap, Long> {

    Optional<NhaCungCap> findByMaNhaCungCap(String maNhaCungCap);

    boolean existsByMaNhaCungCap(String maNhaCungCap);

    List<NhaCungCap> findByKichHoatTrue(Sort sort);

    List<NhaCungCap> findByKichHoat(Boolean kichHoat, Sort sort);

    long countByKichHoat(Boolean kichHoat);
}
