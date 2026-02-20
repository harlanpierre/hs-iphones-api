package com.br.hsiphonesapi.client;

import com.br.hsiphonesapi.client.dto.OpenCepResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "opencep", url = "https://opencep.com/v1")
public interface OpenCepClient {
    @GetMapping("/{cep}")
    OpenCepResponse buscarCep(@PathVariable("cep") String cep);
}