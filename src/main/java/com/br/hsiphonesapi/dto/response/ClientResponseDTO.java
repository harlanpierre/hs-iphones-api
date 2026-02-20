package com.br.hsiphonesapi.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {

    @Schema(description = "ID do cliente", example = "1")
    private Long id;

    @Schema(example = "João da Silva")
    private String name;

    @Schema(example = "123.456.789-00")
    private String cpf;

    @Schema(example = "joao.silva@email.com")
    private String email;

    @Schema(example = "(81) 99999-8888")
    private String phone;

    @Schema(description = "Dados do endereço")
    private AddressResponseDTO address;
}