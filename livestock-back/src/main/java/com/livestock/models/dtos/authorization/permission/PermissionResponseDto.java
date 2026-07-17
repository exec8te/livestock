package com.livestock.models.dtos.authorization.permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * This class represent a dto of permission response.
 * The dto have : resource, action
 */
@Data
@Builder
@AllArgsConstructor
public class PermissionResponseDto {
    private String resource;
    private PermissionAction action;
}
