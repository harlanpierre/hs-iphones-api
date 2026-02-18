package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.ClientRequestDTO;
import com.br.hsiphonesapi.dto.response.ClientResponseDTO;
import com.br.hsiphonesapi.mapper.ClientMapper;
import com.br.hsiphonesapi.model.Client;
import com.br.hsiphonesapi.repository.ClientRepository;
import com.br.hsiphonesapi.service.ClientService;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ClientServiceImpl implements ClientService {

    private final ClientRepository repository;
    private final ClientMapper mapper;

    public ClientServiceImpl(ClientRepository repository, ClientMapper mapper) {
        this.repository = repository;
        this.mapper = mapper;
    }

    @Override
    @Transactional
    public ClientResponseDTO save(ClientRequestDTO dto) {
        if (repository.existsByCpf(dto.getCpf())) {
            throw new IllegalArgumentException("CPF já cadastrado.");
        }
        // Converte DTO -> Entity
        Client client = mapper.toEntity(dto);
        // Salva
        Client savedClient = repository.save(client);
        // Retorna Entity -> ResponseDTO
        return mapper.toResponse(savedClient);
    }

    @Override
    public List<ClientResponseDTO> findAll() {
        return repository.findAll().stream()
                .map(mapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public ClientResponseDTO findById(Long id) {
        return repository.findById(id)
                .map(mapper::toResponse)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));
    }

    @Override
    @Transactional
    public ClientResponseDTO update(Long id, ClientRequestDTO dto) {
        Client existingClient = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Cliente não encontrado."));

        // Atualiza campos do Cliente
        existingClient.setName(dto.getName());
        existingClient.setPhone(dto.getPhone());

        // Atualiza campos do Endereço
        var newAddr = dto.getAddress();
        var oldAddr = existingClient.getAddress();

        oldAddr.setStreet(newAddr.getStreet());
        oldAddr.setNumber(newAddr.getNumber());
        oldAddr.setComplement(newAddr.getComplement());
        oldAddr.setDistrict(newAddr.getDistrict());
        oldAddr.setCity(newAddr.getCity());
        oldAddr.setState(newAddr.getState());
        oldAddr.setZipCode(newAddr.getZipCode());

        Client updatedClient = repository.save(existingClient);
        return mapper.toResponse(updatedClient);
    }

    @Override
    public void delete(Long id) {
        if (!repository.existsById(id)) throw new EntityNotFoundException("Cliente não encontrado.");
        repository.deleteById(id);
    }
}