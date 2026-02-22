package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderItemResponseDTO {

    @Schema(description = "ID do item na Ordem de Serviço", example = "1")
    private Long id;

    @Schema(description = "Nome da peça/produto utilizado", example = "Conector de Carga iPhone 13")
    private String productName;

    @Schema(description = "Quantidade utilizada", example = "1")
    private Integer quantity;

    @Schema(description = "Preço cobrado pela unidade", example = "150.00")
    private BigDecimal unitPrice;

    @Schema(description = "Subtotal (Qtd * Preço Unitário)", example = "150.00")
    private BigDecimal subtotal;
}