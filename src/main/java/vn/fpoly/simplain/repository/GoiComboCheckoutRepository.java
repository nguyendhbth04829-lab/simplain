package vn.fpoly.simplain.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Repository
public class GoiComboCheckoutRepository {
    private final JdbcTemplate jdbcTemplate;

    public GoiComboCheckoutRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Map<Long, BigDecimal> layGiaCombo(List<Long> ids) {
        if (ids.isEmpty()) return Map.of();
        String placeholders = ids.stream().map(id -> "?").collect(Collectors.joining(","));
        String sql = "SELECT id, gia_combo FROM goi_combo WHERE id IN (" + placeholders + ") AND kich_hoat = 1";
        return jdbcTemplate.query(sql, resultSet -> {
            Map<Long, BigDecimal> prices = new LinkedHashMap<>();
            while (resultSet.next()) {
                prices.put(resultSet.getLong("id"), resultSet.getBigDecimal("gia_combo"));
            }
            return prices;
        }, ids.toArray());
    }
}
