package com.livestock.models.dtos.user;

import com.livestock.models.entities.Role;
import jakarta.validation.constraints.NotBlank;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserUpdateDto {
    @NotBlank
    private String fullName;
    @NotBlank
    private String email;
    @NotBlank
    private Role role;
}
