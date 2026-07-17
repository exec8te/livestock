package com.livestock.models.dtos.user;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    @Size(min = 2, max = 100, message = "FullName must contain between 2 and 100 characters")
    private String fullName;

    @NotBlank
    @Email
    private String email;
}
