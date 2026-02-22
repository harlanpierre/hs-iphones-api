package com.br.hsiphonesapi.mapper;

import com.br.hsiphonesapi.dto.request.ProductRequestDTO;
import com.br.hsiphonesapi.dto.response.ProductResponseDTO;
import com.br.hsiphonesapi.model.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;

@RequiredArgsConstructor
@Component
public class ProductMapper {

    private final SupplierMapper supplierMapper;
    private final ClientMapper clientMapper;

    public Product toEntity(ProductRequestDTO dto) {
        return Product.builder()
                .name(dto.getName())
                .description(dto.getDescription())
                .sku(dto.getSku())
                .category(dto.getCategory())
                .purchasePrice(dto.getPurchasePrice())
                .salePrice(dto.getSalePrice())
                .quantity(dto.getQuantity())
                .minStock(dto.getMinStock())
                .imeis(dto.getImeis()) // Passa a lista direto
                .compatibleModel(dto.getCompatibleModel())
                .warrantyDays(dto.getWarrantyDays() != null ? dto.getWarrantyDays() : 90)
                .supplierWarrantyStartDate(dto.getSupplierWarrantyStartDate())
                .supplierWarrantyEndDate(dto.getSupplierWarrantyEndDate())
                .repairCost(dto.getRepairCost() != null ? dto.getRepairCost() : BigDecimal.ZERO)
                // Nota: Supplier e Client são setados na Service via ID
                .build();
    }

    public ProductResponseDTO toResponse(Product entity) {
        return ProductResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .description(entity.getDescription())
                .sku(entity.getSku())
                .category(entity.getCategory())
                .status(entity.getStatus()) // Importante mapear o status
                .purchasePrice(entity.getPurchasePrice())
                .salePrice(entity.getSalePrice())
                .quantity(entity.getQuantity())
                .minStock(entity.getMinStock())
                .imeis(entity.getImeis())
                .compatibleModel(entity.getCompatibleModel())
                .warrantyDays(entity.getWarrantyDays())
                .supplierWarrantyStartDate(entity.getSupplierWarrantyStartDate())
                .supplierWarrantyEndDate(entity.getSupplierWarrantyEndDate())
                .repairCost(entity.getRepairCost())

                // Mapeamento condicional: Se tem fornecedor, mapeia. Se tem cliente, mapeia.
                .supplier(entity.getSupplier() != null ? supplierMapper.toResponse(entity.getSupplier()) : null)
                .client(entity.getClient() != null ? clientMapper.toResponse(entity.getClient()) : null)

                .build();
    }
}