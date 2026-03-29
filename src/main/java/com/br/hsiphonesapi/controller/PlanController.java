package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.response.PlanResponseDTO;
import com.br.hsiphonesapi.service.SubscriptionService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/plans")
@Tag(name = "Planos", description = "Consulta de planos de assinatura disponíveis")
public class PlanController {

    private final SubscriptionService subscriptionService;

    @GetMapping
    @Operation(summary = "Listar planos", description = "Retorna todos os planos ativos ordenados por preço.")
    public ResponseEntity<List<PlanResponseDTO>> listPlans() {
        return ResponseEntity.ok(subscriptionService.listPlans());
    }
}
