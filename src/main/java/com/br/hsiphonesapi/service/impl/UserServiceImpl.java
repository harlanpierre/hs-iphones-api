package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.dto.request.UserRequestDTO;
import com.br.hsiphonesapi.dto.response.UserResponseDTO;
import com.br.hsiphonesapi.exception.BusinessRuleException;
import com.br.hsiphonesapi.exception.DuplicateResourceException;
import com.br.hsiphonesapi.exception.ResourceNotFoundException;
import com.br.hsiphonesapi.model.User;
import com.br.hsiphonesapi.repository.UserRepository;
import com.br.hsiphonesapi.service.PlanUsageService;
import com.br.hsiphonesapi.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final PlanUsageService planUsageService;

    @Override
    public Page<UserResponseDTO> findAll(Pageable pageable, String search) {
        if (search != null && !search.isBlank()) {
            log.debug("Buscando usuários com filtro: {}", search);
            return userRepository.findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(search, search, pageable)
                    .map(this::toResponse);
        }
        log.debug("Listando todos os usuários");
        return userRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public UserResponseDTO findById(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));
        return toResponse(user);
    }

    @Override
    @Transactional
    public UserResponseDTO create(UserRequestDTO dto) {
        planUsageService.checkCanCreateUser();

        if (dto.getPassword() == null || dto.getPassword().isBlank()) {
            throw new BusinessRuleException("Senha é obrigatória para criação de usuário.");
        }

        if (userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("Username já cadastrado.");
        }

        User user = User.builder()
                .username(dto.getUsername())
                .password(passwordEncoder.encode(dto.getPassword()))
                .name(dto.getName())
                .email(dto.getEmail())
                .role(dto.getRole())
                .tenantId(TenantContext.getTenantId())
                .active(true)
                .build();

        User saved = userRepository.save(user);
        log.info("Usuário criado: {} (ID: {})", saved.getUsername(), saved.getId());
        return toResponse(saved);
    }

    @Override
    @Transactional
    public UserResponseDTO update(Long id, UserRequestDTO dto) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        if (userRepository.existsByUsername(dto.getUsername()) && !user.getUsername().equals(dto.getUsername())) {
            throw new DuplicateResourceException("Username já cadastrado.");
        }

        user.setUsername(dto.getUsername());
        user.setName(dto.getName());
        user.setEmail(dto.getEmail());
        user.setRole(dto.getRole());

        if (dto.getPassword() != null && !dto.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(dto.getPassword()));
        }

        User updated = userRepository.save(user);
        log.info("Usuário atualizado: {} (ID: {})", updated.getUsername(), updated.getId());
        return toResponse(updated);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado."));

        User currentUser = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentUser.getId().equals(id)) {
            throw new BusinessRuleException("Não é permitido desativar o próprio usuário.");
        }

        user.setActive(false);
        userRepository.save(user);
        log.info("Usuário desativado: {} (ID: {})", user.getUsername(), user.getId());
    }

    private UserResponseDTO toResponse(User user) {
        return UserResponseDTO.builder()
                .id(user.getId())
                .username(user.getUsername())
                .name(user.getName())
                .email(user.getEmail())
                .role(user.getRole().name())
                .active(user.getActive())
                .createdAt(user.getCreatedAt() != null ? user.getCreatedAt().toString() : null)
                .build();
    }
}
