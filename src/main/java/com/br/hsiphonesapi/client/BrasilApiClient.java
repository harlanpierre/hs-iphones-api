package com.br.hsiphonesapi.client;

import com.br.hsiphonesapi.client.dto.BrasilApiResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "brasilapi", url = "https://brasilapi.com.br/api/cep/v1")
public interface BrasilApiClient {
    @GetMapping("/{cep}")
    BrasilApiResponse buscarCep(@PathVariable("cep") String cep);
}