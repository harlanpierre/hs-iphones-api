package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Sale;
import com.br.hsiphonesapi.model.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    Page<Sale> findByStatus(SaleStatus status, Pageable pageable);

    Page<Sale> findByClientId(Long clientId, Pageable pageable);

    Page<Sale> findByStatusAndClientId(SaleStatus status, Long clientId, Pageable pageable);

    @Query("SELECT s FROM Sale s WHERE " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:clientId IS NULL OR s.client.id = :clientId) AND " +
            "(:dateFrom IS NULL OR s.createdAt >= :dateFrom) AND " +
            "(:dateTo IS NULL OR s.createdAt <= :dateTo)")
    Page<Sale> findByFilters(
            @Param("status") SaleStatus status,
            @Param("clientId") Long clientId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable);
}