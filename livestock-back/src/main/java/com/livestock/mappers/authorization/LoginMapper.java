package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.login.LoginResponseDto;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface LoginMapper {

    LoginResponseDto toResponseDto(String token, Long expiresIn);
}
