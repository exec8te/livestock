package com.livestock.repositories;

import com.livestock.models.entities.Role;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

    Page<Role> findAll(Pageable pageable);

    Boolean existsByName(String name);
}
