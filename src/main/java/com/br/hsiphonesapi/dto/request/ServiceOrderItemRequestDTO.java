package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ServiceOrderItemRequestDTO {

    @Schema(description = "ID do produto/peça a ser consumido no conserto", example = "25")
    @NotNull(message = "O ID da peça é obrigatório")
    private Long productId;

    @Schema(description = "Quantidade de peças a utilizar", example = "1")
    @NotNull(message = "A quantidade é obrigatória")
    @Min(value = 1, message = "A quantidade deve ser no mínimo 1")
    private Integer quantity;
}