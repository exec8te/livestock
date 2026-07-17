package com.livestock.mappers.authorization;

import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.models.dtos.authorization.user.UserResponseDto;
import com.livestock.models.dtos.authorization.user.UserUpdateAdminDto;
import com.livestock.models.dtos.authorization.user.UserUpdateDto;
import com.livestock.models.entities.authorization.User;
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


    public void updateEntity(
            UserUpdateDto dto,
            User user
    ) {

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }


    public void updateByAdminEntity(
            UserUpdateAdminDto dto,
            User user
    ) {

        if (dto.getFullName() != null) {
            user.setFullName(dto.getFullName());
        }

        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }
    }


    public boolean hasChanges(
            UserUpdateDto dto,
            User user
    ) {

        return !Objects.equals(user.getEmail(), dto.getEmail())
                || !Objects.equals(user.getFullName(), dto.getFullName());
    }


    public boolean hasChanges(
            UserUpdateAdminDto dto,
            User user
    ) {

        return !Objects.equals(user.getEmail(), dto.getEmail())
                || !Objects.equals(user.getFullName(), dto.getFullName())
                || !Objects.equals(user.getRole().getId(), dto.getRoleId());
    }
}