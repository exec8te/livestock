package com.livestock.mappers;

import com.livestock.models.dtos.role.RoleCreateDto;
import com.livestock.models.dtos.role.RoleResponseDto;
import com.livestock.models.dtos.role.RoleUpdateDto;
import com.livestock.models.entities.Role;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class RoleMapper {

    public RoleResponseDto toResponseDto(Role role) {

        return RoleResponseDto.builder()
                .id(role.getId())
                .name(role.getName())
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
        }
    }

    public boolean hasChanges(RoleUpdateDto dto, Role role) {

        return !Objects.equals(role.getName(), dto.getName());
    }
}

