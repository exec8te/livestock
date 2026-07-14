package com.livestock.services;

import com.livestock.exceptions.BadRequestException;
import com.livestock.mappers.UserMapper;
import com.livestock.models.dtos.user.UserAuthDto;
import com.livestock.models.dtos.user.UserResponseDto;
import com.livestock.models.entities.User;
import com.livestock.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;

    public UserResponseDto login(UserAuthDto dto) {
        if (!userRepository.existsByEmail(dto.getEmail())) {
            throw new BadRequestException("Password or email or incorrect");
        }

        User user = userRepository.findByEmail(dto.getEmail());

        if (passwordEncoder.matches(
                dto.getPasswordClear(),
                user.getPasswordHash()
        )) {
            throw new BadRequestException("Password or email or incorrect");
        }

        return userMapper.toResponseDto(user);
    }
}