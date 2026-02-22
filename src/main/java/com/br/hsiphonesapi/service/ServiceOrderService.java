package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.ServiceOrderRequestDTO;
import com.br.hsiphonesapi.dto.response.ServiceOrderResponseDTO;
import com.br.hsiphonesapi.model.ServiceOrder;
import com.br.hsiphonesapi.model.enums.ServiceOrderStatus;
import org.springframework.transaction.annotation.Transactional;

public interface ServiceOrderService {
    @Transactional
    ServiceOrderResponseDTO createOS(ServiceOrderRequestDTO dto);

    @Transactional
    void addPartToOS(Long osId, Long partId, Integer quantity);

    @Transactional
    void changeOsStatus(Long osId, ServiceOrderStatus newStatus);
}
