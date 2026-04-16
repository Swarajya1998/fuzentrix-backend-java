package com.fuzentrix.backend.repository;

import com.fuzentrix.backend.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    /**
     * Fetches a user with all authority-related associations eagerly loaded in a single query,
     * eliminating the N+1 problem that arises when loading roles → permissions → userPermissions
     * in separate lazy fetches.
     *
     * <p>Use this method in {@link com.fuzentrix.backend.security.CustomUserDetailsService}
     * to correctly build the {@link org.springframework.security.core.userdetails.UserDetails} object.
     */
    @Query("""
            SELECT DISTINCT u FROM User u
            LEFT JOIN FETCH u.roles r
            LEFT JOIN FETCH r.permissions
            LEFT JOIN FETCH u.userPermissions up
            LEFT JOIN FETCH up.permission
            WHERE u.email = :email
            """)
    Optional<User> findByEmailWithAuthorities(@Param("email") String email);
}
