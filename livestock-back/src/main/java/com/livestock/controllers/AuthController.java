package com.livestock.controllers;

import com.livestock.models.dtos.user.UserAuthDto;
import com.livestock.models.dtos.user.UserResponseDto;
import com.livestock.services.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @GetMapping("/login")
    public UserResponseDto login(UserAuthDto dto) {

        return authService.login(dto);
    }
}