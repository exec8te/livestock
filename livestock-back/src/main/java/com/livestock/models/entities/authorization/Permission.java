package com.livestock.models.entities.authorization;

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
    @NotBlank
    @Size(min = 3, max = 100, message = "Resource must contain between 3 and 100 characters")
    private String resource;

    @Column(name = "action", nullable = false)
    @NotBlank
    @Size(min = 4, max = 100, message = "Action must contain between 4 and 100 characters")
    private String action;
}
