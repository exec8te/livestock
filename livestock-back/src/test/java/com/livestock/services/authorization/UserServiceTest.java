package com.livestock.services.authorization;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.authorization.UserNotFoundException;
import com.livestock.mappers.authorization.UserMapper;
import com.livestock.models.dtos.authorization.user.UserCreateDto;
import com.livestock.models.dtos.authorization.user.UserResponseDto;
import com.livestock.models.dtos.authorization.user.UserUpdateDto;
import com.livestock.models.entities.authorization.Role;
import com.livestock.models.entities.authorization.User;
import com.livestock.repositories.authorization.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private RoleService roleService;
    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private UserService userService;

    private User user;
    private UserResponseDto userResponseDto;
    private final UUID userId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        Role role = Role.builder().id(1L).name("USER").build();
        user = User.builder()
                .id(userId)
                .email("test@test.com")
                .fullName("Test User")
                .role(role)
                .build();

        userResponseDto = UserResponseDto.builder()
                .id(userId)
                .email("test@test.com")
                .fullName("Test User")
                .role("USER")
                .build();
    }

    @Test
    void getUserById_WhenUserExists_ShouldReturnDto() {
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        UserResponseDto result = userService.getUserById(userId);

        assertNotNull(result);
        assertEquals(userResponseDto.getEmail(), result.getEmail());
    }

    @Test
    void getUserById_WhenUserDoesNotExist_ShouldThrowException() {
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(UserNotFoundException.class, () -> userService.getUserById(userId));
    }

    @Test
    void getAll_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<User> userPage = new PageImpl<>(List.of(user));
        // Note: The actual method signature in UserService might have changed or might be different from the mock setup here
        // If it's userRepository.findAll(Specification, Pageable), this mock will fail.
        // Looking at UserService.java: return userRepository.findAll(spec, pageable)
        when(userRepository.findAll(any(org.springframework.data.jpa.domain.Specification.class), any(Pageable.class))).thenReturn(userPage);
        when(userMapper.toResponseDto(any())).thenReturn(userResponseDto);

        com.livestock.models.dtos.authorization.user.UserFilterDto filter = new com.livestock.models.dtos.authorization.user.UserFilterDto();
        Page<UserResponseDto> result = userService.getAll(filter, pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }

    @Test
    void updateUser_WhenNoChanges_ShouldThrowException() {
        UserUpdateDto dto = UserUpdateDto.builder().email("test@test.com").fullName("Test User").build();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertThrows(BadRequestException.class, () -> userService.updateUser(userId, dto));
    }
    @Test
    void createUser_ShouldSaveUserWithDisabledStatus() {
        UserCreateDto dto = UserCreateDto.builder()
                .fullName("New User")
                .email("new@test.com")
                .password("Password123!")
                .disabled(true)
                .build();

        User mappedUser = new User();
        mappedUser.setFullName(dto.getFullName());
        mappedUser.setEmail(dto.getEmail());

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userMapper.toCreateEntity(dto)).thenReturn(mappedUser);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(UserResponseDto.builder().email(dto.getEmail()).build());

        userService.createUser(dto, null);

        assertTrue(mappedUser.getDisabled(), "User should be disabled as per DTO");
        assertEquals("encodedPassword", mappedUser.getPasswordHash());
    }

    @Test
    void createUser_WhenDisabledIsNull_ShouldDefaultToFalse() {
        UserCreateDto dto = UserCreateDto.builder()
                .fullName("New User")
                .email("new2@test.com")
                .password("Password123!")
                .disabled(null)
                .build();

        User mappedUser = new User();
        mappedUser.setFullName(dto.getFullName());
        mappedUser.setEmail(dto.getEmail());

        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userMapper.toCreateEntity(dto)).thenReturn(mappedUser);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("encodedPassword");
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(userMapper.toResponseDto(any(User.class))).thenReturn(UserResponseDto.builder().email(dto.getEmail()).build());

        userService.createUser(dto, null);

        assertNotNull(mappedUser.getDisabled());
        assertFalse(mappedUser.getDisabled(), "User should default to not disabled");
    }

    @Test
    void updateUserByAdmin_WhenDisabledIsChanged_ShouldUpdateUser() {
        com.livestock.models.dtos.authorization.user.UserUpdateAdminDto dto =
                com.livestock.models.dtos.authorization.user.UserUpdateAdminDto.builder()
                        .email("test@test.com")
                        .fullName("Test User")
                        .roleId(1L)
                        .disabled(true)
                        .build();

        user.setDisabled(false);
        Role role = Role.builder().id(1L).name("USER").permissions(java.util.Set.of()).build();
        user.setRole(role);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(roleService.findEntityById(1L)).thenReturn(role);
        when(userMapper.toResponseDto(user)).thenReturn(userResponseDto);

        com.livestock.models.principals.authorization.UserPrincipal principal =
                new com.livestock.models.principals.authorization.UserPrincipal(user);

        UserResponseDto result = userService.updateUserByAdmin(userId, dto, principal);

        org.mockito.Mockito.verify(userMapper).updateByAdminEntity(dto, user);
        assertNotNull(result);
    }
}
