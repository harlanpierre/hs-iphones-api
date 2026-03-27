package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.SupplierRequestDTO;
import com.br.hsiphonesapi.dto.response.SupplierResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SupplierService {
    SupplierResponseDTO save(SupplierRequestDTO dto);
    Page<SupplierResponseDTO> findAll(Pageable pageable);
    SupplierResponseDTO findById(Long id);
    SupplierResponseDTO update(Long id, SupplierRequestDTO dto);
    void delete(Long id);
    Page<SupplierResponseDTO> findByFilter(String filter, Pageable pageable);
}