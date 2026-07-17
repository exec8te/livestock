package com.livestock.controllers;

import com.livestock.models.dtos.permission.PermissionCreateDto;
import com.livestock.models.dtos.permission.PermissionResponseDto;
import com.livestock.models.dtos.permission.PermissionUpdateDto;
import com.livestock.services.PermissionService;
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
@RequestMapping("/permissions")
@RequiredArgsConstructor
public class PermissionController {
    private final PermissionService permissionService;

    @PreAuthorize("hasAuthority('PERMISSION_READ_ALL')")
    @GetMapping
    public Page<PermissionResponseDto> getAll(
            @PageableDefault(
                    size = 20,
                    sort = "resource",
                    direction = Sort.Direction.ASC
            ) Pageable pageable) {

        return permissionService.getAll(pageable);
    }

    @PreAuthorize("hasAuthority('PERMISSION_CREATE')")
    @PostMapping
    public ResponseEntity<PermissionResponseDto> create(
            @Valid @RequestBody PermissionCreateDto dto) {

        PermissionResponseDto permission = permissionService.createPermission(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(permission);
    }

    @PreAuthorize("hasAuthority('PERMISSION_READ_ALL')")
    @GetMapping("/{id}")
    public PermissionResponseDto getById(@PathVariable Long id) {
        return permissionService.getPermissionById(id);
    }

    @PreAuthorize("hasAuthority('PERMISSION_UPDATE_ALL')")
    @PutMapping("/{id}")
    public ResponseEntity<PermissionResponseDto> update(
            @PathVariable Long id,
            @Valid @RequestBody PermissionUpdateDto dto) {
        PermissionResponseDto permission = permissionService.updatePermission(id, dto);
        return ResponseEntity.status(HttpStatus.OK).body(permission);
    }

    @PreAuthorize("hasAuthority('PERMISSION_DELETE_ALL')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        permissionService.deletePermission(id);
        return ResponseEntity.noContent().build();
    }
}
