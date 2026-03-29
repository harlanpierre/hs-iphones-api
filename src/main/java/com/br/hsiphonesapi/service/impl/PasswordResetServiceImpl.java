package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.dto.request.ForgotPasswordRequestDTO;
import com.br.hsiphonesapi.dto.request.ResetPasswordRequestDTO;
import com.br.hsiphonesapi.dto.response.ForgotPasswordResponseDTO;
import com.br.hsiphonesapi.exception.BusinessRuleException;
import com.br.hsiphonesapi.model.PasswordResetToken;
import com.br.hsiphonesapi.model.User;
import com.br.hsiphonesapi.repository.PasswordResetTokenRepository;
import com.br.hsiphonesapi.repository.UserRepository;
import com.br.hsiphonesapi.service.EmailService;
import com.br.hsiphonesapi.service.PasswordResetService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PasswordResetServiceImpl implements PasswordResetService {

    private static final String GENERIC_MESSAGE = "Se o e-mail estiver cadastrado, enviaremos as instruções de recuperação.";

    private final UserRepository userRepository;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Override
    @Transactional
    public ForgotPasswordResponseDTO forgotPassword(ForgotPasswordRequestDTO dto) {
        Optional<User> userOpt = userRepository.findByEmailIgnoringTenant(dto.getEmail());

        if (userOpt.isEmpty()) {
            log.info("Solicitação de recuperação de senha para e-mail não cadastrado: '{}'", dto.getEmail());
            return ForgotPasswordResponseDTO.builder()
                    .message(GENERIC_MESSAGE)
                    .build();
        }

        User user = userOpt.get();

        // Invalida todos os tokens anteriores do usuário
        passwordResetTokenRepository.invalidateAllTokensForUser(user.getId());

        // Gera novo token com expiração de 1 hora
        String token = UUID.randomUUID().toString();
        PasswordResetToken resetToken = PasswordResetToken.builder()
                .token(token)
                .userId(user.getId())
                .tenantId(user.getTenantId())
                .expiresAt(LocalDateTime.now().plusHours(1))
                .used(false)
                .build();

        passwordResetTokenRepository.save(resetToken);

        log.info("Token de recuperação de senha gerado para usuário '{}' (id={})", user.getUsername(), user.getId());

        // Envia e-mail com o link de recuperação
        emailService.sendPasswordResetEmail(user.getEmail(), user.getName(), token);

        return ForgotPasswordResponseDTO.builder()
                .message(GENERIC_MESSAGE)
                .build();
    }

    @Override
    @Transactional
    public void resetPassword(ResetPasswordRequestDTO dto) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByTokenAndUsedFalse(dto.getToken())
                .orElseThrow(() -> new BusinessRuleException("Token inválido ou expirado."));

        if (resetToken.isExpired()) {
            throw new BusinessRuleException("Token inválido ou expirado.");
        }

        // Marca token como usado
        resetToken.setUsed(true);
        passwordResetTokenRepository.save(resetToken);

        // Atualiza a senha do usuário (query nativa para bypass do @TenantId)
        String encodedPassword = passwordEncoder.encode(dto.getNewPassword());
        userRepository.updatePasswordIgnoringTenantFilter(resetToken.getUserId(), encodedPassword);

        log.info("Senha redefinida com sucesso para o usuário (id={})", resetToken.getUserId());
    }
}
