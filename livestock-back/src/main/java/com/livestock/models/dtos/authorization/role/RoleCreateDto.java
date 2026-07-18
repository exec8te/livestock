package com.livestock.models.dtos.authorization.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

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
    @Size(min = 3, max = 100, message = "Name must contain between 3 and 100 characters")
    @Pattern(
            regexp = "^[A-Z]+$",
            message = "Only uppercase letters are allowed"
    )
    private String name;

    @NotEmpty(message = "At least one permission is required")
    private List<Long> permissionIds;
}

