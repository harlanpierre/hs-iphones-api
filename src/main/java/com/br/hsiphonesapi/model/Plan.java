package com.br.hsiphonesapi.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "plan")
public class Plan {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false, unique = true)
    private String slug;

    private String description;

    @Column(name = "price_monthly", nullable = false)
    private BigDecimal priceMonthly;

    @Column(name = "max_products", nullable = false)
    private Integer maxProducts;

    @Column(name = "max_clients", nullable = false)
    private Integer maxClients;

    @Column(name = "max_users", nullable = false)
    private Integer maxUsers;

    @Column(name = "max_sales_per_month", nullable = false)
    private Integer maxSalesPerMonth;

    @Column(name = "max_service_orders_per_month", nullable = false)
    private Integer maxServiceOrdersPerMonth;

    @Column(name = "features_json")
    private String featuresJson;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }
}
