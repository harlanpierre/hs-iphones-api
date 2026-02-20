package com.br.hsiphonesapi.dto.response;

import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponseDTO {

    @Schema(description = "ID do produto", example = "50")
    private Long id;

    @Schema(example = "iPhone 15 Pro Max 256GB Titânio Natural")
    private String name;

    @Schema(example = "Aparelho seminovo em estado de novo.")
    private String description;

    @Schema(example = "CEL-IPH15-001")
    private String sku;

    @Schema(example = "CELULAR")
    private ProductCategory category;

    @Schema(description = "Status atual do produto", example = "DISPONIVEL")
    private ProductStatus status;

    @Schema(example = "4500.00")
    private BigDecimal purchasePrice;

    @Schema(example = "6200.00")
    private BigDecimal salePrice;

    @Schema(example = "1")
    private Integer quantity;

    @Schema(example = "2")
    private Integer minStock;

    @Schema(description = "Lista de IMEIs", example = "[\"354682090001234\", \"354682090005678\"]")
    private Set<String> imeis;

    @Schema(description = "Modelo compatível (se aplicável)", example = "iPhone 15")
    private String compatibleModel;

    @Schema(example = "2024-01-10")
    private LocalDate supplierWarrantyStartDate;

    @Schema(example = "2024-04-10")
    private LocalDate supplierWarrantyEndDate;

    @Schema(description = "Dados do fornecedor (se a origem for compra)")
    private SupplierResponseDTO supplier;

    @Schema(description = "Dados do cliente (se a origem for BuyBack)")
    private ClientResponseDTO client;
}