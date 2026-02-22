package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SaleItemRequestDTO {

    @Schema(description = "ID do produto vendido", example = "50")
    @NotNull(message = "ID do produto é obrigatório")
    private Long productId;

    @Schema(description = "IMEI exato do aparelho (Obrigatório para categoria CELULAR)", example = "354682090001234")
    private String imei;

    @Schema(description = "Quantidade comprada", example = "1")
    @NotNull
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantity;

    @Schema(description = "Indica se o item é um brinde (Sairá por R$ 0,00, mas baixará estoque)", example = "true")
    private Boolean isFreebie = false;
}