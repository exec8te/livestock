package com.livestock.models.dtos.authorization.user;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * This class represent a dto of user response.
 * A user response have : id, fullName, email, roleId, lastLoginAt, createdAt, updatedAt
 */
@Data
@Builder
@AllArgsConstructor
public class UserResponseDto {
    private String fullName;
    private String email;
    private String role;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
