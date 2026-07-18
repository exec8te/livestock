package com.livestock.models.dtos.authorization.role;

import com.livestock.validations.annotations.UppercaseOnly;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.util.List;

/**
 * This class represent a dto of role update.
 * The dto require : name
 */
@Data
@Builder
@AllArgsConstructor
public class RoleUpdateDto {

    @NotNull(message = "Name is required")
    @NotBlank(message = "Name must not be blank")
    @Size(min = 3, max = 100, message = "Name must contain between 3 and 100 characters")
    @UppercaseOnly(message = "Name, only uppercase letters are allowed")
    private String name;

    @NotNull(message = "isDefault is required")
    private Boolean isDefault;

    @NotEmpty
    private List<Long> permissionIds;

}
