package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.ProductStatusHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductStatusHistoryRepository extends JpaRepository<ProductStatusHistory, Long> {
}