package com.br.hsiphonesapi.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
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
@Table(name = "product_repair")
public class ProductRepair {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false)
    @JoinColumn(name = "phone_id")
    private Product phone;

    @ManyToOne(optional = false)
    @JoinColumn(name = "part_id")
    private Product part;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "part_cost", nullable = false)
    private BigDecimal partCost;

    @Column(name = "labor_cost")
    @Builder.Default
    private BigDecimal laborCost = BigDecimal.ZERO;

    @Column(name = "labor_description")
    private String laborDescription;

    private String notes;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }
}