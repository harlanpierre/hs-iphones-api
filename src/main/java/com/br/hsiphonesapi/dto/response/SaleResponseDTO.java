package com.br.hsiphonesapi.dto.response;

import com.br.hsiphonesapi.model.enums.SaleStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SaleResponseDTO {
    @Schema(description = "ID da Venda/Pedido", example = "100")
    private Long id;

    @Schema(description = "Nome do cliente comprador", example = "João da Silva")
    private String clientName;

    @Schema(description = "Nome do vendedor", example = "Carlos Silva")
    private String sellerName;

    @Schema(description = "Status atual da venda", example = "CONCLUIDO")
    private SaleStatus status;

    @Schema(description = "Valor total bruto (soma dos itens)", example = "6350.00")
    private BigDecimal totalAmount;

    @Schema(description = "Desconto aplicado", example = "150.00")
    private BigDecimal discountAmount;

    @Schema(description = "Valor líquido final a pagar/pago", example = "6200.00")
    private BigDecimal netAmount;

    @Schema(description = "Observações", example = "Cliente levou película de brinde")
    private String notes;

    @Schema(description = "Data e hora da criação da venda", example = "2024-02-23T14:30:00")
    private LocalDateTime createdAt;

    @Schema(description = "Itens pertencentes a esta venda")
    private List<SaleItemResponseDTO> items;

    @Schema(description = "Pagamentos vinculados a esta venda")
    private List<PaymentResponseDTO> payments;
}