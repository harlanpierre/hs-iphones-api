package com.br.hsiphonesapi.model.enums;

public enum ServiceOrderStatus {
    RECEIVED,          // Aparelho deu entrada
    IN_DIAGNOSIS,      // Técnico está avaliando
    AWAITING_APPROVAL, // Orçamento enviado ao cliente
    APPROVED,          // Cliente aprovou
    REJECTED,          // Cliente rejeitou (será devolvido)
    IN_PROGRESS,       // Sendo consertado
    READY_FOR_PICKUP,  // Conserto finalizado, aguardando retirada
    DELIVERED,         // Entregue e pago
    CANCELED
}