package com.livestock.models.dtos.user;

import com.livestock.models.entities.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class UserCreateDto {
    private String fullName;
    private String email;
    private String password;
    private Role role;
}
