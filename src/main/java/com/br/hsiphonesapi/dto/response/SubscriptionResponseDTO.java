package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@Builder
@AllArgsConstructor
@Schema(description = "Detalhes da assinatura do tenant")
public class SubscriptionResponseDTO {

    @Schema(description = "ID da assinatura")
    private Long id;

    @Schema(description = "Status da assinatura", example = "ACTIVE")
    private String status;

    @Schema(description = "Data de início")
    private LocalDateTime startedAt;

    @Schema(description = "Data de expiração")
    private LocalDateTime expiresAt;

    @Schema(description = "Plano atual")
    private PlanResponseDTO plan;

    @Schema(description = "Uso atual do tenant")
    private UsageDTO usage;

    @Getter
    @Builder
    @AllArgsConstructor
    @Schema(description = "Uso atual por recurso")
    public static class UsageDTO {
        private long products;
        private long clients;
        private long users;
        private long salesThisMonth;
        private long serviceOrdersThisMonth;
    }
}
