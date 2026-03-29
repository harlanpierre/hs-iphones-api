package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Sale;
import com.br.hsiphonesapi.model.enums.SaleStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    @EntityGraph(attributePaths = {"client", "items", "items.product", "payments", "payments.buybackProduct"})
    @Query("SELECT s FROM Sale s WHERE s.id = :id")
    Optional<Sale> findByIdWithDetails(@Param("id") Long id);

    @EntityGraph(attributePaths = {"client"})
    Page<Sale> findByStatus(SaleStatus status, Pageable pageable);

    @EntityGraph(attributePaths = {"client"})
    Page<Sale> findByClientId(Long clientId, Pageable pageable);

    @EntityGraph(attributePaths = {"client"})
    Page<Sale> findByStatusAndClientId(SaleStatus status, Long clientId, Pageable pageable);

    @Query(value = "SELECT s.* FROM sale s WHERE " +
            "s.tenant_id = :tenantId AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:clientId IS NULL OR s.client_id = :clientId) AND " +
            "(CAST(:dateFrom AS timestamp) IS NULL OR s.created_at >= CAST(:dateFrom AS timestamp)) AND " +
            "(CAST(:dateTo AS timestamp) IS NULL OR s.created_at <= CAST(:dateTo AS timestamp))",
            countQuery = "SELECT count(*) FROM sale s WHERE " +
            "s.tenant_id = :tenantId AND " +
            "(:status IS NULL OR s.status = :status) AND " +
            "(:clientId IS NULL OR s.client_id = :clientId) AND " +
            "(CAST(:dateFrom AS timestamp) IS NULL OR s.created_at >= CAST(:dateFrom AS timestamp)) AND " +
            "(CAST(:dateTo AS timestamp) IS NULL OR s.created_at <= CAST(:dateTo AS timestamp))",
            nativeQuery = true)
    Page<Sale> findByFilters(
            @Param("tenantId") Long tenantId,
            @Param("status") String status,
            @Param("clientId") Long clientId,
            @Param("dateFrom") LocalDateTime dateFrom,
            @Param("dateTo") LocalDateTime dateTo,
            Pageable pageable);
}