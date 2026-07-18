package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.permission.PermissionCreateDto;
import com.livestock.models.dtos.authorization.permission.PermissionResponseDto;
import com.livestock.models.dtos.authorization.permission.PermissionUpdateDto;
import com.livestock.models.entities.authorization.Permission;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class PermissionMapper {

    public PermissionResponseDto toResponseDto(Permission permission) {

        return PermissionResponseDto.builder()
                .id(permission.getId())
                .resource(permission.getResource())
                .action(permission.getAction())
                .build();
    }


    public Permission toCreateEntity(PermissionCreateDto dto) {

        return Permission.builder()
                .resource(dto.getResource())
                .action(dto.getAction())
                .build();
    }

    public void updateEntity(PermissionUpdateDto permissionDto, Permission permission) {

        if (permissionDto.getResource() != null
                && permissionDto.getAction() != null) {
            permission.setResource(permissionDto.getResource());
            permission.setAction(permissionDto.getAction());
        }
    }

    public boolean hasChanges(PermissionUpdateDto dto, Permission permission) {

        return !Objects.equals(permission.getResource(), dto.getResource())
                || !Objects.equals(permission.getAction(), dto.getAction());
    }
}

