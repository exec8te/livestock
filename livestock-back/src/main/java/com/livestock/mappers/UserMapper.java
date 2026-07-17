package com.livestock.mappers;

import com.livestock.models.dtos.login.RegisterRequestDto;
import com.livestock.models.dtos.user.UserResponseDto;
import com.livestock.models.dtos.user.UserUpdateDto;
import com.livestock.models.entities.User;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class UserMapper {

    public UserResponseDto toResponseDto(User user) {
        return UserResponseDto.builder()
                .fullName(user.getFullName())
                .email(user.getEmail())
                .role(user.getRole().getName())
                .lastLoginAt(user.getLastLoginAt())
                .createdAt(user.getCreatedAt())
                .updatedAt(user.getUpdatedAt())
                .build();
    }


    public User toCreateEntity(RegisterRequestDto dto) {
        return User.builder()
                .fullName(dto.getFullName())
                .email(dto.getEmail())
                .passwordHash(dto.getPassword())
                .build();
    }

    public void updateEntity(UserUpdateDto userDto, User user) {
        if (userDto.getFullName() != null) {
            user.setFullName(userDto.getFullName());
        }

        if (userDto.getEmail() != null) {
            user.setEmail(userDto.getEmail());
        }
    }

    public boolean hasChanges(UserUpdateDto dto, User user) {

        return !Objects.equals(user.getEmail(), dto.getEmail())
                || !Objects.equals(user.getFullName(), dto.getFullName());
    }
}

