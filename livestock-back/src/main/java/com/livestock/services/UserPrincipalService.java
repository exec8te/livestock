package com.livestock.services;

import com.livestock.models.principals.UserPrincipal;
import com.livestock.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserPrincipalService
        implements org.springframework.security.core.userdetails.UserDetailsService {


    private final UserRepository userRepository;


    @Override
    public UserPrincipal loadUserByUsername(String email) {

        return userRepository.findByEmail(email)
                .map(UserPrincipal::new)
                .orElseThrow(
                        () -> new UsernameNotFoundException(
                                "User not found"
                        )
                );
    }
}