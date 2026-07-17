package com.livestock.repositories;

import com.livestock.models.entities.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Page<Permission> findAll(Pageable pageable);

    boolean existsByResourceAndAction(String resource, String action);
}
