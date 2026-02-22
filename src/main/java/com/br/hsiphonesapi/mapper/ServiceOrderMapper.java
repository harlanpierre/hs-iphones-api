package com.br.hsiphonesapi.mapper;

import com.br.hsiphonesapi.dto.response.ServiceOrderItemResponseDTO;
import com.br.hsiphonesapi.dto.response.ServiceOrderResponseDTO;
import com.br.hsiphonesapi.model.ServiceOrder;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.stream.Collectors;

@Component
public class ServiceOrderMapper {

    public ServiceOrderResponseDTO toResponse(ServiceOrder entity) {
        return ServiceOrderResponseDTO.builder()
                .id(entity.getId())
                .clientName(entity.getClient().getName())
                .deviceModel(entity.getDeviceModel())
                .reportedIssue(entity.getReportedIssue())
                .diagnostic(entity.getDiagnostic())
                .status(entity.getStatus())
                .laborCost(entity.getLaborCost())
                .partsCost(entity.getPartsCost())
                .discountAmount(entity.getDiscountAmount())
                .totalAmount(entity.getTotalAmount())
                .createdAt(entity.getCreatedAt())
                .completedAt(entity.getCompletedAt())
                .items(entity.getItems() != null ? entity.getItems().stream().map(item ->
                        ServiceOrderItemResponseDTO.builder()
                                .id(item.getId())
                                .productName(item.getProduct().getName())
                                .quantity(item.getQuantity())
                                .unitPrice(item.getUnitPrice())
                                .subtotal(item.getSubtotal())
                                .build()
                ).collect(Collectors.toList()) : Collections.emptyList())
                .build();
    }
}