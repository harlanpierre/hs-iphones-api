package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.LoginRequestDTO;
import com.br.hsiphonesapi.dto.request.RegisterRequestDTO;
import com.br.hsiphonesapi.dto.response.AuthResponseDTO;
import com.br.hsiphonesapi.exception.BusinessRuleException;
import com.br.hsiphonesapi.exception.DuplicateResourceException;
import com.br.hsiphonesapi.model.Plan;
import com.br.hsiphonesapi.model.Subscription;
import com.br.hsiphonesapi.model.Tenant;
import com.br.hsiphonesapi.model.User;
import com.br.hsiphonesapi.repository.PlanRepository;
import com.br.hsiphonesapi.repository.SubscriptionRepository;
import com.br.hsiphonesapi.repository.TenantRepository;
import com.br.hsiphonesapi.repository.UserRepository;
import com.br.hsiphonesapi.security.JwtUtil;
import com.br.hsiphonesapi.security.LoginAttemptService;
import com.br.hsiphonesapi.service.AuthService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final TenantRepository tenantRepository;
    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;
    private final AuthenticationManager authenticationManager;
    private final LoginAttemptService loginAttemptService;

    @Override
    public AuthResponseDTO login(LoginRequestDTO dto) {
        if (loginAttemptService.isBlocked(dto.getUsername())) {
            long remaining = loginAttemptService.getRemainingLockTimeSeconds(dto.getUsername());
            log.warn("Login bloqueado para usuário '{}' - conta temporariamente travada", dto.getUsername());
            throw new BusinessRuleException(
                    "Conta temporariamente bloqueada. Tente novamente em " + remaining / 60 + " minuto(s).");
        }

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(dto.getUsername(), dto.getPassword()));
        } catch (BadCredentialsException e) {
            loginAttemptService.loginFailed(dto.getUsername());
            log.warn("Tentativa de login falhou para usuário '{}'", dto.getUsername());
            throw e;
        }

        loginAttemptService.loginSucceeded(dto.getUsername());
        log.info("Login realizado com sucesso: '{}'", dto.getUsername());

        User user = userRepository.findByUsernameIgnoringTenant(dto.getUsername())
                .orElseThrow(() -> new IllegalArgumentException("Credenciais inválidas."));

        Tenant tenant = tenantRepository.findById(user.getTenantId())
                .orElseThrow(() -> new IllegalArgumentException("Tenant não encontrado."));

        String token = jwtUtil.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .build();
    }

    @Override
    @Transactional
    public AuthResponseDTO register(RegisterRequestDTO dto) {
        // 1. Gera slug a partir do nome da empresa
        String slug = dto.getTenantName().toLowerCase()
                .replaceAll("[^a-z0-9\\s-]", "")
                .replaceAll("\\s+", "-")
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");

        if (tenantRepository.existsBySlug(slug)) {
            throw new DuplicateResourceException("Já existe uma empresa cadastrada com este nome.");
        }

        // 2. Cria o Tenant
        Tenant tenant = Tenant.builder()
                .name(dto.getTenantName())
                .slug(slug)
                .build();
        tenantRepository.save(tenant);

        // 3. Cria assinatura com plano gratuito
        Plan freePlan = planRepository.findBySlug("free")
                .orElseThrow(() -> new IllegalStateException("Plano gratuito não encontrado."));

        Subscription subscription = Subscription.builder()
                .tenantId(tenant.getId())
                .plan(freePlan)
                .build();
        subscriptionRepository.save(subscription);

        // 4. Cria o usuário admin via query nativa (bypass @TenantId da Session)
        String encodedPassword = passwordEncoder.encode(dto.getPassword());
        userRepository.insertIgnoringTenantFilter(
                dto.getUsername(), encodedPassword, dto.getName(),
                dto.getEmail(), dto.getRole().name(), tenant.getId());

        // 5. Recupera o usuário criado para gerar o token
        User user = userRepository.findByUsernameIgnoringTenant(dto.getUsername())
                .orElseThrow(() -> new IllegalStateException("Erro ao criar usuário."));

        log.info("Novo tenant criado: '{}' (id={}), usuário admin: '{}'", tenant.getName(), tenant.getId(), user.getUsername());

        String token = jwtUtil.generateToken(user);

        return AuthResponseDTO.builder()
                .token(token)
                .username(user.getUsername())
                .name(user.getName())
                .role(user.getRole().name())
                .tenantId(tenant.getId())
                .tenantName(tenant.getName())
                .build();
    }
}
