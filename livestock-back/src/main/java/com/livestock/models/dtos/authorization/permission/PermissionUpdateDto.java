package com.livestock.models.dtos.authorization.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
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
    @NotNull(message = "Resource is required")
    @NotBlank(message = "Resource must not be blank")
    @Size(min = 3, max = 100, message = "Resource must contain between 3 and 100 characters")
    @Pattern(
            regexp = "^[A-Z]+$",
            message = "Only uppercase letters are allowed"
    )
    private String resource;

    @NotNull(message = "Action is required")
    @NotBlank(message = "Action must not be blank")
    @Pattern(
            regexp = "^[A-Z]+$",
            message = "Only uppercase letters are allowed"
    )
    private PermissionAction action;
}
