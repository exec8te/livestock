package com.livestock.services.authorization;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.authorization.RoleAlreadyExistsException;
import com.livestock.exceptions.authorization.RoleNotFoundException;
import com.livestock.mappers.authorization.RoleMapper;
import com.livestock.models.dtos.authorization.role.RoleCreateDto;
import com.livestock.models.dtos.authorization.role.RoleResponseDto;
import com.livestock.models.entities.authorization.Permission;
import com.livestock.models.entities.authorization.Role;
import com.livestock.models.entities.authorization.User;
import com.livestock.models.principals.authorization.UserPrincipal;
import com.livestock.repositories.authorization.RoleRepository;
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

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RoleServiceTest {

    @Mock
    private RoleRepository roleRepository;
    @Mock
    private RoleMapper roleMapper;
    @Mock
    private PermissionService permissionService;
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private RoleService roleService;

    private Role role;
    private RoleResponseDto roleResponseDto;
    private UserPrincipal principal;

    @BeforeEach
    void setUp() {
        role = Role.builder()
                .id(1L)
                .name("ADMIN")
                .isDefault(false)
                .permissions(new HashSet<>())
                .build();

        roleResponseDto = RoleResponseDto.builder()
                .id(1L)
                .name("ADMIN")
                .isDefault(false)
                .build();

        User user = User.builder()
                .id(1L)
                .email("admin@test.com")
                .role(role)
                .build();
        principal = new UserPrincipal(user);
    }

    @Test
    void getRoleById_WhenRoleExists_ShouldReturnDto() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));
        when(roleMapper.toResponseDto(role)).thenReturn(roleResponseDto);

        RoleResponseDto result = roleService.getRoleById(1L);

        assertNotNull(result);
        assertEquals(roleResponseDto.getName(), result.getName());
        verify(roleRepository).findById(1L);
    }

    @Test
    void getRoleById_WhenRoleDoesNotExist_ShouldThrowException() {
        when(roleRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RoleNotFoundException.class, () -> roleService.getRoleById(1L));
    }

    @Test
    void createRole_WhenNameExists_ShouldThrowException() {
        RoleCreateDto dto = RoleCreateDto.builder().name("ADMIN").build();
        when(roleRepository.existsByName("ADMIN")).thenReturn(true);

        assertThrows(RoleAlreadyExistsException.class, () -> roleService.createRole(dto, principal));
    }

    @Test
    void createRole_WhenPermissionsDoNotExist_ShouldThrowException() {
        RoleCreateDto dto = RoleCreateDto.builder()
                .name("NEW_ROLE")
                .permissionIds(List.of(1L, 2L))
                .build();
        when(roleRepository.existsByName("NEW_ROLE")).thenReturn(false);
        when(permissionService.findAllByIds(any())).thenReturn(Collections.emptyList());

        assertThrows(BadRequestException.class, () -> roleService.createRole(dto, principal));
    }

    @Test
    void createRole_WhenValid_ShouldReturnDto() {
        RoleCreateDto dto = RoleCreateDto.builder()
                .name("NEW_ROLE")
                .permissionIds(List.of(1L))
                .build();
        Permission permission = Permission.builder().id(1L).build();

        when(roleRepository.existsByName("NEW_ROLE")).thenReturn(false);
        when(permissionService.findAllByIds(dto.getPermissionIds())).thenReturn(List.of(permission));
        when(roleMapper.toCreateEntity(dto)).thenReturn(role);
        when(roleRepository.save(any(Role.class))).thenReturn(role);
        when(roleMapper.toResponseDto(role)).thenReturn(roleResponseDto);

        RoleResponseDto result = roleService.createRole(dto, principal);

        assertNotNull(result);
        verify(roleRepository).save(any(Role.class));
    }

    @Test
    void deleteRole_WhenIsDefault_ShouldThrowException() {
        role.setIsDefault(true);
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        assertThrows(BadRequestException.class, () -> roleService.deleteRole(1L, principal));
    }

    @Test
    void deleteRole_WhenValid_ShouldCallDelete() {
        when(roleRepository.findById(1L)).thenReturn(Optional.of(role));

        roleService.deleteRole(1L, principal);

        verify(roleRepository).delete(role);
    }

    @Test
    void getAll_ShouldReturnPage() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Role> rolePage = new PageImpl<>(List.of(role));
        when(roleRepository.findAll(pageable)).thenReturn(rolePage);
        when(roleMapper.toResponseDto(any())).thenReturn(roleResponseDto);

        Page<RoleResponseDto> result = roleService.getAll(pageable);

        assertNotNull(result);
        assertEquals(1, result.getTotalElements());
    }
}
