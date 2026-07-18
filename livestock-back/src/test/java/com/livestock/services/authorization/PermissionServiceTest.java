package com.livestock.services.authorization;

import com.livestock.exceptions.authorization.PermissionAlreadyExistsException;
import com.livestock.exceptions.authorization.PermissionNotFoundException;
import com.livestock.mappers.authorization.PermissionMapper;
import com.livestock.models.dtos.authorization.permission.PermissionAction;
import com.livestock.models.dtos.authorization.permission.PermissionCreateDto;
import com.livestock.models.entities.authorization.Permission;
import com.livestock.repositories.authorization.PermissionRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PermissionServiceTest {

    @Mock
    private PermissionRepository permissionRepository;
    @Mock
    private PermissionMapper permissionMapper;

    @InjectMocks
    private PermissionService permissionService;

    @Test
    void createPermission_WhenAlreadyExists_ShouldThrowException() {
        PermissionCreateDto dto = PermissionCreateDto.builder()
                .resource("USER")
                .action(PermissionAction.CREATE)
                .build();

        when(permissionRepository.existsByResourceAndAction(any(), any())).thenReturn(true);

        assertThrows(PermissionAlreadyExistsException.class, () -> permissionService.createPermission(dto));
    }

    @Test
    void getPermissionById_WhenDoesNotExist_ShouldThrowException() {
        when(permissionRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(PermissionNotFoundException.class, () -> permissionService.getPermissionById(1L));
    }

    @Test
    void deletePermission_WhenExists_ShouldCallDelete() {
        Permission permission = Permission.builder().id(1L).build();
        when(permissionRepository.findById(1L)).thenReturn(Optional.of(permission));

        permissionService.deletePermission(1L);

        verify(permissionRepository).delete(permission);
    }
}
