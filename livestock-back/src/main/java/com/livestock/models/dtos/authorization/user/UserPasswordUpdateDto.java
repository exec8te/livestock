package com.livestock.models.dtos.authorization.user;

import com.livestock.validations.annotations.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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

    @NotNull(message = "Old password is required")
    @NotBlank(message = "Old password must not be blank")
    @ValidPassword
    private String oldPassword;

    @NotNull(message = "Password is required")
    @ValidPassword
    private String newPassword;
}