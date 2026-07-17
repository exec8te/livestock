package com.livestock.controllers;

import com.livestock.models.dtos.role.RoleCreateDto;
import com.livestock.models.dtos.role.RoleResponseDto;
import com.livestock.models.dtos.role.RoleUpdateDto;
import com.livestock.services.RoleService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/roles")
@RequiredArgsConstructor
public class RoleController {

    private final RoleService roleService;

    @PreAuthorize("hasAuthority('ROLE_READ_ALL')")
    @GetMapping
    public Page<RoleResponseDto> getAll(
            @PageableDefault(
                    size = 20,
                    sort = "createdAt",
                    direction = Sort.Direction.DESC
            ) Pageable pageable) {

        return roleService.getAll(pageable);
    }

    @PreAuthorize("hasAuthority('ROLE_CREATE')")
    @PostMapping
    public ResponseEntity<RoleResponseDto> create(
            @Valid @RequestBody RoleCreateDto dto) {

        RoleResponseDto role = roleService.createRole(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(role);
    }

    @PreAuthorize("hasAuthority('PERMISSION_READ_ALL')")
    @GetMapping("/{id}")
    public RoleResponseDto getById(@PathVariable Long id) {
        return roleService.getRoleById(id);
    }

    @PreAuthorize("hasAuthority('ROLE_UPDATE_ALL')")
    @PutMapping("/{id}")
    public ResponseEntity<RoleResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody RoleUpdateDto dto) {
        RoleResponseDto role = roleService.updateRole(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(role);
    }

    @PreAuthorize("hasAuthority('ROLE_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteRole(id);
        return ResponseEntity.noContent().build();
    }
}