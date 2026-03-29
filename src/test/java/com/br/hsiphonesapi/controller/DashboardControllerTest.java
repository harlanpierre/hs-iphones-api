package com.br.hsiphonesapi.controller;

import com.br.hsiphonesapi.BaseIntegrationTest;
import org.junit.jupiter.api.Test;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class DashboardControllerTest extends BaseIntegrationTest {

    @Test
    void getDashboard_authenticated_shouldReturn200() throws Exception {
        String tenantName = "Tenant-" + UUID.randomUUID().toString().substring(0, 8);
        String adminUser = "admin-" + UUID.randomUUID().toString().substring(0, 8);
        String token = registerAndGetToken(tenantName, adminUser);

        mockMvc.perform(get("/dashboard")
                        .header("Authorization", "Bearer " + token))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kpis").exists())
                .andExpect(jsonPath("$.salesByDay").isArray())
                .andExpect(jsonPath("$.salesByStatus").isArray())
                .andExpect(jsonPath("$.serviceOrdersByStatus").isArray())
                .andExpect(jsonPath("$.productsByCategory").isArray())
                .andExpect(jsonPath("$.recentSales").isArray())
                .andExpect(jsonPath("$.pendingServiceOrders").isArray());
    }

    @Test
    void getDashboard_unauthenticated_shouldReturn401() throws Exception {
        mockMvc.perform(get("/dashboard"))
                .andExpect(status().isForbidden());
    }
}
