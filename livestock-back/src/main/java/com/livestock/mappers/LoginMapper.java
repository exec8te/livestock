package com.livestock.mappers;

import com.livestock.models.dtos.login.LoginResponseDto;
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
