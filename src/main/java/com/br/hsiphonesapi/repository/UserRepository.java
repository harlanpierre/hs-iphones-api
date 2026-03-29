package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import org.springframework.data.jpa.repository.Modifying;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByUsername(String username);

    boolean existsByUsername(String username);

    Page<User> findByNameContainingIgnoreCaseOrUsernameContainingIgnoreCase(String name, String username, Pageable pageable);

    @Query(value = "SELECT * FROM users WHERE username = :username AND active = true", nativeQuery = true)
    Optional<User> findByUsernameIgnoringTenant(@Param("username") String username);

    @Query(value = "SELECT * FROM users WHERE email = :email AND active = true", nativeQuery = true)
    Optional<User> findByEmailIgnoringTenant(@Param("email") String email);

    @Modifying
    @Query(value = "INSERT INTO users (username, password, name, email, role, tenant_id, active, created_at) " +
            "VALUES (:username, :password, :name, :email, :role, :tenantId, true, NOW())", nativeQuery = true)
    void insertIgnoringTenantFilter(@Param("username") String username,
                                     @Param("password") String password,
                                     @Param("name") String name,
                                     @Param("email") String email,
                                     @Param("role") String role,
                                     @Param("tenantId") Long tenantId);

    @Modifying
    @Query(value = "UPDATE users SET password = :password WHERE id = :userId", nativeQuery = true)
    void updatePasswordIgnoringTenantFilter(@Param("userId") Long userId, @Param("password") String password);
}
