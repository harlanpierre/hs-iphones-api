package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PasswordResetControllerTest extends BaseIntegrationTest {

    @Test
    void forgotPassword_withValidEmail_shouldReturn200() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "user-" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@test.com";
        registerAndGetToken(tenantName, username, email);

        var body = Map.of("email", email);

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());

        // Verifica que o token foi criado no banco
        String token = jdbcTemplate.queryForObject(
                "SELECT token FROM password_reset_token WHERE user_id = (SELECT id FROM users WHERE email = ?) AND used = false ORDER BY created_at DESC LIMIT 1",
                String.class, email);
        assert token != null && !token.isEmpty();
    }

    @Test
    void forgotPassword_withUnknownEmail_shouldReturn200() throws Exception {
        var body = Map.of("email", "inexistente-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com");

        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").isNotEmpty());
    }

    @Test
    void resetPassword_withValidToken_shouldChangePassword() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "user-" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@test.com";
        registerAndGetToken(tenantName, username, email);

        // Solicita recuperação
        var forgotBody = Map.of("email", email);
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotBody)))
                .andExpect(status().isOk());

        // Busca token no banco
        String resetToken = jdbcTemplate.queryForObject(
                "SELECT token FROM password_reset_token WHERE user_id = (SELECT id FROM users WHERE email = ?) AND used = false ORDER BY created_at DESC LIMIT 1",
                String.class, email);

        // Redefine a senha
        var resetBody = Map.of(
                "token", resetToken,
                "newPassword", "novaSenha123"
        );
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetBody)))
                .andExpect(status().isNoContent());

        // Verifica que consegue logar com a nova senha
        var loginBody = Map.of(
                "username", username,
                "password", "novaSenha123"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty());

        // Verifica que a senha antiga não funciona mais
        var oldLoginBody = Map.of(
                "username", username,
                "password", "senha123"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(oldLoginBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void resetPassword_withInvalidToken_shouldReturn422() throws Exception {
        var resetBody = Map.of(
                "token", "token-invalido-" + UUID.randomUUID(),
                "newPassword", "novaSenha123"
        );

        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetBody)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void resetPassword_withAlreadyUsedToken_shouldReturn422() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "user-" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@test.com";
        registerAndGetToken(tenantName, username, email);

        // Solicita recuperação
        var forgotBody = Map.of("email", email);
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotBody)))
                .andExpect(status().isOk());

        // Busca token no banco
        String resetToken = jdbcTemplate.queryForObject(
                "SELECT token FROM password_reset_token WHERE user_id = (SELECT id FROM users WHERE email = ?) AND used = false ORDER BY created_at DESC LIMIT 1",
                String.class, email);

        // Usa o token pela primeira vez
        var resetBody = Map.of(
                "token", resetToken,
                "newPassword", "novaSenha123"
        );
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetBody)))
                .andExpect(status().isNoContent());

        // Tenta usar o mesmo token novamente
        var resetBody2 = Map.of(
                "token", resetToken,
                "newPassword", "outraSenha456"
        );
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetBody2)))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void resetPassword_withExpiredToken_shouldReturn422() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "user-" + UUID.randomUUID().toString().substring(0, 8);
        String email = username + "@test.com";
        registerAndGetToken(tenantName, username, email);

        // Solicita recuperação
        var forgotBody = Map.of("email", email);
        mockMvc.perform(post("/auth/forgot-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(forgotBody)))
                .andExpect(status().isOk());

        // Busca token no banco
        String resetToken = jdbcTemplate.queryForObject(
                "SELECT token FROM password_reset_token WHERE user_id = (SELECT id FROM users WHERE email = ?) AND used = false ORDER BY created_at DESC LIMIT 1",
                String.class, email);

        // Força expiração do token via SQL
        jdbcTemplate.update(
                "UPDATE password_reset_token SET expires_at = NOW() - INTERVAL '2 hours' WHERE token = ?",
                resetToken
        );

        // Tenta usar o token expirado
        var resetBody = Map.of(
                "token", resetToken,
                "newPassword", "novaSenha123"
        );
        mockMvc.perform(post("/auth/reset-password")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(resetBody)))
                .andExpect(status().isUnprocessableEntity());
    }
}
