package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.role.RoleCreateDto;
import com.livestock.models.dtos.authorization.role.RoleResponseDto;
import com.livestock.models.entities.authorization.Permission;
import com.livestock.models.entities.authorization.Role;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class RoleMapperTest {

    private final RoleMapper roleMapper = Mappers.getMapper(RoleMapper.class);

    @Test
    void toResponseDto_ShouldMapFieldsCorrectly() {
        Permission permission = Permission.builder().id(1L).resource("USER").build();
        Role role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .isDefault(true)
                .permissions(Set.of(permission))
                .build();

        RoleResponseDto dto = roleMapper.toResponseDto(role);

        assertNotNull(dto);
        assertEquals(role.getId(), dto.getId());
        assertEquals(role.getName(), dto.getName());
        assertEquals(role.getIsDefault(), dto.getIsDefault());
        assertFalse(dto.getPermissions().isEmpty());
    }

    @Test
    void toCreateEntity_ShouldIgnoreSpecificFields() {
        RoleCreateDto dto = RoleCreateDto.builder()
                .name("NEW_ROLE")
                .build();

        Role role = roleMapper.toCreateEntity(dto);

        assertNotNull(role);
        assertEquals(dto.getName(), role.getName());
        assertNull(role.getId());
        assertNull(role.getIsDefault());
    }
}
