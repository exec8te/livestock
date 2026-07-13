package com.livestock.models.dtos.user;

import com.livestock.models.entities.Role;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * This class represent a user response.
 * A user response have : id, fullName, email, roleId, lastLoginAt, createdAt, updatedAt
 */
@Data
@Builder
public class UserResponseDto {
    private Long id;
    private String fullName;
    private String email;
    private Role role;
    private LocalDateTime lastLoginAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
