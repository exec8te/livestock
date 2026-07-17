package com.livestock.services;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.PermissionAlreadyExistsException;
import com.livestock.exceptions.PermissionNotFoundException;
import com.livestock.mappers.PermissionMapper;
import com.livestock.models.dtos.permission.PermissionCreateDto;
import com.livestock.models.dtos.permission.PermissionResponseDto;
import com.livestock.models.dtos.permission.PermissionUpdateDto;
import com.livestock.models.entities.Permission;
import com.livestock.repositories.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PermissionService {
    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;

    public PermissionResponseDto getPermissionById(Long permissionId) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException(permissionId));

        return permissionMapper.toResponseDto(permission);
    }

    public Page<PermissionResponseDto> getAll(Pageable pageable) {

        return permissionRepository.findAll(pageable)
                .map(permissionMapper::toResponseDto);
    }

    public PermissionResponseDto createPermission(PermissionCreateDto dto) {

        if (permissionRepository.existsByResourceAndAction(dto.getResource(), dto.getAction())) {
            throw new PermissionAlreadyExistsException(
                    dto.getResource() + "_" + dto.getAction()
            );
        }

        Permission permission = permissionMapper.toCreateEntity(dto);

        Permission savedPermission = permissionRepository.save(permission);

        return permissionMapper.toResponseDto(savedPermission);
    }

    @Transactional
    public PermissionResponseDto updatePermission(Long permissionId, PermissionUpdateDto dto) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException(permissionId));

        if (permissionRepository.existsByResourceAndAction(dto.getResource(), dto.getAction())) {
            throw new PermissionAlreadyExistsException(
                    dto.getResource() + "_" + dto.getAction()
            );
        }

        if (!permissionMapper.hasChanges(dto, permission)) {
            throw new BadRequestException("No changes were made");
        }

        permission.setAction(dto.getAction());
        permission.setResource(dto.getResource());

        return permissionMapper.toResponseDto(permission);
    }

    @Transactional
    public void deletePermission(Long permissionId) {

        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new PermissionNotFoundException(permissionId));

        permissionRepository.delete(permission);
    }


}
