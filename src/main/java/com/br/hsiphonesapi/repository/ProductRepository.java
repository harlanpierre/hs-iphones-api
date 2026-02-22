package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Product;
import com.br.hsiphonesapi.model.enums.ProductCategory;
import com.br.hsiphonesapi.model.enums.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    boolean existsBySku(String sku);

    // Query Customizada: Verifica se ALGUM imei da lista já existe em um produto DISPONÍVEL
    @Query("SELECT COUNT(p) > 0 FROM Product p JOIN p.imeis i " +
            "WHERE i IN :imeis AND p.status = 'DISPONIVEL'")
    boolean existsByAnyImeiAndStatusAvailable(@Param("imeis") Set<String> imeis);

    // Para o histórico: Busca produtos que contenham aquele IMEI específico
    @Query("SELECT p FROM Product p JOIN p.imeis i WHERE i = :imei ORDER BY p.createdAt DESC")
    List<Product> findByImeiHistory(@Param("imei") String imei);

    List<Product> findByStatus(ProductStatus status);

    // NOVO MÉTODO: Busca o produto pelo ID garantindo que ele pertença a uma categoria específica
    Optional<Product> findByIdAndCategory(Long id, ProductCategory category);

    // NOVO MÉTODO: Busca produtos filtrando por Categoria e Status simultaneamente
    List<Product> findByCategoryAndStatus(ProductCategory category, ProductStatus status);
}