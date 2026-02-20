package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.response.AddressResponseDTO;

public interface CepService {
    AddressResponseDTO searchCepFastest(String cep);
}
