package com.livestock.models.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * This class represent a dto of user update.
 * The dto require : fullName, email, roleId
 */
@Data
@Builder
@AllArgsConstructor
public class UserUpdateDto {
    @NotBlank
    private String fullName;

    @NotBlank
    @Email
    private String email;
}
