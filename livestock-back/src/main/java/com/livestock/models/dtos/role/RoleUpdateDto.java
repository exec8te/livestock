package com.livestock.models.dtos.role;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
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

    @NotBlank
    @Size(min = 3, max = 100, message = "Name must contain between 3 and 100 characters")
    private String name;

    @NotEmpty
    private List<Long> permissionIds;

}
