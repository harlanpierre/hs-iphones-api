package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

public interface ProductStatusHistoryService {
    // Propagation.MANDATORY garante que este método só seja chamado dentro de uma transação existente
    @Transactional(propagation = Propagation.MANDATORY)
    void logStatusChange(Product product, ProductStatus oldStatus, ProductStatus newStatus, String reason);
}
