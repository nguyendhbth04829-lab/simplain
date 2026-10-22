package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import vn.fpoly.simplain.entity.DiaChi;

import java.util.List;
import java.util.Optional;

public interface DiaChiRepository extends JpaRepository<DiaChi, Long> {
    List<DiaChi> findByMaKhachHangIdOrderByLaMacDinhDescIdAsc(Long maKhachHangId);

    Optional<DiaChi> findByIdAndMaKhachHangId(Long id, Long maKhachHangId);
}
