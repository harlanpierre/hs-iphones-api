package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.response.DashboardResponseDTO;
import com.br.hsiphonesapi.service.DashboardService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;

@RestController
@RequestMapping("/dashboard")
@RequiredArgsConstructor
@Tag(name = "Dashboard")
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping
    @Operation(summary = "Retorna metricas do dashboard para o mes/ano informado")
    public ResponseEntity<DashboardResponseDTO> getDashboard(
            @RequestParam(defaultValue = "0") int month,
            @RequestParam(defaultValue = "0") int year) {

        if (month <= 0 || month > 12) {
            month = LocalDate.now().getMonthValue();
        }
        if (year <= 0) {
            year = LocalDate.now().getYear();
        }

        return ResponseEntity.ok(dashboardService.getDashboard(month, year));
    }
}
