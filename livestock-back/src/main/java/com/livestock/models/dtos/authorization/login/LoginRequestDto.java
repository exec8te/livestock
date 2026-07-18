package com.livestock.models.dtos.authorization.login;


import com.livestock.validations.annotations.ValidPassword;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
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
    @NotNull(message = "Email is required")
    @NotBlank(message = "Email must not be blank")
    @Email
    private String email;

    @NotNull(message = "Password is required")
    @ValidPassword
    private String password; // password clear
}
