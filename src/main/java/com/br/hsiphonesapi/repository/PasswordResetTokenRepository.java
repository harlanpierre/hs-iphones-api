package com.br.hsiphonesapi.repository;

import com.br.hsiphonesapi.model.PasswordResetToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {

    @Query(value = "SELECT * FROM password_reset_token WHERE token = :token AND used = false", nativeQuery = true)
    Optional<PasswordResetToken> findByTokenAndUsedFalse(@Param("token") String token);

    @Modifying
    @Query(value = "UPDATE password_reset_token SET used = true WHERE user_id = :userId AND used = false", nativeQuery = true)
    void invalidateAllTokensForUser(@Param("userId") Long userId);
}
