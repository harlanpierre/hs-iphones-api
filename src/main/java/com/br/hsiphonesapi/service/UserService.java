package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.request.UserRequestDTO;
import com.br.hsiphonesapi.dto.response.UserResponseDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {
    Page<UserResponseDTO> findAll(Pageable pageable, String search);
    UserResponseDTO findById(Long id);
    UserResponseDTO create(UserRequestDTO dto);
    UserResponseDTO update(Long id, UserRequestDTO dto);
    void delete(Long id);
}
