package com.livestock.services.authorization;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.authorization.RoleAlreadyExistsException;
import com.livestock.exceptions.authorization.RoleNotFoundException;
import com.livestock.mappers.authorization.RoleMapper;
import com.livestock.models.dtos.authorization.role.RoleCreateDto;
import com.livestock.models.dtos.authorization.role.RoleResponseDto;
import com.livestock.models.dtos.authorization.role.RoleUpdateDto;
import com.livestock.models.entities.authorization.Permission;
import com.livestock.models.entities.authorization.Role;
import com.livestock.models.principals.authorization.UserPrincipal;
import com.livestock.repositories.authorization.RoleRepository;
import com.livestock.repositories.authorization.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionService permissionService;
    private final UserRepository userRepository;

    public RoleResponseDto getRoleById(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        return roleMapper.toResponseDto(role);
    }

    public RoleResponseDto getRoleByName(String roleName) {

        Role role = roleRepository.findByName(roleName)
                .orElseThrow(() -> new RoleNotFoundException(roleName));

        return roleMapper.toResponseDto(role);
    }

    public Page<RoleResponseDto> getAll(Pageable pageable) {

        return roleRepository.findAll(pageable)
                .map(roleMapper::toResponseDto);
    }

    @Transactional
    public RoleResponseDto createRole(RoleCreateDto dto) {

        if (roleRepository.existsByName(dto.getName())) {
            throw new RoleAlreadyExistsException(dto.getName());
        }

        List<Permission> permissions =
                permissionService.findAllByIds(dto.getPermissionIds());

        if (permissions.size() != dto.getPermissionIds().size()) {
            throw new BadRequestException("One or more permissions do not exist");
        }

        Role role = roleMapper.toCreateEntity(dto);

        role.setPermissions(new HashSet<>(permissions));

        Role savedRole = roleRepository.save(role);

        return roleMapper.toResponseDto(savedRole);
    }

    /**
     * Updates a role
     * 1. If the role is set to default (true), then other roles are set to false and users are updated
     * to set the new default role on their account
     *
     * @param roleId role id
     * @param dto    role update dto
     * @return RoleResponseDto
     */
    @Transactional
    public RoleResponseDto updateRole(
            Long roleId,
            RoleUpdateDto dto,
            UserPrincipal principal
    ) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        List<Permission> permissions =
                permissionService.findAllByIds(dto.getPermissionIds());

        if (permissions.size() != dto.getPermissionIds().size()) {
            throw new BadRequestException(
                    "One or more permissions do not exist"
            );
        }

        if (!hasChanges(
                role,
                dto.getName(),
                permissions,
                dto.getIsDefault()
        )) {
            throw new BadRequestException("No changes detected");
        }

        if (Boolean.TRUE.equals(dto.getIsDefault())
                && !Boolean.TRUE.equals(role.getIsDefault())) {

            Role oldDefaultRole = roleRepository.findByIsDefaultTrue()
                    .orElse(null);

            if (oldDefaultRole != null) {

                oldDefaultRole.setIsDefault(false);

                userRepository.updateRoleForUsers(
                        oldDefaultRole.getId(),
                        role.getId()
                );

                log.warn(
                        "event={} actorType={} actorId={} targetType={} targetId={} status=SUCCESS",
                        "ROLE_DEFAULT_UPDATED",
                        "USER",
                        principal.getId(),
                        "ROLE",
                        roleId
                );
            }

            role.setIsDefault(true);
        }

        roleMapper.updateEntity(dto, role);

        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toResponseDto(role);
    }

    @Transactional
    public void deleteRole(Long idToDelete, UserPrincipal principal) {

        Role role = roleRepository.findById(idToDelete)
                .orElseThrow(() -> new RoleNotFoundException(idToDelete));

        if (role.getIsDefault()) {
            throw new BadRequestException("Cannot delete default role");
        }

        roleRepository.delete(role);

        log.info(
                "event={} actorType={} actorId={} targetType={} targetId={} status=SUCCESS",
                "USER_DELETED",
                principal.getAuthorities(),
                principal.getId(),
                "ROLE",
                idToDelete
        );
    }

    public Role getDefaultRole() {

        return roleRepository.findByName("USER")
                .orElseThrow(() -> new RoleNotFoundException("USER"));
    }

    @Transactional
    public void setDefaultRole(Long roleId) {

        roleRepository.clearDefaultRole();

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        role.setIsDefault(true);
    }

    protected Role findEntityById(Long id) {

        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }

    private boolean hasChanges(
            Role role,
            String name,
            Collection<Permission> permissions,
            Boolean isDefault
    ) {

        return !Objects.equals(role.getName(), name)
                || !Objects.equals(
                role.getPermissions(),
                new HashSet<>(permissions)
        )
                || !Objects.equals(
                role.getIsDefault(),
                isDefault
        );
    }

}
