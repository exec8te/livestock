package com.livestock.services;

import com.livestock.exceptions.BadRequestException;
import com.livestock.exceptions.RoleAlreadyExistsException;
import com.livestock.exceptions.RoleNotFoundException;
import com.livestock.mappers.RoleMapper;
import com.livestock.models.dtos.role.RoleCreateDto;
import com.livestock.models.dtos.role.RoleResponseDto;
import com.livestock.models.dtos.role.RoleUpdateDto;
import com.livestock.models.entities.Role;
import com.livestock.repositories.RoleRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;
    private final PasswordEncoder passwordEncoder;

    public RoleResponseDto getRoleById(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

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

        if (!roleMapper.hasChanges(dto, role)) {
            throw new BadRequestException("No changes were made");
        }

        roleMapper.updateEntity(dto, role);

        return roleMapper.toResponseDto(role);
    }

    @Transactional
    public void deleteRole(Long roleId) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new RoleNotFoundException(roleId));

        roleRepository.delete(role);
    }


}
