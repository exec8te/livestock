package com.livestock.models.dtos.permission;

import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    @Size(min = 3, max = 100, message = "Resource must contain between 3 and 100 characters")
    private String resource;

    @NotBlank
    @Size(min = 4, max = 15, message = "Action must contain between 4 and 15 characters")
    private String action;
}

