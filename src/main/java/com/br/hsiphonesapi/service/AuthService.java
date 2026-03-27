package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.LoginRequestDTO;
import com.br.hsiphonesapi.dto.request.RegisterRequestDTO;
import com.br.hsiphonesapi.dto.response.AuthResponseDTO;

public interface AuthService {

    AuthResponseDTO login(LoginRequestDTO dto);

    AuthResponseDTO register(RegisterRequestDTO dto);
}
