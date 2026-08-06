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

        Role role = findEntityById(roleId);

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
    public RoleResponseDto createRole(
            RoleCreateDto dto,
            UserPrincipal principal
    ) {

        if (roleRepository.existsByName(dto.getName())) {

            log.warn(
                    "event=ROLE_CREATED actorId={} roleName={} reason=ROLE_ALREADY_EXISTS status=FAILED",
                    principal.getId(),
                    dto.getName()
            );

            throw new RoleAlreadyExistsException(dto.getName());
        }


        List<Permission> permissions =
                permissionService.findAllByIds(dto.getPermissionIds());


        if (permissions.size() != dto.getPermissionIds().size()) {

            log.warn(
                    "event=ROLE_CREATED actorId={} reason=PERMISSION_NOT_FOUND status=FAILED",
                    principal.getId()
            );

            throw new BadRequestException(
                    "One or more permissions do not exist"
            );
        }


        Role role = roleMapper.toCreateEntity(dto);

        role.setPermissions(
                new HashSet<>(permissions)
        );


        Role savedRole = roleRepository.save(role);


        log.info(
                "event=ROLE_CREATED actorId={} targetType=ROLE targetId={} status=SUCCESS",
                principal.getId(),
                savedRole.getId()
        );


        return roleMapper.toResponseDto(savedRole);
    }


    @Transactional
    public RoleResponseDto updateRole(
            Long roleId,
            RoleUpdateDto dto,
            UserPrincipal principal
    ) {

        Role role = findEntityById(roleId);


        List<Permission> permissions =
                permissionService.findAllByIds(dto.getPermissionIds());


        if (permissions.size() != dto.getPermissionIds().size()) {

            log.warn(
                    "event=ROLE_UPDATED actorId={} targetId={} reason=PERMISSION_NOT_FOUND status=FAILED",
                    principal.getId(),
                    roleId
            );

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

            log.warn(
                    "event=ROLE_UPDATED actorId={} targetId={} reason=NO_CHANGES status=FAILED",
                    principal.getId(),
                    roleId
            );

            throw new BadRequestException(
                    "No changes detected"
            );
        }


        if (!role.getName().equals(dto.getName())
                && roleRepository.existsByName(dto.getName())) {

            log.warn(
                    "event=ROLE_UPDATED actorId={} targetId={} reason=ROLE_ALREADY_EXISTS status=FAILED",
                    principal.getId(),
                    roleId
            );

            throw new RoleAlreadyExistsException(dto.getName());
        }


        if (Boolean.TRUE.equals(dto.getIsDefault())
                && !Boolean.TRUE.equals(role.getIsDefault())) {


            Role oldDefaultRole =
                    roleRepository.findByIsDefaultTrue()
                            .orElse(null);


            if (oldDefaultRole != null) {

                oldDefaultRole.setIsDefault(false);


                userRepository.updateRoleForUsers(
                        oldDefaultRole.getId(),
                        role.getId()
                );


                log.warn(
                        "event=DEFAULT_ROLE_CHANGED actorId={} oldRoleId={} newRoleId={} status=SUCCESS",
                        principal.getId(),
                        oldDefaultRole.getId(),
                        roleId
                );
            }
        }


        roleMapper.updateEntity(dto, role);

        role.setPermissions(
                new HashSet<>(permissions)
        );


        log.info(
                "event=ROLE_UPDATED actorId={} targetType=ROLE targetId={} status=SUCCESS",
                principal.getId(),
                roleId
        );


        return roleMapper.toResponseDto(role);
    }


    @Transactional
    public void deleteRole(
            Long roleId,
            UserPrincipal principal
    ) {

        Role role = findEntityById(roleId);


        if (Boolean.TRUE.equals(role.getIsDefault())) {

            log.warn(
                    "event=ROLE_DELETED actorId={} targetId={} reason=DEFAULT_ROLE status=FAILED",
                    principal.getId(),
                    roleId
            );

            throw new BadRequestException(
                    "Cannot delete default role"
            );
        }


        roleRepository.delete(role);


        log.warn(
                "event=ROLE_DELETED actorId={} targetType=ROLE targetId={} status=SUCCESS",
                principal.getId(),
                roleId
        );
    }


    public Role getDefaultRole() {

        return roleRepository.findByIsDefaultTrue()
                .orElseThrow(
                        () -> new RoleNotFoundException("Default role")
                );
    }


    @Transactional
    public void setDefaultRole(
            Long roleId,
            UserPrincipal principal
    ) {

        Role role = findEntityById(roleId);


        if (Boolean.TRUE.equals(role.getIsDefault())) {

            throw new BadRequestException(
                    "Role is already default"
            );
        }


        Role oldDefault =
                roleRepository.findByIsDefaultTrue()
                        .orElse(null);


        if (oldDefault != null) {

            oldDefault.setIsDefault(false);

            userRepository.updateRoleForUsers(
                    oldDefault.getId(),
                    roleId
            );
        }


        role.setIsDefault(true);


        log.warn(
                "event=DEFAULT_ROLE_CHANGED actorId={} oldRoleId={} newRoleId={} status=SUCCESS",
                principal.getId(),
                oldDefault != null ? oldDefault.getId() : null,
                roleId
        );
    }


    protected Role findEntityById(Long id) {

        return roleRepository.findById(id)
                .orElseThrow(
                        () -> new RoleNotFoundException(id)
                );
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

    public Role getRoleEntityById(Long id) {
        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }
}