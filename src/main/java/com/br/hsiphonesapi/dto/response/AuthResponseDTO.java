package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Resposta de autenticação com token JWT")
public class AuthResponseDTO {

    @Schema(description = "Token JWT para autenticação nas rotas protegidas", example = "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJoYXJsYW4uYWRtaW4iLCJyb2xlIjoiUk9MRV9BRE1JTiIsImlhdCI6MTcxMTQwMDAwMCwiZXhwIjoxNzExNDg2NDAwfQ.abc123")
    private String token;

    @Schema(description = "Nome de usuário", example = "harlan.admin")
    private String username;

    @Schema(description = "Nome completo do usuário", example = "Harlan Silva")
    private String name;

    @Schema(description = "Perfil de acesso", example = "ADMIN")
    private String role;
}
