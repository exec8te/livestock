package com.livestock.models.dtos.authorization.login;

import com.livestock.validation.annotation.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represent a dto of user create.
 * The dto require : fullName, email, password, roleId
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequestDto {

    @NotBlank
    @Size(min = 2, max = 100, message = "FullName must contain between 2 and 100 characters")
    private String fullName;

    @NotBlank
    @Email
    @Size(max = 320, message = "Email must contain max 320 characters")
    private String email;

    @NotNull(message = "Password is required")
    @ValidPassword
    private String password;
}

