package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);

    @Query("SELECT COUNT(p) > 0 FROM Product p JOIN p.imeis i " +
            "WHERE i IN :imeis AND p.status = 'DISPONIVEL'")
    boolean existsByAnyImeiAndStatusAvailable(@Param("imeis") Set<String> imeis);

    @Query("SELECT p FROM Product p JOIN p.imeis i WHERE i = :imei ORDER BY p.createdAt DESC")
    List<Product> findByImeiHistory(@Param("imei") String imei);

    Page<Product> findByStatus(ProductStatus status, Pageable pageable);

    Optional<Product> findByIdAndCategory(Long id, ProductCategory category);

    Page<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status, Pageable pageable);
}