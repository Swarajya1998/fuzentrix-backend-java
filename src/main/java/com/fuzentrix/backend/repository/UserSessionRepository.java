package com.fuzentrix.backend.repository;

import com.fuzentrix.backend.entity.UserSession;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserSessionRepository extends JpaRepository<UserSession, UUID> {

    Optional<UserSession> findByRefreshTokenHash(String refreshTokenHash);

    /**
     * Deletes expired or revoked sessions for a specific user.
     * Called on each login to prevent session rows from accumulating indefinitely.
     */
    @Modifying
    @Query("""
            DELETE FROM UserSession s
            WHERE s.user.id = :userId
            AND (s.expiresAt < :now OR s.isRevoked = true)
            """)
    void deleteExpiredOrRevokedSessionsForUser(@Param("userId") UUID userId, @Param("now") OffsetDateTime now);

    /**
     * Deletes all expired or revoked sessions across all users.
     * Used by the scheduled cleanup job (when enabled).
     */
    @Modifying
    @Query("""
            DELETE FROM UserSession s
            WHERE s.expiresAt < :now OR s.isRevoked = true
            """)
    void deleteAllExpiredOrRevokedSessions(@Param("now") OffsetDateTime now);
}
