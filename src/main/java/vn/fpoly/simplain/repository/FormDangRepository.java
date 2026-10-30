package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.FormDang;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Form dáng [form_dang].
 */
@Repository
public interface FormDangRepository extends JpaRepository<FormDang, Long> {

    Optional<FormDang> findByTenFormDang(String tenFormDang);

    List<FormDang> findByKichHoatTrue(Sort sort);

    boolean existsByTenFormDangIgnoreCase(String tenFormDang);

    boolean existsByTenFormDangIgnoreCaseAndIdNot(String tenFormDang, Long id);

    @Query("SELECT fd FROM FormDang fd WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(fd.tenFormDang) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(fd.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:kichHoat IS NULL OR fd.kichHoat = :kichHoat)")
    Page<FormDang> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                    @Param("kichHoat") Boolean kichHoat,
                                    Pageable pageable);
}
