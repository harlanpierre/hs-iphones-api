package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.SupplierRequestDTO;
import com.br.hsiphonesapi.dto.response.SupplierResponseDTO;

import java.util.List;

public interface SupplierService {
    SupplierResponseDTO save(SupplierRequestDTO dto);
    List<SupplierResponseDTO> findAll();
    SupplierResponseDTO findById(Long id);
    SupplierResponseDTO update(Long id, SupplierRequestDTO dto);
    void delete(Long id);
    List<SupplierResponseDTO> findByFilter(String filter);
}