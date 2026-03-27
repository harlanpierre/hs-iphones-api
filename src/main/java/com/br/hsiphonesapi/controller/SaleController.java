package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.PaymentRequestDTO;
import com.br.hsiphonesapi.dto.request.SaleRequestDTO;
import com.br.hsiphonesapi.dto.response.SaleResponseDTO;
import com.br.hsiphonesapi.model.enums.SaleStatus;
import com.br.hsiphonesapi.service.ReceiptService;
import com.br.hsiphonesapi.service.SaleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales")
@Tag(name = "Vendas / PDV", description = "Gestão de Orçamentos e Vendas")
public class SaleController {

    private final SaleService service;
    private final ReceiptService receiptService;

    @GetMapping
    @Operation(summary = "Listar vendas (paginado com filtros)", description = "Filtre por status, clientId, dateFrom e dateTo. Todos opcionais.")
    public ResponseEntity<Page<SaleResponseDTO>> findAll(
            @RequestParam(required = false) SaleStatus status,
            @RequestParam(required = false) Long clientId,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateFrom,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime dateTo,
            @PageableDefault(size = 20) Pageable pageable) {
        if (status != null || clientId != null || dateFrom != null || dateTo != null) {
            return ResponseEntity.ok(service.findByFilters(status, clientId, dateFrom, dateTo, pageable));
        }
        return ResponseEntity.ok(service.findAll(pageable));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Buscar venda por ID")
    public ResponseEntity<SaleResponseDTO> findById(@PathVariable Long id) {
        return ResponseEntity.ok(service.findById(id));
    }

    @PostMapping("/budget")
    @Operation(summary = "Criar Orçamento", description = "Apenas calcula valores, não baixa estoque nem exige pagamento.")
    public ResponseEntity<SaleResponseDTO> createBudget(@RequestBody @Valid SaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createBudget(dto));
    }

    @PostMapping("/checkout")
    @Operation(summary = "Venda Direta (PDV)", description = "Cria a venda já concluída, deduz estoque e processa pagamentos.")
    public ResponseEntity<SaleResponseDTO> createDirectCheckout(@RequestBody @Valid SaleRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.createDirectCheckout(dto));
    }

    @PutMapping("/{id}/reserve")
    @Operation(summary = "Reservar Orçamento", description = "Muda de Orçamento para Reservado (deduz estoque temporalmente).")
    public ResponseEntity<SaleResponseDTO> reserveSale(@PathVariable Long id) {
        return ResponseEntity.ok(service.reserveSale(id));
    }

    @PutMapping("/{id}/pay")
    @Operation(summary = "Pagar e Concluir Venda", description = "Informa os pagamentos de um orçamento/reserva para finalizá-lo.")
    public ResponseEntity<SaleResponseDTO> payAndCompleteSale(
            @PathVariable Long id,
            @RequestBody @Valid List<PaymentRequestDTO> payments) {
        return ResponseEntity.ok(service.payAndCompleteSale(id, payments));
    }

    @PutMapping("/{id}/cancel")
    @Operation(summary = "Cancelar Venda", description = "Cancela a venda e devolve o estoque caso já tenha sido deduzido.")
    public ResponseEntity<SaleResponseDTO> cancelSale(@PathVariable Long id) {
        return ResponseEntity.ok(service.cancelSale(id));
    }

    @PutMapping("/{id}/return")
    @Operation(summary = "Registrar Devolução", description = "Registra a devolução de uma venda concluída e devolve o estoque.")
    public ResponseEntity<SaleResponseDTO> returnSale(@PathVariable Long id) {
        return ResponseEntity.ok(service.returnSale(id));
    }

    @GetMapping(value = "/{id}/receipt", produces = org.springframework.http.MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Gerar Termo de Garantia/Recibo em HTML")
    public ResponseEntity<String> getReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.generateReceiptHtml(id));
    }
}