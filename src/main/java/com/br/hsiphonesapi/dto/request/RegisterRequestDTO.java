package com.br.hsiphonesapi.dto.request;

import com.br.hsiphonesapi.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Schema(description = "Dados para registro de novo usuário")
public class RegisterRequestDTO {

    @NotBlank(message = "O usuário é obrigatório.")
    @Size(min = 3, max = 100, message = "O usuário deve ter entre 3 e 100 caracteres.")
    @Schema(description = "Nome de usuário (login)", example = "harlan.admin")
    private String username;

    @NotBlank(message = "A senha é obrigatória.")
    @Size(min = 6, message = "A senha deve ter no mínimo 6 caracteres.")
    @Schema(description = "Senha do usuário", example = "123456")
    private String password;

    @NotBlank(message = "O nome é obrigatório.")
    @Schema(description = "Nome completo do usuário", example = "Harlan Silva")
    private String name;

    @NotNull(message = "O perfil (role) é obrigatório.")
    @Schema(description = "Perfil de acesso do usuário", example = "ADMIN")
    private UserRole role;

    @NotBlank(message = "O e-mail é obrigatório.")
    @jakarta.validation.constraints.Email(message = "E-mail inválido.")
    @Schema(description = "E-mail do administrador", example = "admin@minhaloja.com")
    private String email;

    @NotBlank(message = "O nome da empresa é obrigatório.")
    @Schema(description = "Nome da empresa/loja (cria um novo tenant)", example = "Minha Loja de iPhones")
    private String tenantName;
}
