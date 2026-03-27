package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para autenticação do usuário")
public class LoginRequestDTO {

    @NotBlank(message = "O usuário é obrigatório.")
    @Schema(description = "Nome de usuário", example = "harlan.admin")
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;
}
