package com.livestock.services.authorization;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.authorization.PermissionAlreadyExistsException;
import com.livestock.exceptions.authorization.PermissionNotFoundException;
import com.livestock.mappers.authorization.PermissionMapper;
import com.livestock.models.dtos.authorization.permission.PermissionCreateDto;
import com.livestock.models.dtos.authorization.permission.PermissionResponseDto;
import com.livestock.models.dtos.authorization.permission.PermissionUpdateDto;
import com.livestock.models.entities.authorization.Permission;
import com.livestock.repositories.authorization.PermissionRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;


@Service
@RequiredArgsConstructor
@Slf4j
public class PermissionService {

    private final PermissionRepository permissionRepository;
    private final PermissionMapper permissionMapper;


    public PermissionResponseDto getPermissionById(Long permissionId) {

        Permission permission = findEntityById(permissionId);

        return permissionMapper.toResponseDto(permission);
    }


    public Page<PermissionResponseDto> getAll(Pageable pageable) {

        return permissionRepository.findAll(pageable)
                .map(permissionMapper::toResponseDto);
    }


    /**
     * Used by RoleService
     * Retrieves permissions required for role creation/update
     */
    public List<Permission> findAllByIds(
            Collection<Long> ids
    ) {

        return permissionRepository.findAllById(ids);
    }


    @Transactional
    public PermissionResponseDto createPermission(
            PermissionCreateDto dto
    ) {


        if (permissionRepository.existsByResourceAndAction(
                dto.getResource(),
                dto.getAction()
        )) {


            log.warn(
                    "event=PERMISSION_CREATED resource={} action={} reason=ALREADY_EXISTS status=FAILED",
                    dto.getResource(),
                    dto.getAction()
            );


            throw new PermissionAlreadyExistsException(
                    dto.getResource() + "_" + dto.getAction()
            );
        }


        Permission permission =
                permissionMapper.toCreateEntity(dto);


        Permission savedPermission =
                permissionRepository.save(permission);


        log.info(
                "event=PERMISSION_CREATED permissionId={} resource={} action={} status=SUCCESS",
                savedPermission.getId(),
                savedPermission.getResource(),
                savedPermission.getAction()
        );


        return permissionMapper.toResponseDto(savedPermission);
    }


    @Transactional
    public PermissionResponseDto updatePermission(
            Long permissionId,
            PermissionUpdateDto dto
    ) {


        Permission permission =
                findEntityById(permissionId);


        if (!hasChanges(dto, permission)) {

            throw new BadRequestException(
                    "No changes were made"
            );
        }


        if (permissionRepository.existsByResourceAndAction(
                dto.getResource(),
                dto.getAction()
        )
                &&
                (!Objects.equals(
                        permission.getResource(),
                        dto.getResource()
                )
                        ||
                        !Objects.equals(
                                permission.getAction(),
                                dto.getAction()
                        ))) {


            log.warn(
                    "event=PERMISSION_UPDATED permissionId={} reason=ALREADY_EXISTS status=FAILED",
                    permissionId
            );


            throw new PermissionAlreadyExistsException(
                    dto.getResource() + "_" + dto.getAction()
            );
        }


        permission.setResource(
                dto.getResource()
        );

        permission.setAction(
                dto.getAction()
        );


        log.info(
                "event=PERMISSION_UPDATED permissionId={} resource={} action={} status=SUCCESS",
                permission.getId(),
                permission.getResource(),
                permission.getAction()
        );


        return permissionMapper.toResponseDto(permission);
    }


    @Transactional
    public void deletePermission(
            Long permissionId
    ) {


        Permission permission =
                findEntityById(permissionId);


        permissionRepository.delete(permission);


        log.warn(
                "event=PERMISSION_DELETED permissionId={} resource={} action={} status=SUCCESS",
                permission.getId(),
                permission.getResource(),
                permission.getAction()
        );
    }


    protected Permission findEntityById(
            Long id
    ) {

        return permissionRepository.findById(id)
                .orElseThrow(
                        () -> new PermissionNotFoundException(id)
                );
    }


    private boolean hasChanges(
            PermissionUpdateDto dto,
            Permission permission
    ) {


        if (dto == null || permission == null) {
            return false;
        }


        return !Objects.equals(
                permission.getResource(),
                dto.getResource()
        )
                ||
                !Objects.equals(
                        permission.getAction(),
                        dto.getAction()
                );
    }

}