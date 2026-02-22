package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;
import java.util.List;

@Getter
@Setter
public class SaleRequestDTO {

    @Schema(description = "ID do cliente comprador", example = "1")
    @NotNull(message = "ID do cliente é obrigatório")
    private Long clientId;

    @Schema(description = "Nome do vendedor responsável pela venda", example = "Carlos Silva")
    private String sellerName;

    @Schema(description = "Observações da venda (Ex: Brindes, acordos)", example = "Cliente levou película e capa de brinde")
    private String notes;

    @Schema(description = "Valor de desconto aplicado no total da venda", example = "150.00")
    private BigDecimal discountAmount;

    @Schema(description = "Lista de itens (produtos) da venda")
    @NotEmpty(message = "A venda deve conter pelo menos um item")
    @Valid
    private List<SaleItemRequestDTO> items;

    @Schema(description = "Lista de pagamentos (Obrigatório para Venda Direta/Checkout)")
    @Valid
    private List<PaymentRequestDTO> payments;
}