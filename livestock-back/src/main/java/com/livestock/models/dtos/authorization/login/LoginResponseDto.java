package com.livestock.models.dtos.authorization.login;

import lombok.Builder;
import lombok.Data;

/**
 * This class represent a dto of user authentification.
 * The dto have : token, expiresIn
 */
@Data
@Builder
public class LoginResponseDto {

    private String token;
    private long expiresIn;
}