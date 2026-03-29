package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para redefinir senha com token")
public class ResetPasswordRequestDTO {

    @NotBlank(message = "O token é obrigatório.")
    @Schema(description = "Token de recuperação de senha")
    private String token;

    @NotBlank(message = "A nova senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    @Schema(description = "Nova senha do usuário", example = "novaSenha123")
    private String newPassword;
}
