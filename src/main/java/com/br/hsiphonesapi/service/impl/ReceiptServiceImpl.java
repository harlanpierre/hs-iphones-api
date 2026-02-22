package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.service.ReceiptService;

import com.br.hsiphonesapi.model.Sale;
import com.br.hsiphonesapi.repository.SaleRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
@RequiredArgsConstructor
public class ReceiptServiceImpl implements ReceiptService {

    private final SaleRepository saleRepository;
    private final TemplateEngine templateEngine;

    @Override
    public String generateReceiptHtml(Long saleId) {
        Sale sale = saleRepository.findById(saleId)
                .orElseThrow(() -> new EntityNotFoundException("Venda não encontrada"));

        Context context = new Context();
        context.setVariable("sale", sale);
        context.setVariable("client", sale.getClient());
        context.setVariable("items", sale.getItems());

        // Retorna a string do HTML renderizado buscando o arquivo "receipt.html" em src/main/resources/templates/
        return templateEngine.process("receipt", context);
    }
}
