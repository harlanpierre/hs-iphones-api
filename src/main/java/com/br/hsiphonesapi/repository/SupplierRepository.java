package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Supplier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByPhone(String phone);

    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
            "s.cpfCnpj LIKE CONCAT('%', :filter, '%') OR " +
            "s.phone LIKE CONCAT('%', :filter, '%')")
    Page<Supplier> findByFilter(@Param("filter") String filter, Pageable pageable);
}