package com.livestock.models.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * This class represent a dto of user password update.
 * The dto require : oldPassword, newPassword
 * The oldPassword is required to check if the user know is last password.
 */
@Data
@AllArgsConstructor
public class UserPasswordUpdateDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 12)
    private String newPassword;
}