package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleItemResponseDTO {

    @Schema(description = "ID do item na venda", example = "1")
    private Long id;

    @Schema(description = "Nome do produto vendido", example = "iPhone 15 Pro Max 256GB Titânio Natural")
    private String productName;

    @Schema(description = "SKU do produto", example = "CEL-IPH15-001")
    private String sku;

    @Schema(description = "IMEI do aparelho (se for celular)", example = "354682090001234")
    private String imei;

    @Schema(description = "Quantidade vendida", example = "1")
    private Integer quantity;

    @Schema(description = "Subtotal (Quantidade * Preço Unitário)", example = "6200.00")
    private BigDecimal subtotal;

    @Schema(description = "Data do fim da garantia deste item", example = "2024-05-24")
    private LocalDate warrantyEndDate;

    @Schema(description = "Indica se o item é um brinde (Sairá por R$ 0,00, mas baixará estoque)", example = "true")
    private Boolean isFreebie = false;
}