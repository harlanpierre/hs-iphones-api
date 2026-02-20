package com.br.hsiphonesapi.client.dto;

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
public class BrasilApiResponse {
    private String cep;
    private String state;
    private String city;
    private String neighborhood;
    private String street;
}
