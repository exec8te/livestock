package com.livestock.models.dtos.authorization.role;

import com.livestock.validation.annotation.UppercaseOnly;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 100, message = "Name must contain between 3 and 100 characters")
    @UppercaseOnly(message = "Name, only uppercase letters are allowed")
    private String name;

    @NotEmpty(message = "At least one permission is required")
    private List<Long> permissionIds;
}

