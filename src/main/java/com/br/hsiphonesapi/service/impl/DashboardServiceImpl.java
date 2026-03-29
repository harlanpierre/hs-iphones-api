package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.dto.response.DashboardResponseDTO;
import com.br.hsiphonesapi.dto.response.DashboardResponseDTO.*;
import com.br.hsiphonesapi.service.DashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class DashboardServiceImpl implements DashboardService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public DashboardResponseDTO getDashboard(int month, int year) {
        Long tenantId = TenantContext.getTenantId();
        LocalDateTime startOfMonth = LocalDate.of(year, month, 1).atStartOfDay();
        LocalDateTime endOfMonth = LocalDate.of(year, month, 1).plusMonths(1).atStartOfDay();

        log.info("Gerando dashboard para tenant {} - {}/{}", tenantId, month, year);

        return DashboardResponseDTO.builder()
                .kpis(buildKpis(tenantId, startOfMonth, endOfMonth))
                .salesByDay(querySalesByDay(tenantId, startOfMonth, endOfMonth))
                .salesByStatus(querySalesByStatus(tenantId, startOfMonth, endOfMonth))
                .serviceOrdersByStatus(queryServiceOrdersByStatus(tenantId))
                .productsByCategory(queryProductsByCategory(tenantId))
                .lowStockAlerts(queryLowStockAlerts(tenantId))
                .recentSales(queryRecentSales(tenantId))
                .pendingServiceOrders(queryPendingServiceOrders(tenantId))
                .build();
    }

    private KpisDTO buildKpis(Long tenantId, LocalDateTime start, LocalDateTime end) {
        // Sales count and revenue for completed sales in the month
        String salesSql = "SELECT COUNT(*), COALESCE(SUM(net_amount), 0) FROM sale " +
                "WHERE tenant_id = ? AND status = 'CONCLUIDO' AND created_at >= ? AND created_at < ?";
        Long salesCount = 0L;
        BigDecimal revenue = BigDecimal.ZERO;
        var salesResult = jdbcTemplate.queryForMap(salesSql, tenantId, start, end);
        salesCount = ((Number) salesResult.get("count")).longValue();
        revenue = (BigDecimal) salesResult.get("coalesce");

        BigDecimal avgTicket = salesCount > 0
                ? revenue.divide(BigDecimal.valueOf(salesCount), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Open service orders (not delivered/canceled)
        String openOsSql = "SELECT COUNT(*) FROM service_order " +
                "WHERE tenant_id = ? AND status NOT IN ('DELIVERED', 'CANCELED')";
        Long openOs = queryForLong(openOsSql, tenantId);

        // Products in stock
        String stockSql = "SELECT COUNT(*) FROM product " +
                "WHERE tenant_id = ? AND deleted = false AND status = 'DISPONIVEL'";
        Long productsInStock = queryForLong(stockSql, tenantId);

        // Low stock products
        String lowStockSql = "SELECT COUNT(*) FROM product " +
                "WHERE tenant_id = ? AND deleted = false AND quantity < min_stock AND min_stock IS NOT NULL";
        Long lowStockProducts = queryForLong(lowStockSql, tenantId);

        // New clients in the month
        String clientsSql = "SELECT COUNT(*) FROM client " +
                "WHERE tenant_id = ? AND created_at >= ? AND created_at < ?";
        Long newClients = queryForLong(clientsSql, tenantId, start, end);

        return KpisDTO.builder()
                .salesCount(salesCount)
                .revenue(revenue)
                .avgTicket(avgTicket)
                .openServiceOrders(openOs)
                .productsInStock(productsInStock)
                .lowStockProducts(lowStockProducts)
                .newClients(newClients)
                .build();
    }

    private List<SalesByDayDTO> querySalesByDay(Long tenantId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT DATE(created_at) AS sale_date, COUNT(*) AS cnt, COALESCE(SUM(net_amount), 0) AS rev " +
                "FROM sale WHERE tenant_id = ? AND status = 'CONCLUIDO' AND created_at >= ? AND created_at < ? " +
                "GROUP BY DATE(created_at) ORDER BY sale_date";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                SalesByDayDTO.builder()
                        .date(rs.getDate("sale_date").toLocalDate())
                        .count(rs.getLong("cnt"))
                        .revenue(rs.getBigDecimal("rev"))
                        .build(),
                tenantId, start, end);
    }

    private List<StatusCountDTO> querySalesByStatus(Long tenantId, LocalDateTime start, LocalDateTime end) {
        String sql = "SELECT status, COUNT(*) AS cnt FROM sale " +
                "WHERE tenant_id = ? AND created_at >= ? AND created_at < ? " +
                "GROUP BY status ORDER BY status";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                StatusCountDTO.builder()
                        .status(rs.getString("status"))
                        .count(rs.getLong("cnt"))
                        .build(),
                tenantId, start, end);
    }

    private List<StatusCountDTO> queryServiceOrdersByStatus(Long tenantId) {
        String sql = "SELECT status, COUNT(*) AS cnt FROM service_order " +
                "WHERE tenant_id = ? GROUP BY status ORDER BY status";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                StatusCountDTO.builder()
                        .status(rs.getString("status"))
                        .count(rs.getLong("cnt"))
                        .build(),
                tenantId);
    }

    private List<CategoryCountDTO> queryProductsByCategory(Long tenantId) {
        String sql = "SELECT category, COUNT(*) AS cnt FROM product " +
                "WHERE tenant_id = ? AND deleted = false AND status = 'DISPONIVEL' " +
                "GROUP BY category ORDER BY category";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                CategoryCountDTO.builder()
                        .category(rs.getString("category"))
                        .count(rs.getLong("cnt"))
                        .build(),
                tenantId);
    }

    private List<LowStockAlertDTO> queryLowStockAlerts(Long tenantId) {
        String sql = "SELECT id, name, quantity, min_stock FROM product " +
                "WHERE tenant_id = ? AND deleted = false AND quantity < min_stock AND min_stock IS NOT NULL " +
                "ORDER BY (min_stock - quantity) DESC LIMIT 10";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                LowStockAlertDTO.builder()
                        .id(rs.getLong("id"))
                        .name(rs.getString("name"))
                        .quantity(rs.getInt("quantity"))
                        .minStock(rs.getInt("min_stock"))
                        .build(),
                tenantId);
    }

    private List<RecentSaleDTO> queryRecentSales(Long tenantId) {
        String sql = "SELECT s.id, c.name AS client_name, s.net_amount, s.status, s.created_at " +
                "FROM sale s LEFT JOIN client c ON s.client_id = c.id " +
                "WHERE s.tenant_id = ? ORDER BY s.created_at DESC LIMIT 5";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                RecentSaleDTO.builder()
                        .id(rs.getLong("id"))
                        .clientName(rs.getString("client_name"))
                        .netAmount(rs.getBigDecimal("net_amount"))
                        .status(rs.getString("status"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build(),
                tenantId);
    }

    private List<PendingServiceOrderDTO> queryPendingServiceOrders(Long tenantId) {
        String sql = "SELECT so.id, c.name AS client_name, so.device_model, so.status, so.created_at " +
                "FROM service_order so LEFT JOIN client c ON so.client_id = c.id " +
                "WHERE so.tenant_id = ? AND so.status IN ('AWAITING_APPROVAL', 'READY_FOR_PICKUP') " +
                "ORDER BY so.created_at LIMIT 5";
        return jdbcTemplate.query(sql, (ResultSet rs, int rowNum) ->
                PendingServiceOrderDTO.builder()
                        .id(rs.getLong("id"))
                        .clientName(rs.getString("client_name"))
                        .deviceModel(rs.getString("device_model"))
                        .status(rs.getString("status"))
                        .createdAt(rs.getTimestamp("created_at").toLocalDateTime())
                        .build(),
                tenantId);
    }

    private Long queryForLong(String sql, Object... args) {
        Long result = jdbcTemplate.queryForObject(sql, Long.class, args);
        return result != null ? result : 0L;
    }
}
