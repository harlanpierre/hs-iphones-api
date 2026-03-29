package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Plan;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PlanRepository extends JpaRepository<Plan, Long> {

    Optional<Plan> findBySlug(String slug);

    List<Plan> findByActiveTrueOrderByPriceMonthlyAsc();
}
