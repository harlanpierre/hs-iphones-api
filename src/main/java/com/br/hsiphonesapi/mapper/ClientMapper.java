package com.br.hsiphonesapi.mapper;

import com.br.hsiphonesapi.dto.request.AddressRequestDTO;
import com.br.hsiphonesapi.dto.request.ClientRequestDTO;
import com.br.hsiphonesapi.dto.response.AddressResponseDTO;
import com.br.hsiphonesapi.dto.response.ClientResponseDTO;
import com.br.hsiphonesapi.model.Address;
import com.br.hsiphonesapi.model.Client;
import org.springframework.stereotype.Component;

@Component
public class ClientMapper {

    // RequestDTO -> Entity
    public Client toEntity(ClientRequestDTO dto) {
        Client client = new Client();
        client.setName(dto.getName());
        client.setCpf(dto.getCpf());
        client.setEmail(dto.getEmail());
        client.setPhone(dto.getPhone());

        Address address = new Address();
        AddressRequestDTO addrDto = dto.getAddress();
        address.setStreet(addrDto.getStreet());
        address.setNumber(addrDto.getNumber());
        address.setComplement(addrDto.getComplement());
        address.setDistrict(addrDto.getDistrict());
        address.setCity(addrDto.getCity());
        address.setState(addrDto.getState());
        address.setZipCode(addrDto.getZipCode());

        client.setAddress(address);
        return client;
    }

    // Entity -> ResponseDTO
    public ClientResponseDTO toResponse(Client entity) {
        ClientResponseDTO dto = new ClientResponseDTO();
        dto.setId(entity.getId());
        dto.setName(entity.getName());
        dto.setCpf(entity.getCpf());
        dto.setEmail(entity.getEmail());
        dto.setPhone(entity.getPhone());

        if (entity.getAddress() != null) {
            AddressResponseDTO addrDto = new AddressResponseDTO();
            Address addr = entity.getAddress();
            addrDto.setStreet(addr.getStreet());
            addrDto.setNumber(addr.getNumber());
            addrDto.setComplement(addr.getComplement());
            addrDto.setDistrict(addr.getDistrict());
            addrDto.setCity(addr.getCity());
            addrDto.setState(addr.getState());
            addrDto.setZipCode(addr.getZipCode());

            dto.setAddress(addrDto);
        }
        return dto;
    }
}