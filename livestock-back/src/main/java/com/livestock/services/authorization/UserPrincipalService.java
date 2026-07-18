package com.livestock.services.authorization;

import com.livestock.models.principals.authorization.UserPrincipal;
import com.livestock.repositories.authorization.UserRepository;
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