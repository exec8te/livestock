package com.livestock.models.dtos.authorization.login;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginInternalResult {
    private String token;
    private LoginResponseDto responseDto;
}
