package com.livestock.models.dtos.login;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * This class represent a dto of user authentification.
 * The dto require : email, password
 */
@Data
@Builder
@AllArgsConstructor
public class LoginRequestDto {
    @NotBlank
    @Email
    private String email;

    @NotNull
    @NotBlank
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*[0-9])(?=.*[.!*?%@=$£\\[\\]+]).{12,255}$",
            message = "Password must contain at least 12 characters, one uppercase, one lowercase, one digit and one special character"
    )
    private String password; // password clear
}
