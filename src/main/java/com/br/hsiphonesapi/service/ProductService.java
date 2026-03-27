package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.ProductRequestDTO;
import com.br.hsiphonesapi.dto.response.ProductResponseDTO;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ProductService {
    @Transactional
    ProductResponseDTO save(ProductRequestDTO dto);

    List<ProductResponseDTO> findHistoryByImei(String imei);

    Page<ProductResponseDTO> findAll(Pageable pageable);

    Page<ProductResponseDTO> findAvailableProducts(Pageable pageable);

    Page<ProductResponseDTO> findByCategoryAndStatus(ProductCategory category, ProductStatus status, Pageable pageable);

    ProductResponseDTO findById(Long id);

    @Transactional
    ProductResponseDTO update(Long id, ProductRequestDTO dto);

    @Transactional
    void delete(Long id);
}
