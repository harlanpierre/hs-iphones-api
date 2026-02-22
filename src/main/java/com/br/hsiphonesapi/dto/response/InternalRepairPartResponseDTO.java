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
public class InternalRepairPartResponseDTO {

    @Schema(description = "Nome da peça utilizada", example = "Tela Display LCD iPhone 11")
    private String partName;

    @Schema(description = "Quantidade consumida", example = "1")
    private Integer quantity;

    @Schema(description = "Custo unitário da peça no momento do uso (CMV)", example = "250.00")
    private BigDecimal partCost;
}