package com.br.hsiphonesapi.dto.request;

import com.br.hsiphonesapi.model.enums.PaymentMethod;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;
import java.math.BigDecimal;

@Getter
@Setter
public class PaymentRequestDTO {

    @Schema(description = "Método de pagamento", example = "PIX")
    @NotNull(message = "Método de pagamento é obrigatório")
    private PaymentMethod method;

    @Schema(description = "Valor pago neste método", example = "1500.00")
    @NotNull
    @Positive(message = "O valor do pagamento deve ser positivo")
    private BigDecimal amount;

    @Schema(description = "Número de parcelas (para cartão de crédito)", example = "1")
    private Integer installments = 1;

    @Schema(description = "ID do produto de retoma (BuyBack) previamente cadastrado", example = "15")
    private Long buybackProductId;
}