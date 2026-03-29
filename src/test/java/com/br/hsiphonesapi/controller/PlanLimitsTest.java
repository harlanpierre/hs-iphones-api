package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class PlanLimitsTest extends BaseIntegrationTest {

    @Test
    void createUsersOverLimit_shouldReturn403() throws Exception {
        // Register a new tenant (gets free plan with maxUsers=1)
        // The registration itself creates the 1st user (the admin)
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetToken(tenantName, adminUser);

        // Free plan maxUsers=1, and the admin already counts as 1 user.
        // Trying to create another user should exceed the limit and return 403.
        var userBody = Map.of(
                "username", "extra-" + UUID.randomUUID().toString().substring(0, 8),
                "password", "senha123",
                "name", "Extra User",
                "role", "VENDEDOR"
        );

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody)))
                .andExpect(status().isForbidden());
    }
}
