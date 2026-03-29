package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
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
public class UserResponseDTO {

    @Schema(description = "ID do usuário", example = "1")
    private Long id;

    @Schema(description = "Nome de usuário", example = "joao.silva")
    private String username;

    @Schema(description = "Nome completo", example = "João da Silva")
    private String name;

    @Schema(description = "E-mail", example = "joao@empresa.com")
    private String email;

    @Schema(description = "Perfil de acesso", example = "VENDEDOR")
    private String role;

    @Schema(description = "Se o usuário está ativo", example = "true")
    private Boolean active;

    @Schema(description = "Data de criação", example = "2026-03-28T10:30:00")
    private String createdAt;
}
