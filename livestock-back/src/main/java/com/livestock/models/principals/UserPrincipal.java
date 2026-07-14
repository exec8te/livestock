package com.livestock.models.principals;

import com.livestock.models.entities.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

public class UserPrincipal implements UserDetails {

    private final Long id;
    private final String email;
    private final String password;
    private final Collection<? extends GrantedAuthority> authorities;


    public UserPrincipal(User user) {

        this.id = user.getId();
        this.email = user.getEmail();
        this.password = user.getPasswordHash();

        this.authorities = List.of(
                new SimpleGrantedAuthority(
                        "ROLE_" + user.getRole().getName()
                )
        );
    }


    public Long getId() {
        return id;
    }


    @Override
    public String getUsername() {
        return email;
    }


    @Override
    public String getPassword() {
        return password;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }
}