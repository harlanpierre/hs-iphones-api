package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class ServiceOrderRequestDTO {

    @Schema(description = "ID do cliente dono do aparelho", example = "5")
    @NotNull(message = "ID do cliente é obrigatório")
    private Long clientId;

    @Schema(description = "Modelo do aparelho deixado na loja", example = "iPhone 13 Pro Max")
    @NotBlank(message = "O modelo do aparelho é obrigatório")
    private String deviceModel;

    @Schema(description = "IMEI ou Número de Série do aparelho do cliente", example = "354123456789012")
    private String deviceImeiSerial;

    @Schema(description = "Problema relatado pelo cliente na entrada", example = "Aparelho não carrega e bateria descarrega rápido")
    @NotBlank(message = "O relato do problema é obrigatório")
    private String reportedIssue;
}