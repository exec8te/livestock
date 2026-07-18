package com.livestock.controllers.authorization;

import com.livestock.models.dtos.authorization.login.LoginRequestDto;
import com.livestock.models.dtos.authorization.login.LoginResponseDto;
import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import com.livestock.services.authorization.AuthService;
import jakarta.validation.Valid;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
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
            @Valid @RequestBody LoginRequestDto dto,
            HttpServletResponse response
    ) {
        LoginResponseDto loginResponse = authService.login(dto);
        addTokenToCookie(response, loginResponse.getToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/register")
    public ResponseEntity<LoginResponseDto> register(
            @Valid @RequestBody RegisterRequestDto dto,
            HttpServletResponse response
    ) {
        LoginResponseDto loginResponse = authService.register(dto);
        addTokenToCookie(response, loginResponse.getToken());
        return ResponseEntity.ok(loginResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletResponse response) {
        ResponseCookie cookie = ResponseCookie.from("token", "")
                .httpOnly(true)
                .secure(false) // Mettre à true en production (HTTPS)
                .path("/")
                .maxAge(0)
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
        return ResponseEntity.ok().build();
    }

    private void addTokenToCookie(HttpServletResponse response, String token) {
        ResponseCookie cookie = ResponseCookie.from("token", token)
                .httpOnly(true)
                .secure(false) // Mettre à true en production (HTTPS)
                .path("/")
                .maxAge(3600) // 1 heure par exemple, à ajuster selon JwtService
                .sameSite("Lax")
                .build();
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }
}