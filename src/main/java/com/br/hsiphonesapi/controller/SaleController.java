package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.request.PaymentRequestDTO;
import com.br.hsiphonesapi.dto.request.SaleRequestDTO;
import com.br.hsiphonesapi.dto.response.SaleResponseDTO;
import com.br.hsiphonesapi.service.ReceiptService;
import com.br.hsiphonesapi.service.impl.SaleServiceImpl;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/sales")
@Tag(name = "Vendas / PDV", description = "Gestão de Orçamentos e Vendas")
public class SaleController {

    private final SaleServiceImpl service;
    private final ReceiptService receiptService;

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

    @GetMapping(value = "/{id}/receipt", produces = org.springframework.http.MediaType.TEXT_HTML_VALUE)
    @Operation(summary = "Gerar Termo de Garantia/Recibo em HTML")
    public ResponseEntity<String> getReceipt(@PathVariable Long id) {
        return ResponseEntity.ok(receiptService.generateReceiptHtml(id));
    }
}