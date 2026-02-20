package com.br.hsiphonesapi.dto.request;

import com.br.hsiphonesapi.model.enums.ProductCategory;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductRequestDTO {

    @Schema(description = "Nome do produto", example = "iPhone 15 Pro Max 256GB Titânio Natural")
    @NotBlank(message = "Nome do produto é obrigatório")
    private String name;

    @Schema(description = "Descrição detalhada do produto", example = "Aparelho seminovo em estado de novo, bateria 100%. Acompanha caixa.")
    private String description;

    @Schema(description = "Código SKU (Se vazio, será gerado automaticamente)", example = "CEL-IPH15-001")
    private String sku;

    @Schema(description = "Categoria do produto", example = "CELULAR")
    @NotNull(message = "Categoria é obrigatória")
    private ProductCategory category;

    @Schema(description = "Preço de compra (Custo). Pode ser 0.0 em caso de troca.", example = "4500.00")
    @NotNull(message = "Preço de compra é obrigatório")
    @PositiveOrZero(message = "O preço de compra deve ser zero ou positivo")
    private BigDecimal purchasePrice;

    @Schema(description = "Preço de venda", example = "6200.00")
    @NotNull(message = "Preço de venda é obrigatório")
    @DecimalMin(value = "0.0", inclusive = false)
    private BigDecimal salePrice;

    @Schema(description = "Quantidade em estoque", example = "1")
    @NotNull(message = "Quantidade é obrigatória")
    @PositiveOrZero
    private Integer quantity;

    @Schema(description = "Estoque mínimo para alerta", example = "2")
    @PositiveOrZero
    private Integer minStock;

    @Schema(description = "Lista de IMEIs (Físico + eSIM)", example = "[\"354682090001234\", \"354682090005678\"]")
    private Set<String> imeis;

    @Schema(description = "Modelo compatível (Para Peças/Acessórios)", example = "iPhone 14, iPhone 15")
    private String compatibleModel;

    @Schema(description = "Início da garantia do fornecedor", example = "2024-01-10")
    private LocalDate supplierWarrantyStartDate;

    @Schema(description = "Fim da garantia do fornecedor", example = "2024-04-10")
    private LocalDate supplierWarrantyEndDate;

    @Schema(description = "ID do Fornecedor (Opcional se tiver cliente)", example = "1")
    private Long supplierId;

    @Schema(description = "ID do Cliente (Obrigatório em caso de BuyBack de pessoa física)", example = "5")
    private Long clientId;
}