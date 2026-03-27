package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.ServiceOrder;
import com.br.hsiphonesapi.model.enums.ServiceOrderStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ServiceOrderRepository extends JpaRepository<ServiceOrder, Long> {

    Page<ServiceOrder> findByStatus(ServiceOrderStatus status, Pageable pageable);

    Page<ServiceOrder> findByClientId(Long clientId, Pageable pageable);

    @Query("SELECT os FROM ServiceOrder os WHERE " +
            "(:status IS NULL OR os.status = :status) AND " +
            "(:clientId IS NULL OR os.client.id = :clientId)")
    Page<ServiceOrder> findByFilters(
            @Param("status") ServiceOrderStatus status,
            @Param("clientId") Long clientId,
            Pageable pageable);
}