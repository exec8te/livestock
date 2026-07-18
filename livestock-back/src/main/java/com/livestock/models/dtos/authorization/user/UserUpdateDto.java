package com.livestock.models.dtos.authorization.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @NotNull(message = "FullName is required")
    @NotBlank(message = "FullName must not be blank")
    @Size(min = 2, max = 100, message = "FullName must contain between 2 and 100 characters")
    private String fullName;

    @NotNull(message = "Email is required")
    @NotBlank(message = "Email must not be blank")
    @Size(max = 320, message = "Email must contain max 320 characters")
    @Email
    private String email;
}
