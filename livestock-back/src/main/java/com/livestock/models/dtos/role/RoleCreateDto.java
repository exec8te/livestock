package com.livestock.models.dtos.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represent a dto of role create.
 * The dto require : name
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RoleCreateDto {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
}

