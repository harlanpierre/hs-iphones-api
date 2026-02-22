package com.br.hsiphonesapi.dto.response;

import com.br.hsiphonesapi.model.enums.ServiceOrderStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class ServiceOrderResponseDTO {

    @Schema(description = "ID da Ordem de Serviço", example = "1001")
    private Long id;

    @Schema(description = "Nome do Cliente", example = "Maria Oliveira")
    private String clientName;

    @Schema(description = "Modelo do aparelho", example = "iPhone 13 Pro Max")
    private String deviceModel;

    @Schema(description = "Problema relatado", example = "Não carrega")
    private String reportedIssue;

    @Schema(description = "Diagnóstico do técnico", example = "Conector de carga oxidado")
    private String diagnostic;

    @Schema(description = "Status atual da OS", example = "IN_PROGRESS")
    private ServiceOrderStatus status;

    @Schema(description = "Custo da Mão de Obra (Serviço)", example = "150.00")
    private BigDecimal laborCost;

    @Schema(description = "Custo total das Peças utilizadas", example = "150.00")
    private BigDecimal partsCost;

    @Schema(description = "Valor de desconto aplicado", example = "0.00")
    private BigDecimal discountAmount;

    @Schema(description = "Valor total a pagar", example = "300.00")
    private BigDecimal totalAmount;

    @Schema(description = "Data de abertura da OS", example = "2024-03-01T10:00:00")
    private LocalDateTime createdAt;

    @Schema(description = "Data de finalização", example = "2024-03-02T15:30:00")
    private LocalDateTime completedAt;

    @Schema(description = "Peças utilizadas no conserto")
    private List<ServiceOrderItemResponseDTO> items;
}