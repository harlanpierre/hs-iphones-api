package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.InternalRepairRequestDTO;
import com.br.hsiphonesapi.dto.response.InternalRepairResponseDTO;
import com.br.hsiphonesapi.service.InternalRepairService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/repairs/internal")
@RequiredArgsConstructor
@Tag(name = "Reparos Internos", description = "Reparos de aparelhos BuyBack")
public class RepairController {

    private final InternalRepairService service;

    @PostMapping
    @Operation(summary = "Registrar uso de peça num aparelho de estoque")
    public ResponseEntity<InternalRepairResponseDTO> registerRepair(@RequestBody @Valid InternalRepairRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.registerBuybackRepair(dto));
    }

    @PutMapping("/{phoneId}/finish")
    @Operation(summary = "Finalizar reparo e devolver ao estoque disponível")
    public ResponseEntity<Void> finishRepair(@PathVariable Long phoneId) {
        service.finishBuybackRepair(phoneId);
        return ResponseEntity.noContent().build();
    }
}
