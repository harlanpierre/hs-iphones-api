package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.client.BrasilApiClient;
import com.br.hsiphonesapi.client.OpenCepClient;
import com.br.hsiphonesapi.client.ViaCepClient;
import com.br.hsiphonesapi.dto.response.AddressResponseDTO;
import com.br.hsiphonesapi.service.CepService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

@Slf4j
@Service
@RequiredArgsConstructor
public class CepServiceImpl implements CepService {

    private final ViaCepClient viaCepClient;
    private final BrasilApiClient brasilApiClient;
    private final OpenCepClient openCepClient;

    @Override
    public AddressResponseDTO searchCepFastest(String cep) {
        String cleanCep = cep.replaceAll("\\D", "");

        // Future para armazenar o vencedor
        CompletableFuture<AddressResponseDTO> resultFuture = new CompletableFuture<>();

        // Dispara as 3 requests em Threads separadas
        buscarViaCep(cleanCep, resultFuture);
        buscarBrasilApi(cleanCep, resultFuture);
        buscarOpenCep(cleanCep, resultFuture);

        try {
            // Espera até 4 segundos pelo primeiro resultado válido
            return resultFuture.get(4, TimeUnit.SECONDS);
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            log.error("Nenhuma API de CEP respondeu a tempo ou todas falharam", e);
            throw new RuntimeException("Não foi possível buscar o CEP no momento.");
        }
    }

    private void buscarViaCep(String cep, CompletableFuture<AddressResponseDTO> result) {
        CompletableFuture.runAsync(() -> {
            if (result.isDone()) return; // Se alguém já ganhou, nem tenta
            try {
                var response = viaCepClient.buscarCep(cep);
                if (response != null && !response.isErro()) {
                    log.info("ViaCEP venceu a corrida");
                    result.complete(AddressResponseDTO.builder()
                            .street(response.getLogradouro())
                            .district(response.getBairro())
                            .city(response.getLocalidade())
                            .state(response.getUf())
                            .zipCode(response.getCep())
                            .build());
                }
            } catch (Exception e) {
                log.warn("Falha no ViaCEP: {}", e.getMessage());
            }
        });
    }

    private void buscarBrasilApi(String cep, CompletableFuture<AddressResponseDTO> result) {
        CompletableFuture.runAsync(() -> {
            if (result.isDone()) return;
            try {
                var response = brasilApiClient.buscarCep(cep);
                if (response != null) {
                    log.info("BrasilAPI venceu a corrida");
                    result.complete(AddressResponseDTO.builder()
                            .street(response.getStreet())
                            .district(response.getNeighborhood())
                            .city(response.getCity())
                            .state(response.getState())
                            .zipCode(response.getCep())
                            .build());
                }
            } catch (Exception e) {
                log.warn("Falha no BrasilAPI: {}", e.getMessage());
            }
        });
    }

    private void buscarOpenCep(String cep, CompletableFuture<AddressResponseDTO> result) {
        CompletableFuture.runAsync(() -> {
            if (result.isDone()) return;
            try {
                var response = openCepClient.buscarCep(cep);
                if (response != null) {
                    log.info("OpenCEP venceu a corrida");
                    result.complete(AddressResponseDTO.builder()
                            .street(response.getLogradouro())
                            .district(response.getBairro())
                            .city(response.getLocalidade())
                            .state(response.getUf())
                            .zipCode(response.getCep())
                            .build());
                }
            } catch (Exception e) {
                log.warn("Falha no OpenCEP: {}", e.getMessage());
            }
        });
    }

}
