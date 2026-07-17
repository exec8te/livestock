package com.livestock.models.dtos.authorization.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * This class represent a dto of permission update.
 * The dto require : resource, action
 */
@Data
@Builder
@AllArgsConstructor
public class PermissionUpdateDto {
    @NotBlank
    @Size(min = 3, max = 100, message = "Resource must contain between 3 and 100 characters")
    private String resource;

    @NotBlank
    @Size(min = 4, max = 6)
    private String action;
}
