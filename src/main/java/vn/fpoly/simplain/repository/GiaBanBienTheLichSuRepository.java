package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import vn.fpoly.simplain.entity.GiaBanBienTheLichSu;

import java.util.List;

public interface GiaBanBienTheLichSuRepository extends JpaRepository<GiaBanBienTheLichSu, Long> {
    List<GiaBanBienTheLichSu> findAllByBienThe_IdOrderByThoiDiemAscIdAsc(Long bienTheId);

    @Query("select distinct g.bienThe.id from GiaBanBienTheLichSu g")
    List<Long> findDistinctBienTheIds();
}
