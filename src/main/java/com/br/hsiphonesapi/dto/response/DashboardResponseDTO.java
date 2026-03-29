package com.br.hsiphonesapi.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardResponseDTO {

    private KpisDTO kpis;
    private List<SalesByDayDTO> salesByDay;
    private List<StatusCountDTO> salesByStatus;
    private List<StatusCountDTO> serviceOrdersByStatus;
    private List<CategoryCountDTO> productsByCategory;
    private List<LowStockAlertDTO> lowStockAlerts;
    private List<RecentSaleDTO> recentSales;
    private List<PendingServiceOrderDTO> pendingServiceOrders;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class KpisDTO {
        private Long salesCount;
        private BigDecimal revenue;
        private BigDecimal avgTicket;
        private Long openServiceOrders;
        private Long productsInStock;
        private Long lowStockProducts;
        private Long newClients;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SalesByDayDTO {
        private LocalDate date;
        private Long count;
        private BigDecimal revenue;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class StatusCountDTO {
        private String status;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryCountDTO {
        private String category;
        private Long count;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class LowStockAlertDTO {
        private Long id;
        private String name;
        private Integer quantity;
        private Integer minStock;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class RecentSaleDTO {
        private Long id;
        private String clientName;
        private BigDecimal netAmount;
        private String status;
        private LocalDateTime createdAt;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class PendingServiceOrderDTO {
        private Long id;
        private String clientName;
        private String deviceModel;
        private String status;
        private LocalDateTime createdAt;
    }
}
