package com.br.hsiphonesapi.mapper;

import com.br.hsiphonesapi.dto.request.SupplierRequestDTO;
import com.br.hsiphonesapi.dto.response.SupplierResponseDTO;
import com.br.hsiphonesapi.model.Supplier;
import org.springframework.stereotype.Component;

@Component
public class SupplierMapper {

    public Supplier toEntity(SupplierRequestDTO dto) {
        return Supplier.builder()
                .name(dto.getName())
                .cpfCnpj(dto.getCpfCnpj())
                .phone(dto.getPhone())
                .build();
    }

    public SupplierResponseDTO toResponse(Supplier entity) {
        return SupplierResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .cpfCnpj(entity.getCpfCnpj())
                .phone(entity.getPhone())
                .build();
    }
}