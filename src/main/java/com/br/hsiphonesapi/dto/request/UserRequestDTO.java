package com.br.hsiphonesapi.dto.request;

import com.br.hsiphonesapi.model.enums.UserRole;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
public class UserRequestDTO {

    @Schema(description = "Nome de usuário para login", example = "joao.silva")
    @NotBlank(message = "Username é obrigatório")
    @Size(min = 3, max = 100, message = "Username deve ter entre 3 e 100 caracteres")
    private String username;

    @Schema(description = "E-mail do usuário", example = "joao@empresa.com")
    @jakarta.validation.constraints.Email(message = "E-mail inválido")
    private String email;

    @Schema(description = "Senha do usuário (obrigatória na criação, opcional na atualização)", example = "senha123")
    @Size(min = 6, message = "Senha deve ter no mínimo 6 caracteres")
    private String password;

    @Schema(description = "Nome completo do usuário", example = "João da Silva")
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Schema(description = "Perfil de acesso do usuário", example = "VENDEDOR")
    @NotNull(message = "Perfil (role) é obrigatório")
    private UserRole role;
}
