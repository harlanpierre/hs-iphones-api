package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends BaseIntegrationTest {

    @Test
    void register_shouldCreateTenantAndUser() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        var body = Map.of(
                "tenantName", tenantName,
                "username", username,
                "password", "senha123",
                "name", "Admin User",
                "email", username + "@test.com",
                "role", "ADMIN"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tenantId").isNumber())
                .andExpect(jsonPath("$.tenantName").value(tenantName))
                .andExpect(jsonPath("$.username").value(username))
                .andExpect(jsonPath("$.role").value("ADMIN"));
    }

    @Test
    void register_duplicateTenantName_shouldFail() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        var body = Map.of(
                "tenantName", tenantName,
                "username", "user-" + UUID.randomUUID().toString().substring(0, 8),
                "password", "senha123",
                "name", "Admin User",
                "email", uniqueEmail(),
                "role", "ADMIN"
        );

        // First registration should succeed
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body)))
                .andExpect(status().isCreated());

        // Second registration with same tenant name should fail with 409
        var body2 = Map.of(
                "tenantName", tenantName,
                "username", "user2-" + UUID.randomUUID().toString().substring(0, 8),
                "password", "senha123",
                "name", "Another User",
                "email", uniqueEmail(),
                "role", "ADMIN"
        );

        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(body2)))
                .andExpect(status().isConflict());
    }

    @Test
    void login_withValidCredentials_shouldReturnToken() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "user-" + UUID.randomUUID().toString().substring(0, 8);

        // Register first
        var registerBody = Map.of(
                "tenantName", tenantName,
                "username", username,
                "password", "senha123",
                "name", "Test User",
                "email", username + "@test.com",
                "role", "ADMIN"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerBody)))
                .andExpect(status().isCreated());

        // Login with same credentials
        var loginBody = Map.of(
                "username", username,
                "password", "senha123"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.token").isNotEmpty())
                .andExpect(jsonPath("$.tenantId").isNumber())
                .andExpect(jsonPath("$.username").value(username));
    }

    @Test
    void login_withInvalidCredentials_shouldReturn401() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String username = "user-" + UUID.randomUUID().toString().substring(0, 8);

        // Register first
        var registerBody = Map.of(
                "tenantName", tenantName,
                "username", username,
                "password", "senha123",
                "name", "Test User",
                "email", username + "@test.com",
                "role", "ADMIN"
        );
        mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(registerBody)))
                .andExpect(status().isCreated());

        // Login with wrong password
        var loginBody = Map.of(
                "username", username,
                "password", "wrongpassword"
        );
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginBody)))
                .andExpect(status().isUnauthorized());
    }

    @Test
    void login_tenantIsolation_differentUsersInDifferentTenants() throws Exception {
        // Register user in Tenant A
        String tenantA = "TenantA-" + UUID.randomUUID().toString().substring(0, 8);
        String usernameA = "admin-a-" + UUID.randomUUID().toString().substring(0, 8);
        var bodyA = Map.of(
                "tenantName", tenantA,
                "username", usernameA,
                "password", "senha123",
                "name", "Admin A",
                "email", usernameA + "@test.com",
                "role", "ADMIN"
        );
        var resultA = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyA)))
                .andExpect(status().isCreated())
                .andReturn();
        var jsonA = objectMapper.readTree(resultA.getResponse().getContentAsString());
        Long tenantIdA = jsonA.get("tenantId").asLong();

        // Register user in Tenant B
        String tenantB = "TenantB-" + UUID.randomUUID().toString().substring(0, 8);
        String usernameB = "admin-b-" + UUID.randomUUID().toString().substring(0, 8);
        var bodyB = Map.of(
                "tenantName", tenantB,
                "username", usernameB,
                "password", "senha123",
                "name", "Admin B",
                "email", usernameB + "@test.com",
                "role", "ADMIN"
        );
        var resultB = mockMvc.perform(post("/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(bodyB)))
                .andExpect(status().isCreated())
                .andReturn();
        var jsonB = objectMapper.readTree(resultB.getResponse().getContentAsString());
        Long tenantIdB = jsonB.get("tenantId").asLong();

        // Both should login successfully with different tenantIds
        var loginA = Map.of("username", usernameA, "password", "senha123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginA)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantIdA));

        var loginB = Map.of("username", usernameB, "password", "senha123");
        mockMvc.perform(post("/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(loginB)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.tenantId").value(tenantIdB));

        // Ensure tenantIds are different
        assert !tenantIdA.equals(tenantIdB);
    }
}
