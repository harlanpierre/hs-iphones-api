package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.BaseIntegrationTest;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import java.util.Map;
import java.util.UUID;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class UserControllerTest extends BaseIntegrationTest {

    @Test
    void createUser_asAdmin_shouldReturn201() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetTokenWithEnterprisePlan(tenantName, adminUser);

        var userBody = Map.of(
                "username", "newuser-" + UUID.randomUUID().toString().substring(0, 8),
                "password", "senha123",
                "name", "New User",
                "role", "VENDEDOR"
        );

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").isNumber())
                .andExpect(jsonPath("$.username").value(userBody.get("username")))
                .andExpect(jsonPath("$.name").value("New User"))
                .andExpect(jsonPath("$.role").value("VENDEDOR"))
                .andExpect(jsonPath("$.active").value(true));
    }

    @Test
    void createUser_duplicateUsername_shouldReturn409() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetTokenWithEnterprisePlan(tenantName, adminUser);

        String duplicateUsername = "dupuser-" + UUID.randomUUID().toString().substring(0, 8);
        var userBody = Map.of(
                "username", duplicateUsername,
                "password", "senha123",
                "name", "User One",
                "role", "VENDEDOR"
        );

        // First creation should succeed
        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody)))
                .andExpect(status().isCreated());

        // Second creation with same username should return 409
        var userBody2 = Map.of(
                "username", duplicateUsername,
                "password", "senha123",
                "name", "User Two",
                "role", "VENDEDOR"
        );

        mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(userBody2)))
                .andExpect(status().isConflict());
    }

    @Test
    void listUsers_asAdmin_shouldReturnPage() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetTokenWithEnterprisePlan(tenantName, adminUser);

        // The admin user created during registration counts as 1 user
        mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray())
                .andExpect(jsonPath("$.content", hasSize(greaterThanOrEqualTo(1))))
                .andExpect(jsonPath("$.totalElements").isNumber());
    }

    @Test
    void updateUser_shouldUpdateName() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetTokenWithEnterprisePlan(tenantName, adminUser);

        // Create a user first
        String username = "edituser-" + UUID.randomUUID().toString().substring(0, 8);
        var createBody = Map.of(
                "username", username,
                "password", "senha123",
                "name", "Original Name",
                "role", "VENDEDOR"
        );

        var createResult = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andReturn();

        var createdUser = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long userId = createdUser.get("id").asLong();

        // Update the user's name
        var updateBody = Map.of(
                "username", username,
                "name", "Updated Name",
                "role", "VENDEDOR"
        );

        mockMvc.perform(put("/users/" + userId)
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateBody)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("Updated Name"));
    }

    @Test
    void deleteUser_shouldDeactivate() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetTokenWithEnterprisePlan(tenantName, adminUser);

        // Create a user to delete
        String username = "deluser-" + UUID.randomUUID().toString().substring(0, 8);
        var createBody = Map.of(
                "username", username,
                "password", "senha123",
                "name", "To Delete",
                "role", "VENDEDOR"
        );

        var createResult = mockMvc.perform(post("/users")
                        .header("Authorization", "Bearer " + token)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(createBody)))
                .andExpect(status().isCreated())
                .andReturn();

        var createdUser = objectMapper.readTree(createResult.getResponse().getContentAsString());
        Long userId = createdUser.get("id").asLong();

        // Delete (deactivate) the user
        mockMvc.perform(delete("/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isNoContent());

        // Verify the user is now inactive
        mockMvc.perform(get("/users/" + userId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.active").value(false));
    }

    @Test
    void deleteOwnUser_shouldReturn400() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetTokenWithEnterprisePlan(tenantName, adminUser);

        // Find the admin user's ID by listing users
        var listResult = mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andReturn();

        var usersList = objectMapper.readTree(listResult.getResponse().getContentAsString());
        Long adminId = null;
        for (var user : usersList.get("content")) {
            if (user.get("username").asText().equals(adminUser)) {
                adminId = user.get("id").asLong();
                break;
            }
        }

        // Try to delete own user - should return 422 (BusinessRuleException maps to UNPROCESSABLE_ENTITY)
        mockMvc.perform(delete("/users/" + adminId)
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void userFromTenantA_shouldNotSeeTenantBUsers() throws Exception {
        // Register Tenant A with admin
        String tenantA = "TenantA-" + UUID.randomUUID().toString().substring(0, 8);
        String adminA = "adminA-" + UUID.randomUUID().toString().substring(0, 8);
        String tokenA = registerAndGetToken(tenantA, adminA);

        // Register Tenant B with admin
        String tenantB = "TenantB-" + UUID.randomUUID().toString().substring(0, 8);
        String adminB = "adminB-" + UUID.randomUUID().toString().substring(0, 8);
        String tokenB = registerAndGetToken(tenantB, adminB);

        // List users from Tenant A - should only see Tenant A users
        var resultA = mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + tokenA))
                .andExpect(status().isOk())
                .andReturn();

        var usersA = objectMapper.readTree(resultA.getResponse().getContentAsString());
        for (var user : usersA.get("content")) {
            // None of Tenant A's visible users should be Tenant B's admin
            assert !user.get("username").asText().equals(adminB);
        }

        // List users from Tenant B - should only see Tenant B users
        var resultB = mockMvc.perform(get("/users")
                        .header("Authorization", "Bearer " + tokenB))
                .andExpect(status().isOk())
                .andReturn();

        var usersB = objectMapper.readTree(resultB.getResponse().getContentAsString());
        for (var user : usersB.get("content")) {
            // None of Tenant B's visible users should be Tenant A's admin
            assert !user.get("username").asText().equals(adminA);
        }
    }
}
