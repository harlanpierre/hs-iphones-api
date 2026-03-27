package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    boolean existsByCpf(String cpf);
    boolean existsByEmail(String email);
    Optional<Client> findByCpf(String cpf);

    @Query("SELECT c FROM Client c WHERE " +
            "LOWER(c.name) LIKE LOWER(CONCAT('%', :search, '%')) OR " +
            "c.cpf LIKE CONCAT('%', :search, '%') OR " +
            "LOWER(c.email) LIKE LOWER(CONCAT('%', :search, '%'))")
    Page<Client> findBySearch(@Param("search") String search, Pageable pageable);
}