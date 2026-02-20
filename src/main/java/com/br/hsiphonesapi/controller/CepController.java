package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.dto.response.AddressResponseDTO;
import com.br.hsiphonesapi.service.CepService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/cep")
@Tag(name = "Consulta de CEP", description = "Busca de endereço otimizada com múltiplas APIs")
public class CepController {

    private final CepService cepService;

    public CepController(CepService cepService) {
        this.cepService = cepService;
    }

    @Operation(summary = "Buscar endereço por CEP",
            description = "Consulta ViaCEP, BrasilAPI e OpenCEP simultaneamente e retorna o mais rápido.")
    @GetMapping("/{cep}")
    public ResponseEntity<AddressResponseDTO> getAddressByCep(@PathVariable String cep) {
        return ResponseEntity.ok(cepService.searchCepFastest(cep));
    }
}