package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.permission.PermissionCreateDto;
import com.livestock.models.dtos.authorization.permission.PermissionResponseDto;
import com.livestock.models.dtos.authorization.permission.PermissionUpdateDto;
import com.livestock.models.entities.authorization.Permission;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface PermissionMapper {

    PermissionResponseDto toResponseDto(Permission permission);


    @Mapping(target = "id", ignore = true)
    Permission toCreateEntity(PermissionCreateDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    void updateEntity(PermissionUpdateDto permissionDto, @MappingTarget Permission permission);
}

