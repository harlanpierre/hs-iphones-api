package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.ServiceOrderItemRequestDTO;
import com.br.hsiphonesapi.dto.request.ServiceOrderRequestDTO;
import com.br.hsiphonesapi.dto.response.ServiceOrderResponseDTO;
import com.br.hsiphonesapi.model.enums.ServiceOrderStatus;
import com.br.hsiphonesapi.service.ServiceOrderService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/services/os")
@RequiredArgsConstructor
@Tag(name = "Assistência Técnica (OS)", description = "Ordens de Serviço para clientes externos")
public class ServiceOrderController {

    private final ServiceOrderService service;

    @GetMapping
    @Operation(summary = "Listar Ordens de Serviço (paginado com filtros)", description = "Filtre por status e/ou clientId. Ambos opcionais.")
    public ResponseEntity<Page<ServiceOrderResponseDTO>> findAll(
            @RequestParam(required = false) ServiceOrderStatus status,
            @RequestParam(required = false) Long clientId,
            @PageableDefault(size = 20) Pageable pageable) {
        if (status != null || clientId != null) {
            return ResponseEntity.ok(service.findByFilters(status, clientId, pageable));
        }
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar Ordem de Serviço por ID")
    public ResponseEntity<ServiceOrderResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping
    @Operation(summary = "Abrir nova Ordem de Serviço", description = "Cria uma O.S. vinculada a um cliente com o status inicial RECEIVED.")
    public ResponseEntity<ServiceOrderResponseDTO> createOs(@RequestBody @Valid ServiceOrderRequestDTO dto) {
        ServiceOrderResponseDTO response = service.createOS(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{id}/parts")
    @Operation(summary = "Adicionar peça consumida na OS (Deduz Estoque)")
    public ResponseEntity<Void> addPart(
            @PathVariable Long id,
            @RequestBody @Valid ServiceOrderItemRequestDTO dto) {

        service.addPartToOS(id, dto.getProductId(), dto.getQuantity());
        return ResponseEntity.ok().build();
    }

    @PutMapping("/{id}/status")
    @Operation(summary = "Avançar fluxo da Assistência (Aguardando Aprovação, Em Conserto, Pronto, etc)")
    public ResponseEntity<Void> changeStatus(@PathVariable Long id, @RequestParam ServiceOrderStatus status) {
        service.changeOsStatus(id, status);
        return ResponseEntity.ok().build();
    }
}