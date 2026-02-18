package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.ClientRequestDTO;
import com.br.hsiphonesapi.dto.response.ClientResponseDTO;
import java.util.List;

public interface ClientService {
    ClientResponseDTO save(ClientRequestDTO clientDTO);
    List<ClientResponseDTO> findAll();
    ClientResponseDTO findById(Long id);
    ClientResponseDTO update(Long id, ClientRequestDTO clientDTO);
    void delete(Long id);
}