package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.PaymentRequestDTO;
import com.br.hsiphonesapi.dto.request.SaleItemRequestDTO;
import com.br.hsiphonesapi.dto.request.SaleRequestDTO;
import com.br.hsiphonesapi.dto.response.SaleResponseDTO;
import com.br.hsiphonesapi.mapper.SaleMapper;
import com.br.hsiphonesapi.model.*;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import com.br.hsiphonesapi.model.enums.SaleStatus;
import com.br.hsiphonesapi.repository.ClientRepository;
import com.br.hsiphonesapi.repository.ProductRepository;
import com.br.hsiphonesapi.repository.SaleRepository;
import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.service.PlanUsageService;
import com.br.hsiphonesapi.service.SaleService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SaleServiceImpl implements SaleService {

    private final SaleRepository saleRepository;
    private final ProductRepository productRepository;
    private final ClientRepository clientRepository;
    private final SaleMapper saleMapper;
    private final PlanUsageService planUsageService;

    @Override
    public Page<SaleResponseDTO> findAll(Pageable pageable) {
        return saleRepository.findAll(pageable).map(saleMapper::toResponse);
    }

    @Override
    public Page<SaleResponseDTO> findByFilters(SaleStatus status, Long clientId, LocalDateTime dateFrom, LocalDateTime dateTo, Pageable pageable) {
        String statusStr = status != null ? status.name() : null;
        return saleRepository.findByFilters(TenantContext.getTenantId(), statusStr, clientId, dateFrom, dateTo, pageable).map(saleMapper::toResponse);
    }

    @Override
    public SaleResponseDTO findById(Long id) {
        Sale sale = saleRepository.findByIdWithDetails(id)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada."));
        return saleMapper.toResponse(sale);
    }

    // FLUXO 1: ORÇAMENTO (Não desconta estoque, não exige pagamento)
    @Override
    @Transactional
    public SaleResponseDTO createBudget(SaleRequestDTO dto) {
        planUsageService.checkCanCreateSale();
        Sale sale = buildBaseSale(dto);
        sale.setStatus(SaleStatus.ORCAMENTO);

        processItems(dto.getItems(), sale, false); // false = não deduz estoque agora

        Sale saved = saleRepository.save(sale);
        log.info("Orçamento criado: id={}, cliente={}, total={}", saved.getId(), dto.getClientId(), saved.getTotalAmount());
        return saleMapper.toResponse(saved);
    }

    // FLUXO 2: VENDA DIRETA / PDV (Já desconta estoque, valida pagamento)
    @Override
    @Transactional
    public SaleResponseDTO createDirectCheckout(SaleRequestDTO dto) {
        planUsageService.checkCanCreateSale();
        Sale sale = buildBaseSale(dto);
        sale.setStatus(SaleStatus.CONCLUIDO);

        processItems(dto.getItems(), sale, true); // true = deduz estoque e valida IMEI
        processPayments(dto.getPayments(), sale);

        sale.setCompletedAt(LocalDateTime.now());
        Sale saved = saleRepository.save(sale);
        log.info("Venda direta concluída: id={}, cliente={}, total={}", saved.getId(), dto.getClientId(), saved.getNetAmount());
        return saleMapper.toResponse(saved);
    }

    // MÁQUINA DE ESTADOS: Finalizar um orçamento/reserva existente
    @Override
    @Transactional
    public SaleResponseDTO payAndCompleteSale(Long saleId, List<PaymentRequestDTO> payments) {
        Sale sale = saleRepository.findByIdWithDetails(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));

        if (sale.getStatus() == SaleStatus.CONCLUIDO || sale.getStatus() == SaleStatus.CANCELADO) {
            throw new IllegalArgumentException("Esta venda já está concluída ou cancelada.");
        }

        // Se estava apenas como orçamento, precisamos deduzir o estoque agora
        if (sale.getStatus() == SaleStatus.ORCAMENTO) {
            reserveStockForExistingSale(sale);
        }

        processPayments(payments, sale);
        sale.setStatus(SaleStatus.CONCLUIDO);
        sale.setCompletedAt(LocalDateTime.now());

        log.info("Venda finalizada via pagamento: id={}, total={}", saleId, sale.getNetAmount());
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    // MÁQUINA DE ESTADOS: Apenas reservar (cliente disse que vai pagar depois)
    @Override
    @Transactional
    public SaleResponseDTO reserveSale(Long saleId) {
        Sale sale = saleRepository.findByIdWithDetails(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));

        if (sale.getStatus() != SaleStatus.ORCAMENTO) {
            throw new IllegalArgumentException("Apenas orçamentos podem ser reservados.");
        }

        reserveStockForExistingSale(sale);
        sale.setStatus(SaleStatus.RESERVADO);
        log.info("Venda reservada: id={}", saleId);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    // MÁQUINA DE ESTADOS: Cancelar venda (devolve estoque se já foi deduzido)
    @Override
    @Transactional
    public SaleResponseDTO cancelSale(Long saleId) {
        Sale sale = saleRepository.findByIdWithDetails(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada."));

        if (sale.getStatus() == SaleStatus.CANCELADO) {
            throw new IllegalArgumentException("Esta venda já está cancelada.");
        }
        if (sale.getStatus() == SaleStatus.DEVOLVIDO) {
            throw new IllegalArgumentException("Não é possível cancelar uma venda já devolvida.");
        }

        // Devolve estoque se a venda já havia deduzido (RESERVADO ou CONCLUIDO)
        if (sale.getStatus() == SaleStatus.RESERVADO || sale.getStatus() == SaleStatus.CONCLUIDO) {
            restoreStockFromSale(sale);
        }

        sale.setStatus(SaleStatus.CANCELADO);
        sale.setCompletedAt(LocalDateTime.now());
        log.info("Venda cancelada: id={}", saleId);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    // MÁQUINA DE ESTADOS: Registrar devolução (apenas de venda concluída)
    @Override
    @Transactional
    public SaleResponseDTO returnSale(Long saleId) {
        Sale sale = saleRepository.findByIdWithDetails(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada."));

        if (sale.getStatus() != SaleStatus.CONCLUIDO) {
            throw new IllegalArgumentException("Apenas vendas concluídas podem ser devolvidas.");
        }

        restoreStockFromSale(sale);
        sale.setStatus(SaleStatus.DEVOLVIDO);
        log.info("Venda devolvida: id={}", saleId);
        return saleMapper.toResponse(saleRepository.save(sale));
    }

    // --- MÉTODOS AUXILIARES PRIVADOS ---

    private Sale buildBaseSale(SaleRequestDTO dto) {
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado"));

        Sale sale = new Sale();
        sale.setClient(client);
        sale.setSellerName(dto.getSellerName());
        sale.setNotes(dto.getNotes());
        sale.setDiscountAmount(dto.getDiscountAmount() != null ? dto.getDiscountAmount() : BigDecimal.ZERO);
        return sale;
    }

    private void processItems(List<SaleItemRequestDTO> itemDTOs, Sale sale, boolean deductStock) {
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (SaleItemRequestDTO itemDto : itemDTOs) {
            Product product = productRepository.findById(itemDto.getProductId())
                    .orElseThrow(() -> new EntityNotFoundException("Produto não encontrado"));

            if (deductStock) {
                if (product.getQuantity() < itemDto.getQuantity() || product.getStatus() != ProductStatus.DISPONIVEL) {
                    throw new IllegalArgumentException("Stock insuficiente para o produto: " + product.getName());
                }

                // Validação rigorosa de IMEI para venda direta
                if (product.getCategory() == ProductCategory.CELULAR) {
                    if (itemDto.getImei() == null || itemDto.getImei().isBlank()) {
                        throw new IllegalArgumentException("O IMEI é obrigatório para venda de celulares.");
                    }
                    if (!product.getImeis().contains(itemDto.getImei())) {
                        throw new IllegalArgumentException("O IMEI informado não está disponível para este produto.");
                    }
                    // A remoção foi retirada, conforme você solicitou na regra anterior,
                    // contando com a máquina de estado do produto.
                }

                product.setQuantity(product.getQuantity() - itemDto.getQuantity());
                if (product.getQuantity() == 0) {
                    product.setStatus(ProductStatus.VENDIDO);
                }
                productRepository.save(product);
            }

            boolean isFreebie = itemDto.getIsFreebie() != null && itemDto.getIsFreebie();
            BigDecimal unitPrice = isFreebie ? BigDecimal.ZERO : product.getSalePrice();
            BigDecimal subtotal = unitPrice.multiply(BigDecimal.valueOf(itemDto.getQuantity()));

            totalAmount = totalAmount.add(subtotal);

            SaleItem saleItem = SaleItem.builder()
                    .sale(sale)
                    .product(product)
                    .imei(itemDto.getImei())
                    .quantity(itemDto.getQuantity())
                    .unitPrice(unitPrice) // Salva 0.00 se for brinde
                    .subtotal(subtotal)
                    .isFreebie(isFreebie)
                    .warrantyEndDate(LocalDate.now().plusDays(product.getWarrantyDays() != null ? product.getWarrantyDays() : 90))
                    .build();

            sale.getItems().add(saleItem);
        }

        sale.setTotalAmount(totalAmount);
        sale.setNetAmount(totalAmount.subtract(sale.getDiscountAmount()));
    }

    private void processPayments(List<PaymentRequestDTO> payments, Sale sale) {
        if (payments == null || payments.isEmpty()) {
            throw new IllegalArgumentException("Para concluir a venda, é necessário informar os pagamentos.");
        }

        BigDecimal totalPaid = BigDecimal.ZERO;

        for (PaymentRequestDTO payDto : payments) {
            Payment payment = Payment.builder()
                    .sale(sale)
                    .method(payDto.getMethod())
                    .amount(payDto.getAmount())
                    .installments(payDto.getInstallments())
                    .build();

            if (payDto.getBuybackProductId() != null) {
                Product buybackProd = productRepository.findById(payDto.getBuybackProductId())
                        .orElseThrow(() -> new EntityNotFoundException("Aparelho de retoma não encontrado"));
                payment.setBuybackProduct(buybackProd);
            }

            sale.getPayments().add(payment);
            totalPaid = totalPaid.add(payDto.getAmount());
        }

        if (totalPaid.compareTo(sale.getNetAmount()) < 0) {
            throw new IllegalArgumentException("O valor pago é menor que o total líquido da venda.");
        }
    }

    private void restoreStockFromSale(Sale sale) {
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            product.setQuantity(product.getQuantity() + item.getQuantity());
            if (product.getStatus() == ProductStatus.VENDIDO || product.getStatus() == ProductStatus.RESERVADO) {
                product.setStatus(ProductStatus.DISPONIVEL);
            }
            productRepository.save(product);
        }
    }

    private void reserveStockForExistingSale(Sale sale) {
        for (SaleItem item : sale.getItems()) {
            Product product = item.getProduct();
            if (product.getQuantity() < item.getQuantity() || product.getStatus() != ProductStatus.DISPONIVEL) {
                throw new IllegalArgumentException("Produto indisponível no momento: " + product.getName());
            }
            product.setQuantity(product.getQuantity() - item.getQuantity());
            if (product.getQuantity() == 0) {
                product.setStatus(ProductStatus.RESERVADO);
            }
            productRepository.save(product);
        }
    }
}