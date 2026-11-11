package vn.fpoly.simplain.inbound;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface DonMuaHangRepository extends JpaRepository<DonMuaHang, Long> {
    List<DonMuaHang> findAllByOrderByIdDesc();

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select d from DonMuaHang d where d.id = :id")
    Optional<DonMuaHang> findByIdForUpdate(@Param("id") Long id);
}
