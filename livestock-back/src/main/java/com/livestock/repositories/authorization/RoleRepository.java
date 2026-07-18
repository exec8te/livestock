package com.livestock.repositories.authorization;

import com.livestock.models.entities.authorization.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Page<Role> findAll(Pageable pageable);

    Optional<Role> findByName(String name);

    Boolean existsByName(String name);

    @Modifying
    @Query("""
                UPDATE Role r
                SET r.isDefault = false
                WHERE r.isDefault = true
            """)
    void clearDefaultRole();

    Optional<Role> findByIsDefaultTrue();
}
