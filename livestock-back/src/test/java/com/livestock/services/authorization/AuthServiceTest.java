package com.livestock.services.authorization;

import com.livestock.mappers.authorization.LoginMapper;
import com.livestock.models.dtos.authorization.login.LoginRequestDto;
import com.livestock.models.dtos.authorization.login.LoginResponseDto;
import com.livestock.models.dtos.authorization.login.RegisterRequestDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private JwtService jwtService;
    @Mock
    private LoginMapper loginMapper;
    @Mock
    private UserService userService;

    @InjectMocks
    private AuthService authService;

    @Test
    void login_ShouldReturnResponse() {
        LoginRequestDto dto = LoginRequestDto.builder()
                .email("test@test.com")
                .password("password")
                .build();

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mock-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        when(loginMapper.toResponseDto("mock-token", 3600L))
                .thenReturn(LoginResponseDto.builder().token("mock-token").build());

        LoginResponseDto result = authService.login(dto);

        assertNotNull(result);
        assertEquals("mock-token", result.getToken());
    }

    @Test
    void register_ShouldCallUserServiceAndReturnResponse() {
        RegisterRequestDto dto = RegisterRequestDto.builder()
                .email("new@test.com")
                .password("password")
                .fullName("New User")
                .build();

        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(authenticationManager.authenticate(any())).thenReturn(authentication);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(jwtService.generateToken(userDetails)).thenReturn("mock-token");
        when(jwtService.getExpirationTime()).thenReturn(3600L);
        when(loginMapper.toResponseDto("mock-token", 3600L))
                .thenReturn(LoginResponseDto.builder().token("mock-token").build());

        LoginResponseDto result = authService.register(dto);

        assertNotNull(result);
        verify(userService).register(dto);
    }
}
