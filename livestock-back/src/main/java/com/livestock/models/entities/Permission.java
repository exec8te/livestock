package com.livestock.models.entities;

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
    @Size(min = 3, max = 100)
    private String resource;

    @Column(name = "action", length = 6, nullable = false)
    @NotBlank
    @Size(min = 6, max = 6)
    private String action;
}
