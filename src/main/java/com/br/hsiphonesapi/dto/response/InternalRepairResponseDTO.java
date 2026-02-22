package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalRepairResponseDTO {

    @Schema(description = "ID do registro de reparo interno", example = "1")
    private Long id;

    @Schema(description = "Nome do celular reparado", example = "iPhone 11 64GB Preto")
    private String phoneName;

    @Schema(description = "Custo apenas das peças nesta sessão", example = "200.00")
    private BigDecimal totalPartsCost;

    @Schema(description = "Custo da mão de obra nesta sessão", example = "150.00")
    private BigDecimal laborCost;

    @Schema(description = "Custo TOTAL (Peças + Mão de Obra) adicionado ao aparelho", example = "350.00")
    private BigDecimal totalSessionCost;

    @Schema(description = "Descrição do serviço terceirizado", example = "Reparo no CI de Carga")
    private String laborDescription;

    @Schema(description = "Observações registradas", example = "Aparelho voltou a ligar")
    private String notes;

    @Schema(description = "Lista das peças que foram consumidas")
    private List<InternalRepairPartResponseDTO> parts;
}