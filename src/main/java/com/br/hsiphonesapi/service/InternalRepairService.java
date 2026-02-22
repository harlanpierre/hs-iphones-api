package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.InternalRepairRequestDTO;
import com.br.hsiphonesapi.dto.response.InternalRepairResponseDTO;
import org.springframework.transaction.annotation.Transactional;

public interface InternalRepairService {
    @Transactional
    InternalRepairResponseDTO registerBuybackRepair(InternalRepairRequestDTO dto);

    @Transactional
    void finishBuybackRepair(Long phoneId);
}
