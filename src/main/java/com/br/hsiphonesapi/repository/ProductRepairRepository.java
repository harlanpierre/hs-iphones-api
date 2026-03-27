package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.ProductRepair;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepairRepository extends JpaRepository<ProductRepair, Long> {

    List<ProductRepair> findByPhoneIdOrderByCreatedAtDesc(Long phoneId);
}