package com.livestock.repositories.authorization;

import com.livestock.models.entities.authorization.User;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    boolean existsByEmail(String email);

    Optional<User> findByEmail(@NotBlank @Email String email);

    @Modifying
    @Query("""
                UPDATE User u
                SET u.role.id = :newRoleId
                WHERE u.role.id = :oldRoleId
            """)
    void updateRoleForUsers(
            Long oldRoleId,
            Long newRoleId
    );

    @Modifying
    @Query("""
                UPDATE User u 
                SET u.disabled = true 
                WHERE u.id = :id
            """)
    void softDelete(Long id);

    @Modifying
    @Query("""
                UPDATE User u 
                            SET u.lastLoginAt = NOW() 
                            WHERE u.email = :email
            """)
    void updateDateLastLogin(@NotBlank @Email String email);
}
