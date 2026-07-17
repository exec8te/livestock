package com.livestock.models.dtos.authorization.permission;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represent a dto of permission create.
 * The dto require : resource, action
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PermissionCreateDto {

    @NotNull(message = "Resource is required")
    @NotBlank(message = "Resource must not be blank")
    @Size(min = 3, max = 100, message = "Resource must contain between 3 and 100 characters")
    private String resource;

    @NotNull(message = "Action is required")
    @NotBlank(message = "Action must not be blank")
    private PermissionAction action;
}

