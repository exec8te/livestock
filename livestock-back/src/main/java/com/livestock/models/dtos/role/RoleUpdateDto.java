package com.livestock.models.dtos.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * This class represent a dto of role update.
 * The dto require : name
 */
@Data
@Builder
@AllArgsConstructor
public class RoleUpdateDto {
    @NotBlank
    @Size(min = 3, max = 100)
    private String name;
}
