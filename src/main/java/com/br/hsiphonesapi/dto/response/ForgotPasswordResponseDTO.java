package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Resposta da solicitação de recuperação de senha")
public class ForgotPasswordResponseDTO {

    @Schema(description = "Mensagem de confirmação", example = "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação.")
    private String message;
}
