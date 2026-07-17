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
import com.livestock.repositories.authorization.PermissionRepository;
import com.livestock.repositories.authorization.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PermissionRepository permissionRepository;

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

    public RoleResponseDto createRole(RoleCreateDto dto) {

        if (roleRepository.existsByName(dto.getName())) {
            throw new RoleAlreadyExistsException(dto.getName());
        }

        Role role = roleMapper.toCreateEntity(dto);

        Role savedRole = roleRepository.save(role);

        return roleMapper.toResponseDto(savedRole);
    }

    @Transactional
    public RoleResponseDto updateRole(Long roleId, RoleUpdateDto dto) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        if (!role.getName().equals(dto.getName())
                && roleRepository.existsByName(dto.getName())) {
            throw new RoleAlreadyExistsException(dto.getName());
        }

        List<Permission> permissions = permissionRepository.findAllById(dto.getPermissionIds());

        if (permissions.size() != dto.getPermissionIds().size()) {
            throw new BadRequestException("One or more permissions do not exist");
        }

        if (!roleMapper.hasChanges(role, dto.getName(), permissions)) {
            throw new BadRequestException("No changes were made");
        }

        role.setName(dto.getName());
        role.setPermissions(new HashSet<>(permissions));

        return roleMapper.toResponseDto(role);
    }

    @Transactional
    public void deleteRole(Long idToDelete, UserPrincipal principal) {

        Role role = roleRepository.findById(idToDelete)
                .orElseThrow(() -> new RoleNotFoundException(idToDelete));

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

    public Role findEntityById(Long id) {

        return roleRepository.findById(id)
                .orElseThrow(() -> new RoleNotFoundException(id));
    }


}
