package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.response.SubscriptionResponseDTO;
import com.br.hsiphonesapi.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/subscription")
@Tag(name = "Assinatura", description = "Gerenciamento da assinatura do tenant")
public class SubscriptionController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    @Operation(summary = "Assinatura atual", description = "Retorna a assinatura do tenant com uso atual e limites do plano.")
    public ResponseEntity<SubscriptionResponseDTO> getCurrentSubscription() {
        return ResponseEntity.ok(subscriptionService.getCurrentSubscription());
    }

    @PutMapping("/change-plan/{planId}")
    @PreAuthorize("hasRole('ADMIN')")
    @Operation(summary = "Trocar plano", description = "Altera o plano da assinatura. Apenas ADMIN.")
    public ResponseEntity<SubscriptionResponseDTO> changePlan(@PathVariable Long planId) {
        return ResponseEntity.ok(subscriptionService.changePlan(planId));
    }
}
