package vn.fpoly.simplain.repository;

import vn.fpoly.simplain.entity.DanhMuc;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DanhMucRepository extends JpaRepository<DanhMuc, Long> {
    Page<DanhMuc> findAllByTenDanhMucContainingIgnoreCase(String tenDanhMuc, Pageable pageable);
}
