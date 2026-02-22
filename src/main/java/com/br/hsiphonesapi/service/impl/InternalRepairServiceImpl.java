package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.InternalRepairPartRequestDTO;
import com.br.hsiphonesapi.dto.request.InternalRepairRequestDTO;
import com.br.hsiphonesapi.dto.response.InternalRepairPartResponseDTO;
import com.br.hsiphonesapi.dto.response.InternalRepairResponseDTO;
import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.ProductRepair;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import com.br.hsiphonesapi.repository.ProductRepairRepository;
import com.br.hsiphonesapi.repository.ProductRepository;
import com.br.hsiphonesapi.service.InternalRepairService;
import com.br.hsiphonesapi.service.ProductStatusHistoryService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class InternalRepairServiceImpl implements InternalRepairService {

    private final ProductRepairRepository repairRepository;
    private final ProductRepository productRepository;
    private final ProductStatusHistoryService historyService;

    @Override
    @Transactional
    public InternalRepairResponseDTO registerBuybackRepair(InternalRepairRequestDTO dto) {
        Product phone = productRepository.findByIdAndCategory(dto.getPhoneId(), ProductCategory.CELULAR)
                .orElseThrow(() -> new EntityNotFoundException("Celular não encontrado"));

        if (phone.getStatus() != ProductStatus.IN_REPAIR) {
            historyService.logStatusChange(phone, phone.getStatus(), ProductStatus.IN_REPAIR, "Início do processo de recondicionamento.");
            phone.setStatus(ProductStatus.IN_REPAIR);
        }

        BigDecimal totalPartsCost = BigDecimal.ZERO;
        List<InternalRepairPartResponseDTO> partResponses = new ArrayList<>();

        // 1. Processamento das Peças (Se houver)
        if (dto.getParts() != null && !dto.getParts().isEmpty()) {
            for (InternalRepairPartRequestDTO partDto : dto.getParts()) {
                Product part = productRepository.findByIdAndCategory(partDto.getPartId(), ProductCategory.PECA)
                        .orElseThrow(() -> new EntityNotFoundException("Peça não encontrada: ID " + partDto.getPartId()));

                if (part.getQuantity() < partDto.getQuantity()) {
                    throw new IllegalArgumentException("Estoque insuficiente para a peça: " + part.getName());
                }

                ProductStatus oldPartStatus = part.getStatus();
                part.setQuantity(part.getQuantity() - partDto.getQuantity());

                if (part.getQuantity() == 0) {
                    part.setStatus(ProductStatus.CONSUMED_IN_BUYBACK_REPAIR);
                    historyService.logStatusChange(part, oldPartStatus, ProductStatus.CONSUMED_IN_BUYBACK_REPAIR, "Consumido no reparo do BuyBack ID: " + phone.getId());
                }
                productRepository.save(part);

                BigDecimal partTotalCost = part.getPurchasePrice().multiply(BigDecimal.valueOf(partDto.getQuantity()));
                totalPartsCost = totalPartsCost.add(partTotalCost);

                // Grava o histórico de uso da PEÇA
                ProductRepair partRepair = ProductRepair.builder()
                        .phone(phone)
                        .part(part)
                        .quantity(partDto.getQuantity())
                        .partCost(part.getPurchasePrice())
                        .notes(dto.getNotes())
                        .build();
                repairRepository.save(partRepair);

                partResponses.add(InternalRepairPartResponseDTO.builder()
                        .partName(part.getName())
                        .quantity(partDto.getQuantity())
                        .partCost(part.getPurchasePrice())
                        .build());
            }
        }

        // 2. Processamento da Mão de Obra / Serviço Terceirizado
        BigDecimal laborCost = dto.getLaborCost() != null ? dto.getLaborCost() : BigDecimal.ZERO;
        if (laborCost.compareTo(BigDecimal.ZERO) > 0) {

            // Grava um registro exclusivo para a Mão de Obra na tabela
            ProductRepair laborRepair = ProductRepair.builder()
                    .phone(phone)
                    .laborCost(laborCost)
                    .laborDescription(dto.getLaborDescription())
                    .notes(dto.getNotes())
                    .build();
            repairRepository.save(laborRepair);
        }

        BigDecimal totalSessionCost = totalPartsCost.add(laborCost);

        // 3. Validação: A requisição não pode ser vazia
        if (totalSessionCost.compareTo(BigDecimal.ZERO) == 0) {
            throw new IllegalArgumentException("É necessário informar ao menos uma peça consumida ou um custo de mão de obra.");
        }

        // 4. Atualiza o custo acumulado do celular
        BigDecimal currentRepairCost = phone.getRepairCost() != null ? phone.getRepairCost() : BigDecimal.ZERO;
        phone.setRepairCost(currentRepairCost.add(totalSessionCost));
        productRepository.save(phone);

        // 5. Retorna o DTO com o resumo detalhado da operação
        return InternalRepairResponseDTO.builder()
                .phoneName(phone.getName())
                .totalPartsCost(totalPartsCost)
                .laborCost(laborCost)
                .totalSessionCost(totalSessionCost)
                .laborDescription(dto.getLaborDescription())
                .notes(dto.getNotes())
                .parts(partResponses)
                .build();
    }

    @Override
    @Transactional
    public void finishBuybackRepair(Long phoneId) {
        // 1. Busca o produto garantindo estritamente que é da categoria CELULAR
        Product phone = productRepository.findByIdAndCategory(phoneId, ProductCategory.CELULAR)
                .orElseThrow(() -> new EntityNotFoundException("Celular não encontrado ou o ID informado não pertence a um aparelho válido."));

        // 2. Validação da Máquina de Estados: O aparelho DEVE estar em reparo
        if (phone.getStatus() != ProductStatus.IN_REPAIR) {
            throw new IllegalArgumentException(
                    "Não é possível finalizar o reparo. O aparelho não está com o status 'IN_REPAIR'. Status atual: " + phone.getStatus()
            );
        }

        // 3. Atualiza o status e registra na trilha de auditoria
        ProductStatus oldStatus = phone.getStatus();
        phone.setStatus(ProductStatus.DISPONIVEL);

        historyService.logStatusChange(
                phone,
                oldStatus,
                ProductStatus.DISPONIVEL,
                "Reparo finalizado com sucesso. Aparelho recondicionado e pronto para venda."
        );

        // 4. Salva a alteração
        productRepository.save(phone);
    }
}
