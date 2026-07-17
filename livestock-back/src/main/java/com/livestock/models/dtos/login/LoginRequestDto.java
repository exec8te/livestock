package com.livestock.models.dtos.login;


import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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
    @Size(min = 12, max = 255, message = "Password must be between 12 and 255 characters")
    private String password; // password clear
}
