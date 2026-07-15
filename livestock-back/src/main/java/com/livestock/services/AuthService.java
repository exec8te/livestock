package com.livestock.services;

import com.livestock.exceptions.EmailAlreadyExistsException;
import com.livestock.exceptions.RoleNotFoundException;
import com.livestock.mappers.LoginMapper;
import com.livestock.mappers.UserMapper;
import com.livestock.models.dtos.login.LoginRequestDto;
import com.livestock.models.dtos.login.LoginResponseDto;
import com.livestock.models.dtos.login.RegisterRequestDto;
import com.livestock.models.entities.Role;
import com.livestock.models.entities.User;
import com.livestock.models.principals.UserPrincipal;
import com.livestock.repositories.RoleRepository;
import com.livestock.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginMapper loginMapper;
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;

    public LoginResponseDto login(LoginRequestDto dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPasswordClear()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        return loginMapper.toResponseDto(token, jwtService.getExpirationTime());
    }

    public LoginResponseDto register(RegisterRequestDto dto) {

        if (userRepository.existsByEmail(dto.getEmail())) {
            throw new EmailAlreadyExistsException(dto.getEmail());
        }

        User user = userMapper.toCreateEntity(dto);

        user.setPasswordHash(
                passwordEncoder.encode(dto.getPassword())
        );

        Role defaultRole = roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("USER"));

        user.setRole(defaultRole);

        userRepository.save(user);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        UserPrincipal principal =
                (UserPrincipal) authentication.getPrincipal();

        String token = jwtService.generateToken(principal);

        return loginMapper.toResponseDto(
                token,
                jwtService.getExpirationTime()
        );
    }
}