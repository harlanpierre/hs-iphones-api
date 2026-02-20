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
public class SupplierResponseDTO {

    @Schema(description = "ID do fornecedor", example = "10")
    private Long id;

    @Schema(example = "Distribuidora Apple Nordeste")
    private String name;

    @Schema(example = "12.345.678/0001-90")
    private String cpfCnpj;

    @Schema(example = "(81) 3322-4455")
    private String phone;
}