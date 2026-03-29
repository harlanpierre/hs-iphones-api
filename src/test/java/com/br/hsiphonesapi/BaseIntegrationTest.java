package com.br.hsiphonesapi;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public abstract class BaseIntegrationTest {

    @Autowired
    protected MockMvc mockMvc;

    @Autowired
    protected JdbcTemplate jdbcTemplate;

    protected ObjectMapper objectMapper = new ObjectMapper();

    @AfterEach
    void clearTenantContext() {
        TenantContext.clear();
    }

    /**
     * Registra um tenant e retorna o token JWT.
     */
    protected String registerAndGetToken(String tenantName, String username) throws Exception {
        String email = username + "@test.com";
        return registerAndGetToken(tenantName, username, email);
    }

    /**
     * Registra um tenant com e-mail específico e retorna o token JWT.
     */
    protected String registerAndGetToken(String tenantName, String username, String email) throws Exception {
        var body = Map.of(
                "tenantName", tenantName,
                "username", username,
                "password", "senha123",
                "name", "Test User",
                "email", email,
                "role", "ADMIN"
        );
        var result = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andReturn();
        var json = objectMapper.readTree(result.getResponse().getContentAsString());
        return json.get("token").asText();
    }

    /**
     * Registra um tenant e faz upgrade para plano enterprise (ilimitado).
     * Retorna o token JWT.
     */
    protected String registerAndGetTokenWithEnterprisePlan(String tenantName, String username) throws Exception {
        String token = registerAndGetToken(tenantName, username);
        // Decodificar payload do JWT para extrair tenantId
        String payload = new String(java.util.Base64.getUrlDecoder().decode(token.split("\\.")[1]));
        var json = objectMapper.readTree(payload);
        Long tenantId = json.get("tenantId").asLong();
        // Upgrade subscription to enterprise plan (id=4)
        jdbcTemplate.update("UPDATE subscription SET plan_id = 4 WHERE tenant_id = ?", tenantId);
        return token;
    }

    /**
     * Gera um e-mail único para testes.
     */
    protected String uniqueEmail() {
        return "test-" + UUID.randomUUID().toString().substring(0, 8) + "@test.com";
    }
}
