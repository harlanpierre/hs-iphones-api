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
public class AddressResponseDTO {

    @Schema(example = "Av. Boa Viagem")
    private String street;

    @Schema(example = "1500")
    private String number;

    @Schema(example = "Apto 101")
    private String complement;

    @Schema(example = "Boa Viagem")
    private String district;

    @Schema(example = "Recife")
    private String city;

    @Schema(example = "PE")
    private String state;

    @Schema(example = "51000-000")
    private String zipCode;
}