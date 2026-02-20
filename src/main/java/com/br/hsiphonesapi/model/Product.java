package com.br.hsiphonesapi.model;

import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "product")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(unique = true)
    private String sku;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductCategory category;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ProductStatus status;

    @Column(name = "purchase_price", nullable = false)
    private BigDecimal purchasePrice;

    @Column(name = "sale_price", nullable = false)
    private BigDecimal salePrice;

    @Column(nullable = false)
    private Integer quantity;

    @Column(name = "min_stock")
    private Integer minStock;

    @ElementCollection
    @CollectionTable(name = "product_imei", joinColumns = @JoinColumn(name = "product_id"))
    @Column(name = "imei", nullable = false)
    private Set<String> imeis = new HashSet<>();

    @Column(name = "compatible_model")
    private String compatibleModel;

    @Column(name = "supplier_warranty_start_date")
    private LocalDate supplierWarrantyStartDate;

    @Column(name = "supplier_warranty_end_date")
    private LocalDate supplierWarrantyEndDate;

    // Origem: Fornecedor
    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;

    // Origem: Cliente (BuyBack de PF)
    @ManyToOne
    @JoinColumn(name = "client_id")
    private Client client;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
        if (this.quantity == null) this.quantity = 0;
        if (this.status == null) this.status = ProductStatus.DISPONIVEL;
    }
}