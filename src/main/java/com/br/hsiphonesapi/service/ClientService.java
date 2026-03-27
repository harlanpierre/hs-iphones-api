package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.ClientRequestDTO;
import com.br.hsiphonesapi.dto.response.ClientResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ClientService {
    ClientResponseDTO save(ClientRequestDTO clientDTO);
    Page<ClientResponseDTO> findAll(Pageable pageable);
    Page<ClientResponseDTO> search(String search, Pageable pageable);
    ClientResponseDTO findById(Long id);
    ClientResponseDTO findByCpf(String cpf);
    ClientResponseDTO update(Long id, ClientRequestDTO clientDTO);
    void delete(Long id);
}