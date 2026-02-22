package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.ServiceOrderRequestDTO;
import com.br.hsiphonesapi.dto.response.ServiceOrderResponseDTO;
import com.br.hsiphonesapi.mapper.ServiceOrderMapper;
import com.br.hsiphonesapi.model.Client;
import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.ServiceOrder;
import com.br.hsiphonesapi.model.ServiceOrderItem;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import com.br.hsiphonesapi.model.enums.ServiceOrderStatus;
import com.br.hsiphonesapi.repository.ClientRepository;
import com.br.hsiphonesapi.repository.ProductRepository;
import com.br.hsiphonesapi.repository.ServiceOrderRepository;
import com.br.hsiphonesapi.service.ProductStatusHistoryService;
import com.br.hsiphonesapi.service.ServiceOrderService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ServiceOrderServiceImpl implements ServiceOrderService {

    private final ClientRepository clientRepository;
    private final ServiceOrderMapper mapper;
    private final ServiceOrderRepository osRepository;
    private final ProductRepository productRepository;
    private final ProductStatusHistoryService historyService;

    @Override
    @Transactional
    public ServiceOrderResponseDTO createOS(ServiceOrderRequestDTO dto) {
        // 1. Busca o cliente na base de dados
        Client client = clientRepository.findById(dto.getClientId())
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        // 2. Monta a entidade
        ServiceOrder os = new ServiceOrder();
        os.setClient(client);
        os.setDeviceModel(dto.getDeviceModel());
        os.setDeviceImeiSerial(dto.getDeviceImeiSerial());
        os.setReportedIssue(dto.getReportedIssue());
        os.setStatus(ServiceOrderStatus.RECEIVED); // Status inicial padrão

        // 3. Salva no banco e já retorna mapeado para o DTO
        return mapper.toResponse(osRepository.save(os));
    }

    @Override
    @Transactional
    public void addPartToOS(Long osId, Long partId, Integer quantity) {
        ServiceOrder os = osRepository.findById(osId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Serviço não encontrada."));

        // ALTERADO AQUI: Busca garantindo que é uma PEÇA
        Product part = productRepository.findByIdAndCategory(partId, ProductCategory.PECA)
                .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada ou o ID informado não pertence a uma peça válida."));

        if (os.getStatus() == ServiceOrderStatus.DELIVERED || os.getStatus() == ServiceOrderStatus.CANCELED) {
            throw new IllegalArgumentException("OS finalizada não pode receber peças.");
        }

        if (part.getQuantity() < quantity) {
            throw new IllegalArgumentException("Estoque insuficiente.");
        }

        // Baixa de estoque e registro no histórico
        ProductStatus oldStatus = part.getStatus();
        part.setQuantity(part.getQuantity() - quantity);
        if (part.getQuantity() == 0) {
            part.setStatus(ProductStatus.CONSUMED_IN_SERVICE_ORDER);
            historyService.logStatusChange(part, oldStatus, ProductStatus.CONSUMED_IN_SERVICE_ORDER, "Consumido na OS: " + os.getId());
        }
        productRepository.save(part);

        // Adiciona item na OS e recalcula totais
        BigDecimal subtotal = part.getSalePrice().multiply(BigDecimal.valueOf(quantity));
        ServiceOrderItem item = ServiceOrderItem.builder()
                .serviceOrder(os)
                .product(part)
                .quantity(quantity)
                .unitPrice(part.getSalePrice())
                .subtotal(subtotal)
                .build();

        os.getItems().add(item);
        os.setPartsCost(os.getPartsCost().add(subtotal));
        os.setTotalAmount(os.getPartsCost().add(os.getLaborCost()).subtract(os.getDiscountAmount()));

        osRepository.save(os);
    }

    @Override
    @Transactional
    public void changeOsStatus(Long osId, ServiceOrderStatus newStatus) {
        ServiceOrder os = osRepository.findById(osId)
                .orElseThrow(() -> new EntityNotFoundException("Ordem de Serviço não encontrada."));

        ServiceOrderStatus oldStatus = os.getStatus();

        // 1. Bloqueia alterações em OS já finalizadas
        if (oldStatus == ServiceOrderStatus.DELIVERED || oldStatus == ServiceOrderStatus.CANCELED) {
            throw new IllegalArgumentException("Não é possível alterar o status de uma OS já Entregue ou Cancelada.");
        }

        // 2. Regra de Entrega
        if (newStatus == ServiceOrderStatus.DELIVERED) {
            if (oldStatus != ServiceOrderStatus.READY_FOR_PICKUP) {
                throw new IllegalArgumentException("O aparelho deve constar como Pronto (READY_FOR_PICKUP) antes de ser entregue.");
            }
            os.setCompletedAt(LocalDateTime.now());
        }

        // 3. Regra de Cancelamento / Rejeição de Orçamento
        if (newStatus == ServiceOrderStatus.CANCELED || newStatus == ServiceOrderStatus.REJECTED) {
            restoreOsPartsToStock(os); // Devolve as peças fisicamente
            os.setCompletedAt(LocalDateTime.now()); // Finaliza o ciclo de vida da OS
        }

        os.setStatus(newStatus);
        osRepository.save(os);
    }

    // Método privado auxiliar para devolver peças em caso de cancelamento
    private void restoreOsPartsToStock(ServiceOrder os) {
        if (os.getItems() != null && !os.getItems().isEmpty()) {
            for (ServiceOrderItem item : os.getItems()) {
                Product part = item.getProduct();
                ProductStatus oldPartStatus = part.getStatus();

                // Restaura a quantidade
                part.setQuantity(part.getQuantity() + item.getQuantity());
                part.setStatus(ProductStatus.DISPONIVEL);

                // Registra na auditoria que a peça voltou pro estoque
                historyService.logStatusChange(part, oldPartStatus, ProductStatus.DISPONIVEL,
                        "Peça devolvida ao estoque devido a cancelamento/rejeição da OS: " + os.getId());

                productRepository.save(part);
            }

            // Zera o custo de peças da OS para não gerar falso faturamento
            os.setPartsCost(BigDecimal.ZERO);

            // Recalcula o total (Neste caso, dependendo da sua regra, você pode ou não cobrar a mão de obra/taxa de orçamento)
            BigDecimal newTotal = os.getLaborCost().subtract(os.getDiscountAmount());
            os.setTotalAmount(newTotal.compareTo(BigDecimal.ZERO) > 0 ? newTotal : BigDecimal.ZERO);

            // Limpa a lista de itens utilizados
            os.getItems().clear();
        }
    }
}
