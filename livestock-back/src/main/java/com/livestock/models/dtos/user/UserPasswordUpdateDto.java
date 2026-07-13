package com.livestock.models.dtos.user;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserPasswordUpdateDto {

    @NotBlank
    private String oldPassword;

    @NotBlank
    @Size(min = 12)
    private String newPassword;
}