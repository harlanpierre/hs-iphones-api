package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InternalRepairRequestDTO {

    @Schema(description = "ID do celular (BuyBack) que será reparado", example = "10")
    @NotNull(message = "ID do celular é obrigatório")
    private Long phoneId;

    @Schema(description = "Lista de peças consumidas (Opcional caso seja apenas serviço de placa)")
    @Valid
    private List<InternalRepairPartRequestDTO> parts;

    @Schema(description = "Custo de mão de obra terceirizada (Ex: Reparo de placa)", example = "150.00")
    private BigDecimal laborCost;

    @Schema(description = "Descrição do serviço terceirizado", example = "Reparo no CI de Carga")
    private String laborDescription;

    @Schema(description = "Observações sobre o reparo", example = "Troca de tela frontal completa e vedação")
    private String notes;
}