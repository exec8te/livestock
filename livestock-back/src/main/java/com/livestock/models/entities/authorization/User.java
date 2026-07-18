package com.livestock.models.entities.authorization;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.hibernate.annotations.SQLRestriction;

import java.time.LocalDateTime;

/**
 * This class represent a user.
 * A user have : id, fullName, email, passwordHash, role, lastLoginAt, createdAt, updatedAt
 */
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table(name = "users")
@SQLRestriction("disabled = false")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_user")
    private Long id;

    @Column(name = "full_name", length = 120, nullable = false)
    @NotBlank
    @Size(min = 3, max = 120, message = "FullName must contain between 4 and 120 characters")
    private String fullName;

    @Column(name = "email", length = 320, nullable = false, unique = true)
    @NotBlank
    @Size(max = 320, message = "Email must contain max 320 characters")
    @Email
    private String email;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private Role role;

    @Column(name = "password_hash", length = 255, nullable = false)
    @NotBlank
    @ToString.Exclude
    private String passwordHash;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    private Boolean disabled = false;


    // PrePersist and PreUpdate are used to set the creation and update date automatically
    @PrePersist
    public void prePersist() {
        this.createdAt = LocalDateTime.now();
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
