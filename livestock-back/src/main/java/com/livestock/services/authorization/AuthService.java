package com.livestock.services.authorization;

import com.livestock.mappers.authorization.LoginMapper;
import com.livestock.mappers.authorization.UserMapper;
import com.livestock.models.dtos.authorization.login.LoginRequestDto;
import com.livestock.models.dtos.authorization.login.LoginResponseDto;
import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final LoginMapper loginMapper;
    private final UserService userService;

    public LoginResponseDto login(LoginRequestDto dto) {

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        userService.updateDateLastLogin(dto.getEmail());

        return loginMapper.toResponseDto(
                token,
                jwtService.getExpirationTime()
        );
    }

    public LoginResponseDto register(RegisterRequestDto dto) {

        userService.register(dto);

        Authentication authentication =
                authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(
                                dto.getEmail(),
                                dto.getPassword()
                        )
                );

        UserDetails userDetails =
                (UserDetails) authentication.getPrincipal();

        String token = jwtService.generateToken(userDetails);

        userService.updateDateLastLogin(dto.getEmail());

        return loginMapper.toResponseDto(
                token,
                jwtService.getExpirationTime()
        );
    }
}