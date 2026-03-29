package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Detalhes de um plano de assinatura")
public class PlanResponseDTO {

    @Schema(description = "ID do plano", example = "1")
    private Long id;

    @Schema(description = "Nome do plano", example = "Básico")
    private String name;

    @Schema(description = "Slug do plano", example = "basic")
    private String slug;

    @Schema(description = "Descrição do plano")
    private String description;

    @Schema(description = "Preço mensal", example = "79.90")
    private BigDecimal priceMonthly;

    @Schema(description = "Máximo de produtos", example = "100")
    private Integer maxProducts;

    @Schema(description = "Máximo de clientes", example = "200")
    private Integer maxClients;

    @Schema(description = "Máximo de usuários", example = "3")
    private Integer maxUsers;

    @Schema(description = "Máximo de vendas por mês", example = "100")
    private Integer maxSalesPerMonth;

    @Schema(description = "Máximo de OS por mês", example = "50")
    private Integer maxServiceOrdersPerMonth;
}
