package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.ServiceOrderRequestDTO;
import com.br.hsiphonesapi.dto.response.ServiceOrderResponseDTO;
import com.br.hsiphonesapi.model.enums.ServiceOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceOrderService {

    Page<ServiceOrderResponseDTO> findAll(Pageable pageable);

    Page<ServiceOrderResponseDTO> findByFilters(ServiceOrderStatus status, Long clientId, Pageable pageable);

    ServiceOrderResponseDTO findById(Long id);

    @Transactional
    ServiceOrderResponseDTO createOS(ServiceOrderRequestDTO dto);

    @Transactional
    void addPartToOS(Long osId, Long partId, Integer quantity);

    @Transactional
    void changeOsStatus(Long osId, ServiceOrderStatus newStatus);
}
