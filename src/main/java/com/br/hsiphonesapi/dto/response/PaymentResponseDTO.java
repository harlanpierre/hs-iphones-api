package com.br.hsiphonesapi.dto.response;

import com.br.hsiphonesapi.model.enums.PaymentMethod;
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
public class PaymentResponseDTO {

    @Schema(description = "ID do pagamento", example = "1")
    private Long id;

    @Schema(description = "Método de pagamento utilizado", example = "PIX")
    private PaymentMethod method;

    @Schema(description = "Valor pago", example = "1500.00")
    private BigDecimal amount;

    @Schema(description = "Quantidade de parcelas", example = "1")
    private Integer installments;

    @Schema(description = "Nome do aparelho de retoma (se método for BUYBACK)", example = "iPhone 11 64GB Preto")
    private String buybackProductName;
}