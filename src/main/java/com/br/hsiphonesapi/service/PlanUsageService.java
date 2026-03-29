package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.exception.PlanLimitExceededException;
import com.br.hsiphonesapi.model.Plan;
import com.br.hsiphonesapi.model.Subscription;
import com.br.hsiphonesapi.model.enums.SubscriptionStatus;
import com.br.hsiphonesapi.repository.SubscriptionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

@Slf4j
@Service
@RequiredArgsConstructor
public class PlanUsageService {

    private final SubscriptionRepository subscriptionRepository;
    private final JdbcTemplate jdbcTemplate;

    public void checkCanCreateProduct() {
        Plan plan = getActivePlan();
        long count = countByTenant("product", "deleted = false");
        if (count >= plan.getMaxProducts()) {
            throw new PlanLimitExceededException(
                    "Limite de produtos atingido (" + plan.getMaxProducts() + "). Faça upgrade do seu plano.");
        }
    }

    public void checkCanCreateClient() {
        Plan plan = getActivePlan();
        long count = countByTenant("client", "deleted = false");
        if (count >= plan.getMaxClients()) {
            throw new PlanLimitExceededException(
                    "Limite de clientes atingido (" + plan.getMaxClients() + "). Faça upgrade do seu plano.");
        }
    }

    public void checkCanCreateUser() {
        Plan plan = getActivePlan();
        long count = countByTenant("users", "active = true");
        if (count >= plan.getMaxUsers()) {
            throw new PlanLimitExceededException(
                    "Limite de usuários atingido (" + plan.getMaxUsers() + "). Faça upgrade do seu plano.");
        }
    }

    public void checkCanCreateSale() {
        Plan plan = getActivePlan();
        long count = countMonthlyByTenant("sale");
        if (count >= plan.getMaxSalesPerMonth()) {
            throw new PlanLimitExceededException(
                    "Limite de vendas mensais atingido (" + plan.getMaxSalesPerMonth() + "). Faça upgrade do seu plano.");
        }
    }

    public void checkCanCreateServiceOrder() {
        Plan plan = getActivePlan();
        long count = countMonthlyByTenant("service_order");
        if (count >= plan.getMaxServiceOrdersPerMonth()) {
            throw new PlanLimitExceededException(
                    "Limite de ordens de serviço mensais atingido (" + plan.getMaxServiceOrdersPerMonth() + "). Faça upgrade do seu plano.");
        }
    }

    private Plan getActivePlan() {
        Long tenantId = TenantContext.getTenantId();
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new PlanLimitExceededException("Nenhuma assinatura ativa encontrada."));

        if (subscription.getStatus() != SubscriptionStatus.ACTIVE && subscription.getStatus() != SubscriptionStatus.TRIAL) {
            throw new PlanLimitExceededException("Sua assinatura está " + subscription.getStatus().name().toLowerCase() + ". Regularize para continuar.");
        }

        return subscription.getPlan();
    }

    public long countProducts() {
        return countByTenant("product", "deleted = false");
    }

    public long countClients() {
        return countByTenant("client", "deleted = false");
    }

    public long countUsers() {
        return countByTenant("users", "active = true");
    }

    public long countSalesThisMonth() {
        return countMonthlyByTenant("sale");
    }

    public long countServiceOrdersThisMonth() {
        return countMonthlyByTenant("service_order");
    }

    private long countByTenant(String table, String extraCondition) {
        Long tenantId = TenantContext.getTenantId();
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE tenant_id = ? AND " + extraCondition;
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tenantId);
        return count != null ? count : 0;
    }

    private long countMonthlyByTenant(String table) {
        Long tenantId = TenantContext.getTenantId();
        LocalDateTime firstDayOfMonth = LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        String sql = "SELECT COUNT(*) FROM " + table + " WHERE tenant_id = ? AND created_at >= ?";
        Long count = jdbcTemplate.queryForObject(sql, Long.class, tenantId, firstDayOfMonth);
        return count != null ? count : 0;
    }
}
