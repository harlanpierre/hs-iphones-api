package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.ProductStatusHistory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import com.br.hsiphonesapi.repository.ProductStatusHistoryRepository;
import com.br.hsiphonesapi.service.ProductStatusHistoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class ProductStatusHistoryServiceImpl implements ProductStatusHistoryService {

    private final ProductStatusHistoryRepository repository;

    // Propagation.MANDATORY garante que este método só seja chamado dentro de uma transação existente
    @Override
    @Transactional(propagation = Propagation.MANDATORY)
    public void logStatusChange(Product product, ProductStatus oldStatus, ProductStatus newStatus, String reason) {
        if (oldStatus == newStatus) return;

        ProductStatusHistory history = ProductStatusHistory.builder()
                .product(product)
                .oldStatus(oldStatus)
                .newStatus(newStatus)
                .reason(reason)
                .build();

        repository.save(history);
    }
}
