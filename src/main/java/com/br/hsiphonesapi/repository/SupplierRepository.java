package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    boolean existsByCpfCnpj(String cpfCnpj);
    boolean existsByPhone(String phone);

    // Busca Flexível: Nome (parcial), CPF (exato ou parcial) ou Telefone (exato ou parcial)
    @Query("SELECT s FROM Supplier s WHERE " +
            "LOWER(s.name) LIKE LOWER(CONCAT('%', :filter, '%')) OR " +
            "s.cpfCnpj LIKE CONCAT('%', :filter, '%') OR " +
            "s.phone LIKE CONCAT('%', :filter, '%')")
    List<Supplier> findByFilter(@Param("filter") String filter);
}