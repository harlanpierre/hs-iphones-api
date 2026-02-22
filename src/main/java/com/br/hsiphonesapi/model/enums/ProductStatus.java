package com.br.hsiphonesapi.model.enums;

public enum ProductStatus {
    DISPONIVEL,
    VENDIDO,
    RESERVADO,
    DEVOLVIDO_AO_FORNECEDOR,
    IN_REPAIR, // Em manutenção (BuyBack)
    CONSUMED_IN_BUYBACK_REPAIR, // Peça usada em um BuyBack interno
    CONSUMED_IN_SERVICE_ORDER // Peça usada em um conserto de cliente
}
