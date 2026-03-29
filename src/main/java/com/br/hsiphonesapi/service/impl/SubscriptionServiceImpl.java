package com.br.hsiphonesapi.service.impl;

import com.br.hsiphonesapi.config.tenant.TenantContext;
import com.br.hsiphonesapi.dto.response.PlanResponseDTO;
import com.br.hsiphonesapi.dto.response.SubscriptionResponseDTO;
import com.br.hsiphonesapi.model.Plan;
import com.br.hsiphonesapi.model.Subscription;
import com.br.hsiphonesapi.repository.PlanRepository;
import com.br.hsiphonesapi.repository.SubscriptionRepository;
import com.br.hsiphonesapi.service.PlanUsageService;
import com.br.hsiphonesapi.service.SubscriptionService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class SubscriptionServiceImpl implements SubscriptionService {

    private final PlanRepository planRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final PlanUsageService planUsageService;

    @Override
    public List<PlanResponseDTO> listPlans() {
        return planRepository.findByActiveTrueOrderByPriceMonthlyAsc().stream()
                .map(this::toPlanDTO)
                .toList();
    }

    @Override
    public SubscriptionResponseDTO getCurrentSubscription() {
        Long tenantId = TenantContext.getTenantId();
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura não encontrada."));

        return toSubscriptionDTO(subscription);
    }

    @Override
    @Transactional
    public SubscriptionResponseDTO changePlan(Long planId) {
        Long tenantId = TenantContext.getTenantId();
        Subscription subscription = subscriptionRepository.findByTenantId(tenantId)
                .orElseThrow(() -> new EntityNotFoundException("Assinatura não encontrada."));

        Plan newPlan = planRepository.findById(planId)
                .orElseThrow(() -> new EntityNotFoundException("Plano não encontrado."));

        Plan oldPlan = subscription.getPlan();
        subscription.setPlan(newPlan);
        subscriptionRepository.save(subscription);

        log.info("Plano alterado para tenant {}: {} -> {}", tenantId, oldPlan.getSlug(), newPlan.getSlug());
        return toSubscriptionDTO(subscription);
    }

    private PlanResponseDTO toPlanDTO(Plan plan) {
        return PlanResponseDTO.builder()
                .id(plan.getId())
                .name(plan.getName())
                .slug(plan.getSlug())
                .description(plan.getDescription())
                .priceMonthly(plan.getPriceMonthly())
                .maxProducts(plan.getMaxProducts())
                .maxClients(plan.getMaxClients())
                .maxUsers(plan.getMaxUsers())
                .maxSalesPerMonth(plan.getMaxSalesPerMonth())
                .maxServiceOrdersPerMonth(plan.getMaxServiceOrdersPerMonth())
                .build();
    }

    private SubscriptionResponseDTO toSubscriptionDTO(Subscription subscription) {
        SubscriptionResponseDTO.UsageDTO usage = SubscriptionResponseDTO.UsageDTO.builder()
                .products(planUsageService.countProducts())
                .clients(planUsageService.countClients())
                .users(planUsageService.countUsers())
                .salesThisMonth(planUsageService.countSalesThisMonth())
                .serviceOrdersThisMonth(planUsageService.countServiceOrdersThisMonth())
                .build();

        return SubscriptionResponseDTO.builder()
                .id(subscription.getId())
                .status(subscription.getStatus().name())
                .startedAt(subscription.getStartedAt())
                .expiresAt(subscription.getExpiresAt())
                .plan(toPlanDTO(subscription.getPlan()))
                .usage(usage)
                .build();
    }
}
