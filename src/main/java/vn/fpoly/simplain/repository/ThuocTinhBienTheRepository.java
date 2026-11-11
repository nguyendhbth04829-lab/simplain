package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fpoly.simplain.entity.ThuocTinhBienThe;

import java.util.List;
import java.util.Optional;

public interface ThuocTinhBienTheRepository extends JpaRepository<ThuocTinhBienThe, Long> {
    List<ThuocTinhBienThe> findByKichHoatTrueOrderByThuTuAscIdAsc();

    Optional<ThuocTinhBienThe> findByMaThuocTinhIgnoreCase(String maThuocTinh);
}
