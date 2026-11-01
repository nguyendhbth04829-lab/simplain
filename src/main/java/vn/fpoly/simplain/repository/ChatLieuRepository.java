package vn.fpoly.simplain.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.fpoly.simplain.entity.ChatLieu;

import java.util.List;
import java.util.Optional;

/**
 * Kho dữ liệu cho thực thể Chất liệu vải [chat_lieu].
 */
@Repository
public interface ChatLieuRepository extends JpaRepository<ChatLieu, Long> {

    Optional<ChatLieu> findByTenChatLieu(String tenChatLieu);

    List<ChatLieu> findByKichHoatTrue(Sort sort);

    boolean existsByTenChatLieuIgnoreCase(String tenChatLieu);

    boolean existsByTenChatLieuIgnoreCaseAndIdNot(String tenChatLieu, Long id);

    @Query("SELECT cl FROM ChatLieu cl WHERE " +
           "(:tuKhoa IS NULL OR :tuKhoa = '' OR " +
           " LOWER(cl.tenChatLieu) LIKE LOWER(CONCAT('%', :tuKhoa, '%')) OR " +
           " LOWER(cl.moTa) LIKE LOWER(CONCAT('%', :tuKhoa, '%'))) AND " +
           "(:kichHoat IS NULL OR cl.kichHoat = :kichHoat)")
    Page<ChatLieu> timKiemPhanTrang(@Param("tuKhoa") String tuKhoa,
                                    @Param("kichHoat") Boolean kichHoat,
                                    Pageable pageable);
}
