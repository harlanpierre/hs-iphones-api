package com.br.hsiphonesapi.dto.response;

import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ClientResponseDTO {
    private Long id;
    private String name;
    private String cpf;
    private String email;
    private String phone;
    private AddressResponseDTO address;
}
