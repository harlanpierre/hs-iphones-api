package com.br.hsiphonesapi.mapper;

import com.br.hsiphonesapi.dto.request.ClientRequestDTO;
import com.br.hsiphonesapi.dto.response.AddressResponseDTO;
import com.br.hsiphonesapi.dto.response.ClientResponseDTO;
import com.br.hsiphonesapi.model.Address;
import com.br.hsiphonesapi.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    // RequestDTO -> Entity (Usando Builder)
    public Client toEntity(ClientRequestDTO dto) {
        return Client.builder()
                .name(dto.getName())
                .cpf(dto.getCpf())
                .email(dto.getEmail())
                .phone(dto.getPhone())
                .address(Address.builder()
                        .street(dto.getAddress().getStreet())
                        .number(dto.getAddress().getNumber())
                        .complement(dto.getAddress().getComplement())
                        .district(dto.getAddress().getDistrict())
                        .city(dto.getAddress().getCity())
                        .state(dto.getAddress().getState())
                        .zipCode(dto.getAddress().getZipCode())
                        .build())
                .build();
    }

    // Entity -> ResponseDTO (Usando Builder)
    public ClientResponseDTO toResponse(Client entity) {
        return ClientResponseDTO.builder()
                .id(entity.getId())
                .name(entity.getName())
                .cpf(entity.getCpf())
                .email(entity.getEmail())
                .phone(entity.getPhone())
                .address(entity.getAddress() != null ? AddressResponseDTO.builder()
                        .street(entity.getAddress().getStreet())
                        .number(entity.getAddress().getNumber())
                        .complement(entity.getAddress().getComplement())
                        .district(entity.getAddress().getDistrict())
                        .city(entity.getAddress().getCity())
                        .state(entity.getAddress().getState())
                        .zipCode(entity.getAddress().getZipCode())
                        .build() : null)
                .build();
    }
}