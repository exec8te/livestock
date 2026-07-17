package com.livestock.models.dtos.login;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
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
    private String email;

    @NotBlank
    @Size(min = 12, max = 255, message = "Password must be between 12 and 255 characters")
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[.!*?%@=$£\\[\\]+]).{12,255}$",
            message = "Password must contain at least 12 characters, one uppercase, one lowercase, one digit and one special character"
    )
    private String password;
}

