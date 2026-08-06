package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.models.dtos.authorization.user.UserCreateDto;
import com.livestock.models.dtos.authorization.user.UserResponseDto;
import com.livestock.models.dtos.authorization.user.UserUpdateAdminDto;
import com.livestock.models.dtos.authorization.user.UserUpdateDto;
import com.livestock.models.entities.authorization.User;
import org.mapstruct.*;

@Mapper(componentModel = "spring")
public interface UserMapper {


    @Mapping(target = "role", source = "role.name")
    @Mapping(target = "lastLoginAt", ignore = true)
    UserResponseDto toResponseDto(User user);


    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "disabled", ignore = true)
    User toCreateEntity(RegisterRequestDto dto);

    @Mapping(target = "passwordHash", source = "password")
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "disabled", source = "disabled")
    User toCreateEntity(UserCreateDto dto);


    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", source = "email")
    void updateEntity(
            UserUpdateDto dto,
            @MappingTarget User user
    );

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "role", ignore = true)
    @Mapping(target = "passwordHash", ignore = true)
    @Mapping(target = "lastLoginAt", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "email", source = "email")
    @Mapping(target = "disabled", source = "disabled")
    void updateByAdminEntity(
            UserUpdateAdminDto dto,
            @MappingTarget User user
    );
}