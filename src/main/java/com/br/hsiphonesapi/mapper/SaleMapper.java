package com.br.hsiphonesapi.mapper;

import com.br.hsiphonesapi.dto.response.PaymentResponseDTO;
import com.br.hsiphonesapi.dto.response.SaleItemResponseDTO;
import com.br.hsiphonesapi.dto.response.SaleResponseDTO;
import com.br.hsiphonesapi.model.Sale;
import org.springframework.stereotype.Component;
import java.util.stream.Collectors;

@Component
public class SaleMapper {

    public SaleResponseDTO toResponse(Sale entity) {
        return SaleResponseDTO.builder()
                .id(entity.getId())
                .clientName(entity.getClient().getName())
                .sellerName(entity.getSellerName())
                .status(entity.getStatus())
                .totalAmount(entity.getTotalAmount())
                .discountAmount(entity.getDiscountAmount())
                .netAmount(entity.getNetAmount())
                .notes(entity.getNotes())
                .createdAt(entity.getCreatedAt())
                .items(entity.getItems().stream().map(item ->
                        SaleItemResponseDTO.builder()
                                .id(item.getId())
                                .productName(item.getProduct().getName())
                                .sku(item.getProduct().getSku())
                                .imei(item.getImei())
                                .quantity(item.getQuantity())
                                .subtotal(item.getSubtotal())
                                .warrantyEndDate(item.getWarrantyEndDate())
                                .build()
                ).collect(Collectors.toList()))
                .payments(entity.getPayments().stream().map(payment ->
                        PaymentResponseDTO.builder()
                                .id(payment.getId())
                                .method(payment.getMethod())
                                .amount(payment.getAmount())
                                .installments(payment.getInstallments())
                                .buybackProductName(payment.getBuybackProduct() != null ? payment.getBuybackProduct().getName() : null)
                                .build()
                ).collect(Collectors.toList()))
                .build();
    }
}