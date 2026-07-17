package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.login.LoginResponseDto;
import org.springframework.stereotype.Component;

@Component
public class LoginMapper {

    public LoginResponseDto toResponseDto(String token, Long expiresIn) {

        return LoginResponseDto.builder()
                .token(token)
                .expiresIn(expiresIn)
                .build();
    }
}
