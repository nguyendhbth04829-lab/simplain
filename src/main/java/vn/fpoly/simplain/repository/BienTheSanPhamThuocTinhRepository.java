package vn.fpoly.simplain.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import vn.fpoly.simplain.entity.BienTheSanPhamThuocTinh;

import java.util.List;

public interface BienTheSanPhamThuocTinhRepository extends JpaRepository<BienTheSanPhamThuocTinh, Long> {
    @EntityGraph(attributePaths = "thuocTinh")
    List<BienTheSanPhamThuocTinh> findByBienThe_IdIn(List<Long> bienTheIds);

    @EntityGraph(attributePaths = "thuocTinh")
    List<BienTheSanPhamThuocTinh> findByBienThe_Id(Long bienTheId);

    @org.springframework.transaction.annotation.Transactional
    void deleteByBienThe_Id(Long bienTheId);
}
