package com.livestock.models.dtos.authorization.role;

import com.livestock.models.entities.authorization.Permission;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

/**
 * This class represent a dto of role response.
 * The dto have : id, name, createdAt, updatedAt
 */
@Data
@Builder
@AllArgsConstructor
public class RoleResponseDto {

    private Long id;

    @NotBlank
    @Size(min = 3, max = 100)
    private String name;

    private Set<Permission> permissions;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
