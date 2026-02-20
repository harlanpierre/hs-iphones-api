package com.br.hsiphonesapi.dto.request;

import com.br.hsiphonesapi.validation.CpfOrCnpj;
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
public class SupplierRequestDTO {

    @Schema(description = "Nome do fornecedor ou Razão Social", example = "Distribuidora Apple Nordeste")
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Schema(description = "CPF ou CNPJ válido (sem formatação ou com)", example = "12.345.678/0001-90")
    @CpfOrCnpj(message = "Informe um CPF ou CNPJ válido")
    private String cpfCnpj;

    @Schema(description = "Telefone de contato", example = "(81) 3322-4455")
    @NotBlank(message = "Telefone é obrigatório")
    private String phone;
}