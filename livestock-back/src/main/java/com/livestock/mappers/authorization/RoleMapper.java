package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.role.RoleCreateDto;
import com.livestock.models.dtos.authorization.role.RoleResponseDto;
import com.livestock.models.dtos.authorization.role.RoleUpdateDto;
import com.livestock.models.entities.authorization.Permission;
import com.livestock.models.entities.authorization.Role;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;

@Component
public class RoleMapper {

    public RoleResponseDto toResponseDto(Role role) {

        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
                .isDefault(role.getIsDefault())
                .permissions(role.getPermissions())
                .createdAt(role.getCreatedAt())
                .updatedAt(role.getUpdatedAt())
                .build();
    }


    public Role toCreateEntity(RoleCreateDto dto) {

        return Role.builder()
                .name(dto.getName())
                .build();
    }

    public void updateEntity(RoleUpdateDto roleDto, Role role) {

        if (roleDto.getName() != null) {
            role.setName(roleDto.getName());
            role.setIsDefault(roleDto.getIsDefault());
        }
    }

    public boolean hasChanges(Role role, String name, Collection<Permission> permissions, Boolean isDefault) {

        return !Objects.equals(role.getName(), name)
                || !role.getPermissions().equals(new HashSet<>(permissions))
                || !role.getIsDefault().equals(isDefault);
    }
}

