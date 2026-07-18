package com.livestock.models.entities.authorization;

import com.livestock.models.dtos.authorization.permission.PermissionAction;
import com.livestock.validations.annotations.UppercaseOnly;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * This class represent a permission.
 * A permission is linked to a role.
 * A permission have : id, resource, action
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "permissions")
public class Permission {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_permission")
    private Long id;

    @Column(name = "resource", length = 100, nullable = false)
    @NotBlank(message = "Resource must not be blank")
    @Size(min = 3, max = 100, message = "Resource must contain between 3 and 100 characters")
    @UppercaseOnly(message = "Resource, only uppercase letters are allowed")
    private String resource;

    @Enumerated(EnumType.STRING)
    @Column(name = "action", nullable = false)
    @UppercaseOnly(message = "Action, only uppercase letters are allowed")
    private PermissionAction action;
}
