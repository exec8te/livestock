package com.livestock.models.dtos.authorization.user;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UserFilterDto {
    private String fullName;
    private String email;
    private String role;
    private Boolean disabled;
    private LocalDateTime createdAtFrom;
    private LocalDateTime createdAtTo;
    private String search; // recherche globale (fullName + email)
}