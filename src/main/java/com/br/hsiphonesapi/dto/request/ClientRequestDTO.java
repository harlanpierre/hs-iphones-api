package com.br.hsiphonesapi.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.validator.constraints.br.CPF;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientRequestDTO {

    @Schema(description = "Nome completo do cliente", example = "João da Silva")
    @NotBlank(message = "Nome é obrigatório")
    private String name;

    @Schema(description = "CPF válido", example = "123.456.789-00")
    @NotBlank(message = "CPF é obrigatório")
    @CPF(message = "CPF inválido")
    private String cpf;

    @Schema(description = "E-mail para contato", example = "joao.silva@email.com")
    @NotBlank(message = "E-mail é obrigatório")
    @Email(message = "E-mail inválido")
    private String email;

    @Schema(description = "Telefone celular com DDD", example = "(81) 99999-8888")
    private String phone;

    @Schema(description = "Endereço completo do cliente")
    @NotNull(message = "Endereço é obrigatório")
    @Valid
    private AddressRequestDTO address;
}