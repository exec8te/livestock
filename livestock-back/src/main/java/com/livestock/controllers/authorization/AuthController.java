package com.livestock.controllers.authorization;

import com.livestock.models.dtos.authorization.login.LoginRequestDto;
import com.livestock.models.dtos.authorization.login.LoginResponseDto;
import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.services.authorization.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @PostMapping("/login")
    public ResponseEntity<LoginResponseDto> login(
            @Valid @RequestBody LoginRequestDto dto
    ) {
        return ResponseEntity.ok(
                authService.login(dto)
        );
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(
            @Valid @RequestBody RegisterRequestDto dto
    ) {
        return ResponseEntity.ok(
                authService.register(dto)
        );
    }
}