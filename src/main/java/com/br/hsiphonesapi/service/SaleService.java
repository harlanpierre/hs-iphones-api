package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.PaymentRequestDTO;
import com.br.hsiphonesapi.dto.request.SaleRequestDTO;
import com.br.hsiphonesapi.dto.response.SaleResponseDTO;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface SaleService {
    // FLUXO 1: ORÇAMENTO (Não desconta estoque, não exige pagamento)
    @Transactional
    SaleResponseDTO createBudget(SaleRequestDTO dto);

    // FLUXO 2: VENDA DIRETA / PDV (Já desconta estoque, valida pagamento)
    @Transactional
    SaleResponseDTO createDirectCheckout(SaleRequestDTO dto);

    // MÁQUINA DE ESTADOS: Finalizar um orçamento/reserva existente
    @Transactional
    SaleResponseDTO payAndCompleteSale(Long saleId, List<PaymentRequestDTO> payments);

    // MÁQUINA DE ESTADOS: Apenas reservar (cliente disse que vai pagar depois)
    @Transactional
    SaleResponseDTO reserveSale(Long saleId);
}
