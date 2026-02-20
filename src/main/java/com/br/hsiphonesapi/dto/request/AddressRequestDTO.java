package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
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
public class AddressRequestDTO {

    @Schema(description = "Logradouro", example = "Av. Boa Viagem")
    @NotBlank(message = "Logradouro é obrigatório")
    private String street;

    @Schema(description = "Número", example = "1500")
    @NotBlank(message = "Número é obrigatório")
    private String number;

    @Schema(description = "Complemento", example = "Apto 101, Bloco B")
    private String complement;

    @Schema(description = "Bairro", example = "Boa Viagem")
    @NotBlank(message = "Bairro é obrigatório")
    private String district;

    @Schema(description = "Cidade", example = "Recife")
    @NotBlank(message = "Cidade é obrigatória")
    private String city;

    @Schema(description = "Estado (UF)", example = "PE")
    @NotBlank(message = "Estado é obrigatório")
    private String state;

    @Schema(description = "CEP (Com ou sem formatação)", example = "51000-000")
    @NotBlank(message = "CEP é obrigatório")
    private String zipCode;
}