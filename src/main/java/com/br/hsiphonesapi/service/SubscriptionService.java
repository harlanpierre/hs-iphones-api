package com.br.hsiphonesapi.service;

import com.br.hsiphonesapi.dto.response.PlanResponseDTO;
import com.br.hsiphonesapi.dto.response.SubscriptionResponseDTO;

import java.util.List;

public interface SubscriptionService {

    List<PlanResponseDTO> listPlans();

    SubscriptionResponseDTO getCurrentSubscription();

    SubscriptionResponseDTO changePlan(Long planId);
}
