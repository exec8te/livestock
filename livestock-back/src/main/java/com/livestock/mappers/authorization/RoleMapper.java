package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.role.RoleCreateDto;
import com.livestock.models.dtos.authorization.role.RoleResponseDto;
import com.livestock.models.dtos.authorization.role.RoleUpdateDto;
import com.livestock.models.entities.authorization.Role;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface RoleMapper {


    @Mapping(target = "permissions", source = "permissions")
    RoleResponseDto toResponseDto(Role role);


    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "isDefault", ignore = true)
    Role toCreateEntity(RoleCreateDto dto);


    @BeanMapping(
            nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            RoleUpdateDto dto,
            @MappingTarget Role role
    );

    Role toEntity(RoleResponseDto dto);

}