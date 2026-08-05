package com.livestock.models.principals.authorization;

import com.livestock.models.entities.authorization.Role;
import com.livestock.models.entities.authorization.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.UUID;

/**
 * This class represent the security context of the user.
 * The security context have : id, email, passwordHash, role, authorities
 * It is linked to the User entity and spring security.
 */
public class UserPrincipal implements UserDetails {

    private final UUID id;
    private final String email;
    private final String passwordHash;
    private final Role role;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(User user) {

        this.id = user.getId();
        this.email = user.getEmail();
        this.passwordHash = user.getPasswordHash();
        this.role = user.getRole();
        this.authorities = user.getRole()
                .getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(
                        permission.getResource() + "_" + permission.getAction()
                ))
                .toList();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    public UUID getId() {
        return id;
    }


    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public String getPassword() {
        return passwordHash;
    }
}